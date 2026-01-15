package com.node5.supportservice.recommendation.presentation.dto;

public record ProductEmbeddingBackfillResponse(
        int processed,
        int pages,
        int pageSize
) {}
