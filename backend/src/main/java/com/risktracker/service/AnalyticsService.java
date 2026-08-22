package com.risktracker.service;

import com.risktracker.dto.DashboardStats;
import com.risktracker.model.Project;
import com.risktracker.repository.ProjectRepository;
import com.risktracker.repository.ResourceRepository;
import com.risktracker.repository.RiskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Analytics Service
 * 
 * Calculates dashboard statistics and metrics
 */
@Service
public class AnalyticsService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private RiskRepository riskRepository;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    /**
     * Get comprehensive dashboard statistics
     */
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        
        stats.setTotalProjects(projectRepository.count());
        stats.setActiveProjects(projectRepository.countActiveProjects());
        stats.setHighRisks(riskRepository.countHighRisks());
        stats.setAverageRiskScore(riskRepository.getAverageRiskScore());
        stats.setResourceUtilization(getResourceUtilization());
        stats.setBudgetUtilization(getBudgetUtilization());
        stats.setRiskDistribution(getRiskDistribution());
        stats.setRiskByCategory(getRiskByCategory());
        
        // Calculate average project health
        List<Project> projects = projectRepository.findAll();
        int avgHealth = projects.isEmpty() ? 100 : 
                (int) projects.stream().mapToInt(Project::getHealthScore).average().orElse(100);
        stats.setProjectHealthScore(avgHealth);
        
        return stats;
    }
    
    /**
     * Calculate resource utilization
     */
    private BigDecimal getResourceUtilization() {
        BigDecimal avg = resourceRepository.getAverageUtilization();
        return avg != null ? avg.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }
    
    /**
     * Calculate budget utilization
     */
    private BigDecimal getBudgetUtilization() {
        List<Project> activeProjects = projectRepository.findByStatus(Project.ProjectStatus.ACTIVE);
        
        if (activeProjects.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalPlanned = activeProjects.stream()
                .map(p -> p.getPlannedBudget() != null ? p.getPlannedBudget() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalActual = activeProjects.stream()
                .map(p -> p.getActualCost() != null ? p.getActualCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalPlanned.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return totalActual.divide(totalPlanned, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Get risk distribution by level
     */
    private Map<String, Long> getRiskDistribution() {
        Map<String, Long> distribution = new HashMap<>();
        distribution.put("LOW", 0L);
        distribution.put("MEDIUM", 0L);
        distribution.put("HIGH", 0L);
        
        List<Object[]> results = riskRepository.countByRiskLevel();
        for (Object[] result : results) {
            String level = result[0].toString();
            Long count = ((Number) result[1]).longValue();
            distribution.put(level, count);
        }
        
        return distribution;
    }
    
    /**
     * Get risk count by category
     */
    private Map<String, Long> getRiskByCategory() {
        Map<String, Long> categoryMap = new HashMap<>();
        
        List<Object[]> results = riskRepository.countByCategory();
        for (Object[] result : results) {
            String category = result[0].toString();
            Long count = ((Number) result[1]).longValue();
            categoryMap.put(category, count);
        }
        
        return categoryMap;
    }
}
