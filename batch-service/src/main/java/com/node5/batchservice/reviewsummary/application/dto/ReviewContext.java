package com.node5.batchservice.reviewsummary.application.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ReviewContext(
        UUID productId,
        String prevSummary,
        List<String> reviews,
        LocalDate summaryEndDate
) {}
