package com.risktracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Risk Entity
 * Represents a project risk with deterministic scoring
 */
@Entity
@Table(name = "risks")
public class Risk {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;
    
    @NotBlank(message = "Risk title is required")
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskCategory category;
    
    @NotNull(message = "Probability is required")
    @Min(value = 1, message = "Probability must be between 1 and 5")
    @Max(value = 5, message = "Probability must be between 1 and 5")
    @Column(nullable = false)
    private Integer probability;
    
    @NotNull(message = "Impact is required")
    @Min(value = 1, message = "Impact must be between 1 and 5")
    @Max(value = 5, message = "Impact must be between 1 and 5")
    @Column(nullable = false)
    private Integer impact;
    
    // Calculated fields
    @Column(nullable = false)
    private Integer riskScore;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskStatus status = RiskStatus.OPEN;
    
    @Column(length = 2000)
    private String mitigation;
    
    @Column(length = 5000)
    private String aiRecommendation;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        calculateRiskScore();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        calculateRiskScore();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Calculate Risk Score and Level
     * Formula: riskScore = probability × impact
     * Classification:
     * - 1-5: LOW
     * - 6-12: MEDIUM
     * - 13-25: HIGH
     */
    public void calculateRiskScore() {
        if (probability != null && impact != null) {
            this.riskScore = probability * impact;
            
            if (riskScore <= 5) {
                this.riskLevel = RiskLevel.LOW;
            } else if (riskScore <= 12) {
                this.riskLevel = RiskLevel.MEDIUM;
            } else {
                this.riskLevel = RiskLevel.HIGH;
            }
        }
    }
    
    // Enums
    public enum RiskCategory {
        TECHNICAL, RESOURCE, FINANCIAL, REQUIREMENT, 
        SECURITY, SCHEDULE, OPERATIONAL
    }
    
    public enum RiskLevel {
        LOW, MEDIUM, HIGH
    }
    
    public enum RiskStatus {
        OPEN, IN_PROGRESS, MITIGATED, CLOSED
    }
    
    // Constructors
    public Risk() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public RiskCategory getCategory() { return category; }
    public void setCategory(RiskCategory category) { this.category = category; }
    
    public Integer getProbability() { return probability; }
    public void setProbability(Integer probability) { 
        this.probability = probability;
        calculateRiskScore();
    }
    
    public Integer getImpact() { return impact; }
    public void setImpact(Integer impact) { 
        this.impact = impact;
        calculateRiskScore();
    }
    
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    
    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    
    public RiskStatus getStatus() { return status; }
    public void setStatus(RiskStatus status) { this.status = status; }
    
    public String getMitigation() { return mitigation; }
    public void setMitigation(String mitigation) { this.mitigation = mitigation; }
    
    public String getAiRecommendation() { return aiRecommendation; }
    public void setAiRecommendation(String aiRecommendation) { 
        this.aiRecommendation = aiRecommendation; 
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
