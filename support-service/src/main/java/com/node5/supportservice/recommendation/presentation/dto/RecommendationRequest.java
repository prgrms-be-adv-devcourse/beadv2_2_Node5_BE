package com.node5.supportservice.recommendation.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {
    private List<ProductItem> orderItems;
    private List<ProductItem> cartItemIds;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem {
        private String name;
        private String category;
        private String description;
    }
}
