package com.risktracker.service;

import com.risktracker.exception.ResourceNotFoundException;
import com.risktracker.model.Resource;
import com.risktracker.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resource Service
 * 
 * Business logic for resource/employee management and utilization tracking.
 */
@Service
@Transactional
public class ResourceService {
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    /**
     * Get all resources
     */
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }
    
    /**
     * Get resource by ID
     */
    public Resource getResourceById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
    }
    
    /**
     * Create resource
     */
    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }
    
    /**
     * Update resource
     */
    public Resource updateResource(Long id, Resource resourceDetails) {
        Resource resource = getResourceById(id);
        
        resource.setEmployeeName(resourceDetails.getEmployeeName());
        resource.setRole(resourceDetails.getRole());
        resource.setAvailableHours(resourceDetails.getAvailableHours());
        resource.setAssignedHours(resourceDetails.getAssignedHours());
        
        return resourceRepository.save(resource);
    }
    
    /**
     * Delete resource
     */
    public void deleteResource(Long id) {
        Resource resource = getResourceById(id);
        resourceRepository.delete(resource);
    }
    
    /**
     * Get overloaded resources
     */
    public List<Resource> getOverloadedResources() {
        return resourceRepository.findByUtilizationStatus(Resource.UtilizationStatus.OVERLOADED);
    }
    
    /**
     * Get underutilized resources
     */
    public List<Resource> getUnderutilizedResources() {
        return resourceRepository.findByUtilizationStatus(Resource.UtilizationStatus.UNDERUTILIZED);
    }
    
    /**
     * Get average utilization
     */
    public BigDecimal getAverageUtilization() {
        BigDecimal avg = resourceRepository.getAverageUtilization();
        return avg != null ? avg : BigDecimal.ZERO;
    }
}
