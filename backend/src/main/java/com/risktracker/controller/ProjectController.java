package com.risktracker.controller;

import com.risktracker.dto.ApiResponse;
import com.risktracker.dto.RiskPrediction;
import com.risktracker.model.Project;
import com.risktracker.service.ProjectService;
import com.risktracker.service.RiskPredictionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Project Controller
 * REST API endpoints for project management
 */
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private RiskPredictionService riskPredictionService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Project>>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(ApiResponse.success(projects));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Project>> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success(project));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Project>> createProject(@Valid @RequestBody Project project) {
        Project created = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created successfully", created));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Project>> updateProject(
            @PathVariable Long id, 
            @Valid @RequestBody Project project) {
        Project updated = projectService.updateProject(id, project);
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully", null));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Project>>> searchProjects(@RequestParam String keyword) {
        List<Project> projects = projectService.searchProjects(keyword);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }
    
    @GetMapping("/{id}/predict-risk")
    public ResponseEntity<ApiResponse<RiskPrediction>> predictProjectRisk(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        RiskPrediction prediction = riskPredictionService.predictProjectRisk(project);
        return ResponseEntity.ok(ApiResponse.success(prediction));
    }
}
