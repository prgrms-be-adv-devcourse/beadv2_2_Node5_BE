package com.node5.batchservice.reviewsummary.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class NoReviewException extends RuntimeException {

    private final UUID productId;
    private final int year;
    private final int month;

    public NoReviewException(UUID productId, int year, int month) {
        super(String.format(
                "No reviews found. productId=%s, year=%d, month=%d",
                productId, year, month
        ));
        this.productId = productId;
        this.year = year;
        this.month = month;
    }
}
