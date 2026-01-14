package com.node5.supportservice.review.presentation.dto;

import com.node5.supportservice.review.application.dto.ReviewSearchSimilarCommand;

public record ReviewSearchSimilarRequest(
        int referenceYear,
        int referenceMonth
) {
    public ReviewSearchSimilarCommand toCommand(ReviewSearchSimilarRequest request) {
        return new ReviewSearchSimilarCommand(
                request.referenceYear(),
                request.referenceMonth()
        );
    }
}
