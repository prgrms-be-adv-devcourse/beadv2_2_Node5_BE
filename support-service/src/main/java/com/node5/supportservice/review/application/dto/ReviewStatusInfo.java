package com.node5.supportservice.review.application.dto;

public record ReviewStatusInfo(
        Boolean reviewed
) {
    public static ReviewStatusInfo from(Boolean status) {
        return new ReviewStatusInfo(
                status
        );
    }
}
