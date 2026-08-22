package com.risktracker.service;

import com.risktracker.ai.OllamaService;
import com.risktracker.exception.ResourceNotFoundException;
import com.risktracker.model.Project;
import com.risktracker.model.Risk;
import com.risktracker.repository.ProjectRepository;
import com.risktracker.repository.RiskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Risk Service
 * 
 * Business logic for risk management including:
 * - CRUD operations
 * - Deterministic risk scoring (probability × impact)
 * - AI-assisted mitigation recommendations via Ollama
 */
@Service
@Transactional
public class RiskService {
    
    @Autowired
    private RiskRepository riskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private OllamaService ollamaService;
    
    @Autowired
    private ProjectService projectService;
    
    /**
     * Get all risks
     */
    public List<Risk> getAllRisks() {
        return riskRepository.findAll();
    }
    
    /**
     * Get risk by ID
     */
    public Risk getRiskById(Long id) {
        return riskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Risk not found with id: " + id));
    }
    
    /**
     * Get risks by project
     */
    public List<Risk> getRisksByProject(Long projectId) {
        return riskRepository.findByProjectId(projectId);
    }
    
    /**
     * Create new risk with AI recommendation
     */
    public Risk createRisk(Long projectId, Risk risk) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        risk.setProject(project);
        
        // Save risk first (this calculates risk score automatically via @PrePersist)
        Risk savedRisk = riskRepository.save(risk);
        
        // Try to generate AI recommendation
        try {
            String aiRecommendation = ollamaService.generateMitigationRecommendation(
                    savedRisk, project.getProjectName());
            savedRisk.setAiRecommendation(aiRecommendation);
            savedRisk = riskRepository.save(savedRisk);
        } catch (Exception e) {
            System.err.println("Could not generate AI recommendation: " + e.getMessage());
            savedRisk.setAiRecommendation("AI service unavailable. Use traditional risk analysis.");
        }
        
        // Update project health after adding risk
        projectService.calculateProjectHealth(project);
        projectRepository.save(project);
        
        return savedRisk;
    }
    
    /**
     * Update risk
     */
    public Risk updateRisk(Long id, Risk riskDetails) {
        Risk risk = getRiskById(id);
        Project project = risk.getProject();
        
        risk.setTitle(riskDetails.getTitle());
        risk.setDescription(riskDetails.getDescription());
        risk.setCategory(riskDetails.getCategory());
        risk.setProbability(riskDetails.getProbability());
        risk.setImpact(riskDetails.getImpact());
        risk.setStatus(riskDetails.getStatus());
        risk.setMitigation(riskDetails.getMitigation());
        
        Risk savedRisk = riskRepository.save(risk);
        
        // Update project health
        projectService.calculateProjectHealth(project);
        projectRepository.save(project);
        
        return savedRisk;
    }
    
    /**
     * Delete risk
     */
    public void deleteRisk(Long id) {
        Risk risk = getRiskById(id);
        Project project = risk.getProject();
        
        riskRepository.delete(risk);
        
        // Update project health after removal
        projectService.calculateProjectHealth(project);
        projectRepository.save(project);
    }
    
    /**
     * Get high severity risks
     */
    public List<Risk> getHighSeverityRisks() {
        return riskRepository.findByRiskLevel(Risk.RiskLevel.HIGH).stream()
                .filter(r -> r.getStatus() != Risk.RiskStatus.CLOSED)
                .toList();
    }
    
    /**
     * Regenerate AI recommendation for a risk
     */
    public Risk regenerateAIRecommendation(Long id) {
        Risk risk = getRiskById(id);
        Project project = risk.getProject();
        
        try {
            String aiRecommendation = ollamaService.generateMitigationRecommendation(
                    risk, project.getProjectName());
            risk.setAiRecommendation(aiRecommendation);
            return riskRepository.save(risk);
        } catch (Exception e) {
            throw new RuntimeException("AI service is currently unavailable");
        }
    }
}
