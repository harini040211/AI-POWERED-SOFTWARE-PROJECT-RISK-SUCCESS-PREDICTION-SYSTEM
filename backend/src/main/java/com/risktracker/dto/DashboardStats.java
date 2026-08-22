package com.risktracker.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Dashboard Statistics DTO
 * Contains all metrics for the main dashboard
 */
public class DashboardStats {
    private Long totalProjects;
    private Long activeProjects;
    private Long highRisks;
    private Double averageRiskScore;
    private Integer projectHealthScore;
    private BigDecimal resourceUtilization;
    private BigDecimal budgetUtilization;
    private Map<String, Long> riskDistribution;
    private Map<String, Long> riskByCategory;
    
    public DashboardStats() {}
    
    // Getters and Setters
    public Long getTotalProjects() { return totalProjects; }
    public void setTotalProjects(Long totalProjects) { this.totalProjects = totalProjects; }
    
    public Long getActiveProjects() { return activeProjects; }
    public void setActiveProjects(Long activeProjects) { this.activeProjects = activeProjects; }
    
    public Long getHighRisks() { return highRisks; }
    public void setHighRisks(Long highRisks) { this.highRisks = highRisks; }
    
    public Double getAverageRiskScore() { return averageRiskScore; }
    public void setAverageRiskScore(Double averageRiskScore) { 
        this.averageRiskScore = averageRiskScore; 
    }
    
    public Integer getProjectHealthScore() { return projectHealthScore; }
    public void setProjectHealthScore(Integer projectHealthScore) { 
        this.projectHealthScore = projectHealthScore; 
    }
    
    public BigDecimal getResourceUtilization() { return resourceUtilization; }
    public void setResourceUtilization(BigDecimal resourceUtilization) { 
        this.resourceUtilization = resourceUtilization; 
    }
    
    public BigDecimal getBudgetUtilization() { return budgetUtilization; }
    public void setBudgetUtilization(BigDecimal budgetUtilization) { 
        this.budgetUtilization = budgetUtilization; 
    }
    
    public Map<String, Long> getRiskDistribution() { return riskDistribution; }
    public void setRiskDistribution(Map<String, Long> riskDistribution) { 
        this.riskDistribution = riskDistribution; 
    }
    
    public Map<String, Long> getRiskByCategory() { return riskByCategory; }
    public void setRiskByCategory(Map<String, Long> riskByCategory) { 
        this.riskByCategory = riskByCategory; 
    }
}
