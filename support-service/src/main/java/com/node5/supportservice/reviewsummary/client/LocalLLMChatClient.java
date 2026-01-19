package com.node5.supportservice.reviewsummary.client;

import com.node5.supportservice.reviewsummary.client.dto.LocalLLMResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LocalLLMChatClient {
    private final RestTemplate llmRestTemplate;
    private static final String baseUrl = "http://localhost:11434";
    private static final String model = "qwen2.5:7b";

    public String reviewSummary(String systemPrompt){
        Map<String, Object> body = Map.of(
                "model", model,
                "prompt", systemPrompt,
                "stream", false,
                "num_predict", 160,
                "num_ctx", 1024,
                "temperature", 0.15,
                "repeat_penalty", 1.1,
                "keep_alive", "10m"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<LocalLLMResponse> response = llmRestTemplate.postForEntity(
                baseUrl + "/api/generate",
                request,
                LocalLLMResponse.class
        );

        if (response.getBody() == null){
            throw new RuntimeException("Local LLM API Error");
        }

        return response.getBody().response();
    }
}
