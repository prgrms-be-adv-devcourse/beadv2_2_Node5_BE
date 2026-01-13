package com.node5.supportservice.review.application.dto;

public record ReviewUpdateCommand(
        Integer rating,
        String body) {
}
