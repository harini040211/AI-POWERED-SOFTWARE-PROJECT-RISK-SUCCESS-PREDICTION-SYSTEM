package com.risktracker.service;

import com.risktracker.dto.RiskPrediction;
import com.risktracker.model.Project;
import com.risktracker.model.Risk;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Risk Prediction Service
 * 
 * Implements a SIMPLE, EXPLAINABLE ML-based risk prediction model.
 * 
 * IMPORTANT FOR INTERVIEW:
 * This is a rule-based heuristic model (not a trained ML model) for demonstration purposes.
 * In a real production system, this would use a trained model with historical data.
 * 
 * The model considers:
 * - Budget utilization
 * - Completion percentage vs time elapsed
 * - High risk count
 * - Average risk score
 * 
 * Output:
 * - Predicted Risk Level: LOW, MEDIUM, HIGH
 * - Prediction Probability: 0-100%
 * 
 * This provides an "early warning" system separate from traditional risk scoring.
 */
@Service
public class RiskPredictionService {
    
    /**
     * Predict project risk level using simple explainable rules
     * 
     * @param project The project to analyze
     * @return Risk prediction with level and probability
     */
    public RiskPrediction predictProjectRisk(Project project) {
        int riskScore = 0;
        int maxScore = 100;
        
        // Factor 1: Budget Utilization (max 30 points)
        BigDecimal budgetUtil = BigDecimal.ZERO;
        if (project.getPlannedBudget() != null && project.getActualCost() != null && project.getPlannedBudget().compareTo(BigDecimal.ZERO) > 0) {
            budgetUtil = project.getActualCost()
                    .divide(project.getPlannedBudget(), 2, RoundingMode.HALF_UP);
            
            if (budgetUtil.compareTo(BigDecimal.ONE) > 0) {
                riskScore += 30; // Over budget
            } else if (budgetUtil.compareTo(BigDecimal.valueOf(0.9)) > 0) {
                riskScore += 20; // Near limit
            } else if (budgetUtil.compareTo(BigDecimal.valueOf(0.8)) > 0) {
                riskScore += 10;
            }
        }
        
        // Factor 2: Completion vs Timeline (max 25 points)
        // If we're 50% through timeline but only 30% complete, that's risky
        int completion = project.getCompletionPercentage();
        if (completion < 50) {
            riskScore += 15;
        } else if (completion < 70) {
            riskScore += 10;
        } else if (completion < 90) {
            riskScore += 5;
        }
        
        // Factor 3: High Risk Count (max 30 points)
        long highRiskCount = project.getRisks().stream()
                .filter(r -> r.getRiskLevel() == Risk.RiskLevel.HIGH && 
                            r.getStatus() != Risk.RiskStatus.CLOSED)
                .count();
        
        if (highRiskCount >= 3) {
            riskScore += 30;
        } else if (highRiskCount == 2) {
            riskScore += 20;
        } else if (highRiskCount == 1) {
            riskScore += 10;
        }
        
        // Factor 4: Average Risk Score (max 15 points)
        if (!project.getRisks().isEmpty()) {
            double avgRiskScore = project.getRisks().stream()
                    .filter(r -> r.getStatus() != Risk.RiskStatus.CLOSED)
                    .mapToInt(Risk::getRiskScore)
                    .average()
                    .orElse(0);
            
            if (avgRiskScore >= 15) {
                riskScore += 15;
            } else if (avgRiskScore >= 10) {
                riskScore += 10;
            } else if (avgRiskScore >= 6) {
                riskScore += 5;
            }
        }
        
        // Calculate probability as percentage
        double probability = (riskScore / (double) maxScore) * 100;
        probability = Math.min(99, probability); // Cap at 99% (never 100% certain)
        
        // Determine risk level
        String riskLevel;
        if (riskScore <= 30) {
            riskLevel = "LOW";
        } else if (riskScore <= 60) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "HIGH";
        }
        
        // Build explanation
        String explanation = buildExplanation(budgetUtil, completion, highRiskCount);
        
        return new RiskPrediction(riskLevel, probability, explanation);
    }
    
    /**
     * Build human-readable explanation
     */
    private String buildExplanation(BigDecimal budgetUtil, int completion, long highRiskCount) {
        StringBuilder sb = new StringBuilder("Prediction based on: ");
        
        if (budgetUtil.compareTo(BigDecimal.valueOf(0.9)) > 0) {
            sb.append("High budget utilization. ");
        }
        
        if (completion < 50) {
            sb.append("Low completion rate. ");
        }
        
        if (highRiskCount > 0) {
            sb.append(highRiskCount).append(" high-severity risk(s) present. ");
        }
        
        if (budgetUtil.compareTo(BigDecimal.valueOf(0.7)) < 0 && 
            completion > 70 && highRiskCount == 0) {
            sb.append("Project indicators are healthy.");
        }
        
        return sb.toString();
    }
}
