package com.risktracker.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.risktracker.model.Risk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Ollama AI Service
 * 
 * Integrates with local Ollama instance for AI-assisted risk mitigation recommendations.
 * This service calls the Ollama API running locally on port 11434.
 * 
 * IMPORTANT: This is for demonstration purposes. The application continues
 * to work even if Ollama is unavailable - deterministic risk scoring is independent.
 */
@Service
public class OllamaService {
    
    @Value("${ollama.url}")
    private String ollamaUrl;
    
    @Value("${ollama.model}")
    private String ollamaModel;
    
    @Value("${ollama.timeout}")
    private long timeout;
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public OllamaService() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Generate AI-powered risk mitigation recommendation
     * 
     * @param risk The risk object
     * @param projectName The project name for context
     * @return AI-generated mitigation recommendation
     */
    public String generateMitigationRecommendation(Risk risk, String projectName) {
        try {
            String prompt = buildPrompt(risk, projectName);
            return callOllama(prompt);
        } catch (Exception e) {
            System.err.println("Ollama service unavailable: " + e.getMessage());
            return "AI service is currently unavailable. Please use traditional risk analysis.";
        }
    }
    
    /**
     * Build prompt for Ollama
     */
    private String buildPrompt(Risk risk, String projectName) {
        return String.format("""
                You are a software project risk management expert.
                Analyze this project risk and provide concise, actionable mitigation recommendations.
                
                Project: %s
                Risk: %s
                Description: %s
                Category: %s
                Probability (1-5): %d
                Impact (1-5): %d
                Risk Score: %d
                Risk Level: %s
                
                Provide a brief, professional response with:
                1. Possible causes (2-3 points)
                2. Immediate mitigation actions (3-4 points)
                3. Preventive measures (2-3 points)
                
                Keep the response concise, practical, and under 300 words.
                """,
                projectName,
                risk.getTitle(),
                risk.getDescription() != null ? risk.getDescription() : "No description",
                risk.getCategory(),
                risk.getProbability(),
                risk.getImpact(),
                risk.getRiskScore(),
                risk.getRiskLevel()
        );
    }
    
    /**
     * Call Ollama API
     */
    private String callOllama(String prompt) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", ollamaModel);
        request.put("prompt", prompt);
        request.put("stream", false);
        
        try {
            String response = webClient.post()
                    .uri(ollamaUrl)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();
            
            JsonNode jsonResponse = objectMapper.readTree(response);
            return jsonResponse.get("response").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get AI response: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if Ollama service is available
     */
    public boolean isAvailable() {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("model", ollamaModel);
            request.put("prompt", "test");
            request.put("stream", false);
            
            webClient.post()
                    .uri(ollamaUrl)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
