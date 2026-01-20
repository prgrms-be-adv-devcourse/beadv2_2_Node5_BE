package com.node5.batchservice.reviewsummary.client.dto;

public record ReviewSearchSimilarRequest(
        int referenceYear,
        int referenceMonth
) {
}
