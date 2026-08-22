package com.risktracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class
 * 
 * AI-Assisted Software Project Risk Prediction and Mitigation System
 * 
 * This is a final-year student project demonstrating:
 * - Spring Boot REST API development
 * - MySQL database integration
 * - Deterministic risk scoring
 * - Basic ML-based risk prediction
 * - Local AI integration with Ollama
 * - PDF report generation
 * 
 * @author Student Name
 * @version 1.0.0
 */
@SpringBootApplication
public class RiskTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskTrackerApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("AI Risk Tracker Application Started");
        System.out.println("Server: http://localhost:8080");
        System.out.println("========================================\n");
    }
}
