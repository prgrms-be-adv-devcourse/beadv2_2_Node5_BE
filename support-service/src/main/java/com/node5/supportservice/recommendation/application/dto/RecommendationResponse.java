package com.node5.supportservice.recommendation.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendationResponse {
    private String tasteSummary;
    private float[] embedding;
}
