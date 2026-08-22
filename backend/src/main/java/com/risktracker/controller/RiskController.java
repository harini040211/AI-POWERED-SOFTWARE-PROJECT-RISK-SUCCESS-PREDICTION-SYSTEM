package com.risktracker.controller;

import com.risktracker.dto.ApiResponse;
import com.risktracker.model.Risk;
import com.risktracker.service.RiskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Risk Controller
 * REST API endpoints for risk management
 */
@RestController
@RequestMapping("/api/risks")
@CrossOrigin(origins = "*")
public class RiskController {
    
    @Autowired
    private RiskService riskService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Risk>>> getAllRisks() {
        List<Risk> risks = riskService.getAllRisks();
        return ResponseEntity.ok(ApiResponse.success(risks));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Risk>> getRiskById(@PathVariable Long id) {
        Risk risk = riskService.getRiskById(id);
        return ResponseEntity.ok(ApiResponse.success(risk));
    }
    
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<Risk>>> getRisksByProject(@PathVariable Long projectId) {
        List<Risk> risks = riskService.getRisksByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(risks));
    }
    
    @PostMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<Risk>> createRisk(
            @PathVariable Long projectId,
            @Valid @RequestBody Risk risk) {
        Risk created = riskService.createRisk(projectId, risk);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Risk created successfully", created));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Risk>> updateRisk(
            @PathVariable Long id,
            @Valid @RequestBody Risk risk) {
        Risk updated = riskService.updateRisk(id, risk);
        return ResponseEntity.ok(ApiResponse.success("Risk updated successfully", updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRisk(@PathVariable Long id) {
        riskService.deleteRisk(id);
        return ResponseEntity.ok(ApiResponse.success("Risk deleted successfully", null));
    }
    
    @GetMapping("/high-severity")
    public ResponseEntity<ApiResponse<List<Risk>>> getHighSeverityRisks() {
        List<Risk> risks = riskService.getHighSeverityRisks();
        return ResponseEntity.ok(ApiResponse.success(risks));
    }
    
    @PostMapping("/{id}/regenerate-ai")
    public ResponseEntity<ApiResponse<Risk>> regenerateAI(@PathVariable Long id) {
        Risk risk = riskService.regenerateAIRecommendation(id);
        return ResponseEntity.ok(ApiResponse.success("AI recommendation regenerated", risk));
    }
}
