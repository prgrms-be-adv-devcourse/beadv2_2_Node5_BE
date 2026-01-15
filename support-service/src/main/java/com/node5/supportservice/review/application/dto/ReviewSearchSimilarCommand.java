package com.node5.supportservice.review.application.dto;

public record ReviewSearchSimilarCommand(
        int referenceYear,
        int referenceMonth
) {
}
