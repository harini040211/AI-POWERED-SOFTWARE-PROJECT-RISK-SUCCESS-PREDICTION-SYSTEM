package com.risktracker.dto;

/**
 * Risk Prediction DTO
 * Contains ML prediction results
 */
public class RiskPrediction {
    private String predictedRiskLevel;
    private Double predictionProbability;
    private String explanation;
    
    public RiskPrediction() {}
    
    public RiskPrediction(String predictedRiskLevel, Double predictionProbability, String explanation) {
        this.predictedRiskLevel = predictedRiskLevel;
        this.predictionProbability = predictionProbability;
        this.explanation = explanation;
    }
    
    // Getters and Setters
    public String getPredictedRiskLevel() { return predictedRiskLevel; }
    public void setPredictedRiskLevel(String predictedRiskLevel) { 
        this.predictedRiskLevel = predictedRiskLevel; 
    }
    
    public Double getPredictionProbability() { return predictionProbability; }
    public void setPredictionProbability(Double predictionProbability) { 
        this.predictionProbability = predictionProbability; 
    }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
