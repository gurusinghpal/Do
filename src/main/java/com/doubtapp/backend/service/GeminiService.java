package com.doubtapp.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeminiService {
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    public String generateAnswer(String question) {
        try {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new RuntimeException("Gemini API key is not configured");
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String prompt = String.format(
                "You are a helpful teaching assistant. Please provide a clear and concise answer to the following question. " +
                "Focus on the key points and avoid unnecessary explanations. " +
                "If the question is about code, provide a brief explanation followed by the code example. " +
                "Keep your response under 200 words.\n\n" +
                "Question: %s",
                question
            );

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            content.put("parts", List.of(part));
            requestBody.put("contents", List.of(content));

            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                GEMINI_API_URL + apiKey,
                HttpMethod.POST,
                request,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody.containsKey("candidates")) {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                    if (!candidates.isEmpty()) {
                        Map<String, Object> candidate = candidates.get(0);
                        Map<String, Object> content2 = (Map<String, Object>) candidate.get("content");
                        List<Map<String, String>> parts = (List<Map<String, String>>) content2.get("parts");
                        if (!parts.isEmpty()) {
                            return parts.get(0).get("text");
                        }
                    }
                }
                throw new RuntimeException("Invalid response format from Gemini API");
            }
            throw new RuntimeException("Error response from Gemini API: " + response.getStatusCode());
        } catch (Exception e) {
            logger.error("Failed to generate answer: {}", e.getMessage());
            throw new RuntimeException("Failed to generate answer: " + e.getMessage());
        }
    }
} 