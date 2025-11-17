package com.fitness.aiservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getAnswer(String question) {
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{ Map.of("text", question) })
                }
        );

        String fullUrl = geminiApiUrl + "/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;

        try {
            return webClient.post()
                    .uri(fullUrl)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("Gemini API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "{\"error\":\"Gemini API error: " + e.getStatusCode() + "\"}";
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return "{\"error\":\"Unexpected exception occurred\"}";
        }
    }
}
