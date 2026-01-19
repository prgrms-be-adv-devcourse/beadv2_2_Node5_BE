package com.node5.supportservice.recommendation.application.dto;

import com.node5.supportservice.global.openfeign.client.dto.ProductSummaryListResponse;
import java.util.ArrayList;
import java.util.List;

public record PromptPayload(
        String instruction,
        List<ProductPayload> orderItemList,
        List<ProductPayload> cartItemList
) {
    public record ProductPayload(
            String name,
            String category,
            String description
    ) { }

    public static List<ProductPayload> fromItems(List<ProductSummaryListResponse.ProductSummaryResponse> items) {
        if (items == null) {
            return List.of();
        }
        List<ProductPayload> payloads = new ArrayList<>();
        for (ProductSummaryListResponse.ProductSummaryResponse item : items) {
            payloads.add(new ProductPayload(
                    trim(item.name(), 80),
                    trim(item.category(), 40),
                    trim(item.description(), 200)
            ));
        }
        return payloads;
    }

    private static String trim(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength);
    }
}
