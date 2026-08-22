package com.risktracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Project Entity
 * Represents a software development project
 */
@Entity
@Table(name = "projects")
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Project name is required")
    @Column(nullable = false)
    private String projectName;
    
    @NotBlank(message = "Project code is required")
    @Column(unique = true, nullable = false)
    private String projectCode;
    
    @Column(length = 2000)
    private String description;
    
    private String clientName;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "Expected end date is required")
    private LocalDate expectedEndDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.PLANNED;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;
    
    @NotNull(message = "Planned budget is required")
    @DecimalMin(value = "0.0", message = "Budget must be positive")
    @Column(precision = 15, scale = 2)
    private BigDecimal plannedBudget;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal actualCost = BigDecimal.ZERO;
    
    @Min(value = 0, message = "Completion must be between 0 and 100")
    @Max(value = 100, message = "Completion must be between 0 and 100")
    private Integer completionPercentage = 0;
    
    // Project Health Score (0-100)
    private Integer healthScore = 100;
    
    @Enumerated(EnumType.STRING)
    private HealthStatus healthStatus = HealthStatus.HEALTHY;
    
    // ML Prediction Fields
    private String predictedRiskLevel;
    private Double predictionProbability;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Risk> risks = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Enums
    public enum ProjectStatus {
        PLANNED, ACTIVE, ON_HOLD, COMPLETED
    }
    
    public enum Priority {
        LOW, MEDIUM, HIGH
    }
    
    public enum HealthStatus {
        HEALTHY, WARNING, CRITICAL
    }
    
    // Constructors
    public Project() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    
    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getExpectedEndDate() { return expectedEndDate; }
    public void setExpectedEndDate(LocalDate expectedEndDate) { this.expectedEndDate = expectedEndDate; }
    
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    
    public BigDecimal getPlannedBudget() { return plannedBudget; }
    public void setPlannedBudget(BigDecimal plannedBudget) { this.plannedBudget = plannedBudget; }
    
    public BigDecimal getActualCost() { return actualCost; }
    public void setActualCost(BigDecimal actualCost) { this.actualCost = actualCost; }
    
    public Integer getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(Integer completionPercentage) { 
        this.completionPercentage = completionPercentage; 
    }
    
    public Integer getHealthScore() { return healthScore; }
    public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }
    
    public HealthStatus getHealthStatus() { return healthStatus; }
    public void setHealthStatus(HealthStatus healthStatus) { this.healthStatus = healthStatus; }
    
    public String getPredictedRiskLevel() { return predictedRiskLevel; }
    public void setPredictedRiskLevel(String predictedRiskLevel) { 
        this.predictedRiskLevel = predictedRiskLevel; 
    }
    
    public Double getPredictionProbability() { return predictionProbability; }
    public void setPredictionProbability(Double predictionProbability) { 
        this.predictionProbability = predictionProbability; 
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    public List<Risk> getRisks() { return risks; }
    public void setRisks(List<Risk> risks) { this.risks = risks; }
}
