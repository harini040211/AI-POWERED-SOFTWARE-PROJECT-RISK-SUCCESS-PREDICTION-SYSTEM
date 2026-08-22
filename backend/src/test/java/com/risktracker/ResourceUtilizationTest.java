package com.risktracker;

import com.risktracker.model.Resource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for Resource Utilization Calculation
 * 
 * Tests verify the utilization formula and classification:
 * - Utilization = (assignedHours / availableHours) × 100
 * - 0-70%: UNDERUTILIZED
 * - 71-90%: OPTIMAL
 * - 91-100%: HIGH
 * - >100%: OVERLOADED
 */
public class ResourceUtilizationTest {
    
    @Test
    public void testUnderutilizedResource() {
        Resource resource = new Resource();
        resource.setAvailableHours(new BigDecimal("160"));
        resource.setAssignedHours(new BigDecimal("100"));
        resource.calculateUtilization();
        
        assertEquals(new BigDecimal("62.50"), resource.getUtilizationPercentage());
        assertEquals(Resource.UtilizationStatus.UNDERUTILIZED, resource.getUtilizationStatus());
    }
    
    @Test
    public void testOptimalResource() {
        Resource resource = new Resource();
        resource.setAvailableHours(new BigDecimal("160"));
        resource.setAssignedHours(new BigDecimal("130"));
        resource.calculateUtilization();
        
        assertEquals(new BigDecimal("81.25"), resource.getUtilizationPercentage());
        assertEquals(Resource.UtilizationStatus.OPTIMAL, resource.getUtilizationStatus());
    }
    
    @Test
    public void testHighUtilizationResource() {
        Resource resource = new Resource();
        resource.setAvailableHours(new BigDecimal("160"));
        resource.setAssignedHours(new BigDecimal("150"));
        resource.calculateUtilization();
        
        assertEquals(new BigDecimal("93.75"), resource.getUtilizationPercentage());
        assertEquals(Resource.UtilizationStatus.HIGH, resource.getUtilizationStatus());
    }
    
    @Test
    public void testOverloadedResource() {
        Resource resource = new Resource();
        resource.setAvailableHours(new BigDecimal("160"));
        resource.setAssignedHours(new BigDecimal("180"));
        resource.calculateUtilization();
        
        assertEquals(new BigDecimal("112.50"), resource.getUtilizationPercentage());
        assertEquals(Resource.UtilizationStatus.OVERLOADED, resource.getUtilizationStatus());
    }
    
    @Test
    public void testBoundaryUnderutilizedToOptimal() {
        Resource resource = new Resource();
        resource.setAvailableHours(new BigDecimal("100"));
        resource.setAssignedHours(new BigDecimal("71"));
        resource.calculateUtilization();
        
        assertEquals(new BigDecimal("71.00"), resource.getUtilizationPercentage());
        assertEquals(Resource.UtilizationStatus.OPTIMAL, resource.getUtilizationStatus());
    }
}
