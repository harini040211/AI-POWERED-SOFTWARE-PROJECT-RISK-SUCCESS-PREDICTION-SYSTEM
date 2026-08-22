package com.risktracker.controller;

import com.risktracker.dto.ApiResponse;
import com.risktracker.model.Resource;
import com.risktracker.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resource Controller
 * REST API endpoints for resource management
 */
@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = "*")
public class ResourceController {
    
    @Autowired
    private ResourceService resourceService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Resource>>> getAllResources() {
        List<Resource> resources = resourceService.getAllResources();
        return ResponseEntity.ok(ApiResponse.success(resources));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Resource>> getResourceById(@PathVariable Long id) {
        Resource resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(ApiResponse.success(resource));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Resource>> createResource(@Valid @RequestBody Resource resource) {
        Resource created = resourceService.createResource(resource);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resource created successfully", created));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Resource>> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody Resource resource) {
        Resource updated = resourceService.updateResource(id, resource);
        return ResponseEntity.ok(ApiResponse.success("Resource updated successfully", updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok(ApiResponse.success("Resource deleted successfully", null));
    }
    
    @GetMapping("/overloaded")
    public ResponseEntity<ApiResponse<List<Resource>>> getOverloadedResources() {
        List<Resource> resources = resourceService.getOverloadedResources();
        return ResponseEntity.ok(ApiResponse.success(resources));
    }
    
    @GetMapping("/utilization/average")
    public ResponseEntity<ApiResponse<BigDecimal>> getAverageUtilization() {
        BigDecimal avg = resourceService.getAverageUtilization();
        return ResponseEntity.ok(ApiResponse.success(avg));
    }
}
