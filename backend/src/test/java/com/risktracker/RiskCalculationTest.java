package com.risktracker;

import com.risktracker.model.Risk;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for Risk Calculation Logic
 * 
 * These tests verify the deterministic risk scoring algorithm:
 * - Risk Score = Probability × Impact
 * - Classification: 1-5=LOW, 6-12=MEDIUM, 13-25=HIGH
 */
public class RiskCalculationTest {
    
    @Test
    public void testLowRiskCalculation() {
        Risk risk = new Risk();
        risk.setProbability(2);
        risk.setImpact(2);
        risk.calculateRiskScore();
        
        assertEquals(4, risk.getRiskScore());
        assertEquals(Risk.RiskLevel.LOW, risk.getRiskLevel());
    }
    
    @Test
    public void testMediumRiskCalculation() {
        Risk risk = new Risk();
        risk.setProbability(3);
        risk.setImpact(3);
        risk.calculateRiskScore();
        
        assertEquals(9, risk.getRiskScore());
        assertEquals(Risk.RiskLevel.MEDIUM, risk.getRiskLevel());
    }
    
    @Test
    public void testHighRiskCalculation() {
        Risk risk = new Risk();
        risk.setProbability(4);
        risk.setImpact(4);
        risk.calculateRiskScore();
        
        assertEquals(16, risk.getRiskScore());
        assertEquals(Risk.RiskLevel.HIGH, risk.getRiskLevel());
    }
    
    @Test
    public void testMaximumRiskScore() {
        Risk risk = new Risk();
        risk.setProbability(5);
        risk.setImpact(5);
        risk.calculateRiskScore();
        
        assertEquals(25, risk.getRiskScore());
        assertEquals(Risk.RiskLevel.HIGH, risk.getRiskLevel());
    }
    
    @Test
    public void testBoundaryLowToMedium() {
        Risk risk1 = new Risk();
        risk1.setProbability(1);
        risk1.setImpact(5);
        risk1.calculateRiskScore();
        
        assertEquals(5, risk1.getRiskScore());
        assertEquals(Risk.RiskLevel.LOW, risk1.getRiskLevel());
        
        Risk risk2 = new Risk();
        risk2.setProbability(2);
        risk2.setImpact(3);
        risk2.calculateRiskScore();
        
        assertEquals(6, risk2.getRiskScore());
        assertEquals(Risk.RiskLevel.MEDIUM, risk2.getRiskLevel());
    }
    
    @Test
    public void testBoundaryMediumToHigh() {
        Risk risk1 = new Risk();
        risk1.setProbability(3);
        risk1.setImpact(4);
        risk1.calculateRiskScore();
        
        assertEquals(12, risk1.getRiskScore());
        assertEquals(Risk.RiskLevel.MEDIUM, risk1.getRiskLevel());
        
        Risk risk2 = new Risk();
        risk2.setProbability(4);
        risk2.setImpact(4);
        risk2.calculateRiskScore();
        
        assertEquals(16, risk2.getRiskScore());
        assertEquals(Risk.RiskLevel.HIGH, risk2.getRiskLevel());
    }
}
