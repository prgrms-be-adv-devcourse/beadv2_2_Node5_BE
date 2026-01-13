package com.node5.supportservice.recommendation.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecommendationResponse {
    private String tasteSummary;
    private List<Double> embedding;
}
