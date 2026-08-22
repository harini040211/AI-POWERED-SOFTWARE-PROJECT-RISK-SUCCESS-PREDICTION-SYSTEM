package com.risktracker.service;

import com.risktracker.exception.ResourceNotFoundException;
import com.risktracker.model.Project;
import com.risktracker.model.Risk;
import com.risktracker.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Project Service
 * 
 * Business logic for project management and project health calculation.
 */
@Service
@Transactional
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    /**
     * Get all projects
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
    
    /**
     * Get project by ID
     */
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }
    
    /**
     * Create new project
     */
    public Project createProject(Project project) {
        if (project.getProjectCode() != null && projectRepository.findByProjectCode(project.getProjectCode()).isPresent()) {
            throw new IllegalArgumentException("Project code already exists");
        }
        
        calculateProjectHealth(project);
        return projectRepository.save(project);
    }
    
    /**
     * Update project
     */
    public Project updateProject(Long id, Project projectDetails) {
        Project project = getProjectById(id);
        
        project.setProjectName(projectDetails.getProjectName());
        project.setDescription(projectDetails.getDescription());
        project.setClientName(projectDetails.getClientName());
        project.setStartDate(projectDetails.getStartDate());
        project.setExpectedEndDate(projectDetails.getExpectedEndDate());
        project.setStatus(projectDetails.getStatus());
        project.setPriority(projectDetails.getPriority());
        project.setPlannedBudget(projectDetails.getPlannedBudget());
        project.setActualCost(projectDetails.getActualCost());
        project.setCompletionPercentage(projectDetails.getCompletionPercentage());
        
        calculateProjectHealth(project);
        return projectRepository.save(project);
    }
    
    /**
     * Delete project
     */
    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        projectRepository.delete(project);
    }
    
    /**
     * Calculate Project Health Score
     * 
     * This is a DETERMINISTIC calculation based on multiple factors:
     * - High risks count (-20 points per high risk, max -40)
     * - Budget overrun (-30 points if over budget, -15 if >90% used)
     * - Schedule delay (-20 points if delayed)
     * - Completion bonus (+10 if >= 80% complete)
     * 
     * Score Range: 0-100
     * Classification:
     * - 80-100: HEALTHY
     * - 60-79: WARNING
     * - 0-59: CRITICAL
     */
    public void calculateProjectHealth(Project project) {
        int healthScore = 100;
        
        // Factor 1: High Risks (-20 per high risk, max -40)
        long highRisks = project.getRisks() != null ? project.getRisks().stream()
                .filter(r -> r.getRiskLevel() == Risk.RiskLevel.HIGH && 
                            r.getStatus() != Risk.RiskStatus.CLOSED)
                .count() : 0;
        healthScore -= Math.min(highRisks * 20, 40);
        
        // Factor 2: Budget Utilization
        if (project.getPlannedBudget() != null && project.getActualCost() != null && project.getPlannedBudget().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal budgetUtil = project.getActualCost()
                    .divide(project.getPlannedBudget(), 2, RoundingMode.HALF_UP);
            
            if (budgetUtil.compareTo(BigDecimal.ONE) > 0) {
                healthScore -= 30; // Over budget
            } else if (budgetUtil.compareTo(BigDecimal.valueOf(0.9)) > 0) {
                healthScore -= 15; // Warning level
            }
        }
        
        // Factor 3: Schedule Status
        LocalDate now = LocalDate.now();
        if (project.getExpectedEndDate() != null && 
            project.getExpectedEndDate().isBefore(now) && 
            project.getStatus() != Project.ProjectStatus.COMPLETED) {
            healthScore -= 20; // Delayed
        }
        
        // Factor 4: Completion Bonus
        if (project.getCompletionPercentage() >= 80) {
            healthScore += 10;
        }
        
        // Ensure score is between 0 and 100
        healthScore = Math.max(0, Math.min(100, healthScore));
        
        project.setHealthScore(healthScore);
        
        // Set health status based on score
        if (healthScore >= 80) {
            project.setHealthStatus(Project.HealthStatus.HEALTHY);
        } else if (healthScore >= 60) {
            project.setHealthStatus(Project.HealthStatus.WARNING);
        } else {
            project.setHealthStatus(Project.HealthStatus.CRITICAL);
        }
    }
    
    /**
     * Search projects
     */
    public List<Project> searchProjects(String keyword) {
        return projectRepository.findAll().stream()
                .filter(p -> p.getProjectName().toLowerCase().contains(keyword.toLowerCase()) ||
                            p.getProjectCode().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }
    
    /**
     * Get projects by status
     */
    public List<Project> getProjectsByStatus(Project.ProjectStatus status) {
        return projectRepository.findByStatus(status);
    }
}
