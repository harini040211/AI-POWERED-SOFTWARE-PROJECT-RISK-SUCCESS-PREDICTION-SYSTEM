package com.risktracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Resource Entity
 * Represents a team member/employee resource
 */
@Entity
@Table(name = "resources")
public class Resource {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Employee name is required")
    @Column(nullable = false)
    private String employeeName;
    
    @NotBlank(message = "Role is required")
    private String role;
    
    @NotNull(message = "Available hours is required")
    @DecimalMin(value = "0.0", message = "Available hours must be positive")
    @Column(precision = 10, scale = 2)
    private BigDecimal availableHours;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal assignedHours = BigDecimal.ZERO;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal utilizationPercentage = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    private UtilizationStatus utilizationStatus = UtilizationStatus.OPTIMAL;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        calculateUtilization();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        calculateUtilization();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Calculate Resource Utilization
     * Formula: utilization = (assignedHours / availableHours) * 100
     * Classification:
     * - 0-70%: UNDERUTILIZED
     * - 71-90%: OPTIMAL
     * - 91-100%: HIGH
     * - >100%: OVERLOADED
     */
    public void calculateUtilization() {
        if (availableHours != null && availableHours.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal hours = assignedHours != null ? assignedHours : BigDecimal.ZERO;
            this.utilizationPercentage = hours
                .multiply(BigDecimal.valueOf(100))
                .divide(availableHours, 2, RoundingMode.HALF_UP);
            
            int util = utilizationPercentage.intValue();
            if (util <= 70) {
                this.utilizationStatus = UtilizationStatus.UNDERUTILIZED;
            } else if (util <= 90) {
                this.utilizationStatus = UtilizationStatus.OPTIMAL;
            } else if (util <= 100) {
                this.utilizationStatus = UtilizationStatus.HIGH;
            } else {
                this.utilizationStatus = UtilizationStatus.OVERLOADED;
            }
        }
    }
    
    // Enum
    public enum UtilizationStatus {
        UNDERUTILIZED, OPTIMAL, HIGH, OVERLOADED
    }
    
    // Constructors
    public Resource() {}
    
    public Resource(String employeeName, String role, BigDecimal availableHours) {
        this.employeeName = employeeName;
        this.role = role;
        this.availableHours = availableHours;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public BigDecimal getAvailableHours() { return availableHours; }
    public void setAvailableHours(BigDecimal availableHours) { 
        this.availableHours = availableHours;
        calculateUtilization();
    }
    
    public BigDecimal getAssignedHours() { return assignedHours; }
    public void setAssignedHours(BigDecimal assignedHours) { 
        this.assignedHours = assignedHours;
        calculateUtilization();
    }
    
    public BigDecimal getUtilizationPercentage() { return utilizationPercentage; }
    public void setUtilizationPercentage(BigDecimal utilizationPercentage) { 
        this.utilizationPercentage = utilizationPercentage; 
    }
    
    public UtilizationStatus getUtilizationStatus() { return utilizationStatus; }
    public void setUtilizationStatus(UtilizationStatus utilizationStatus) { 
        this.utilizationStatus = utilizationStatus; 
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
