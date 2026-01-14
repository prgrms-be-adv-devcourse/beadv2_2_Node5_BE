package com.node5.supportservice.recommendation.application.dto;

public record RecommendationResponse (
        String tasteSummary,
        float[] embedding
){
}
