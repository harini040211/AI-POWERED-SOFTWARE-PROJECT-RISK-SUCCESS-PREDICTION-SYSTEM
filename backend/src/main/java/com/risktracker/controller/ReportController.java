package com.risktracker.controller;

import com.risktracker.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Report Controller
 * REST API endpoints for report generation
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    @GetMapping("/project/{projectId}")
    public ResponseEntity<byte[]> generateProjectReport(@PathVariable Long projectId) {
        byte[] pdfBytes = reportService.generateProjectReport(projectId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
                "project-risk-report-" + projectId + ".pdf");
        headers.setContentLength(pdfBytes.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
    
    @GetMapping("/executive-summary")
    public ResponseEntity<byte[]> generateExecutiveSummary() {
        byte[] pdfBytes = reportService.generateExecutiveSummary();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
                "executive-summary-" + System.currentTimeMillis() + ".pdf");
        headers.setContentLength(pdfBytes.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
    
    @GetMapping("/risk-assessment")
    public ResponseEntity<byte[]> generateRiskAssessment() {
        byte[] pdfBytes = reportService.generateRiskAssessment();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
                "risk-assessment-" + System.currentTimeMillis() + ".pdf");
        headers.setContentLength(pdfBytes.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
    
    @GetMapping("/resource-utilization")
    public ResponseEntity<byte[]> generateResourceUtilization() {
        byte[] pdfBytes = reportService.generateResourceUtilization();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
                "resource-utilization-" + System.currentTimeMillis() + ".pdf");
        headers.setContentLength(pdfBytes.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
    
    @GetMapping("/financial-analysis")
    public ResponseEntity<byte[]> generateFinancialAnalysis() {
        byte[] pdfBytes = reportService.generateFinancialAnalysis();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
                "financial-analysis-" + System.currentTimeMillis() + ".pdf");
        headers.setContentLength(pdfBytes.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
