package com.node5.supportservice.review.presentation.dto;

import com.node5.supportservice.review.application.dto.ReviewCreateCommand;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ReviewCreateRequest(
        @NotNull(message = "상품 ID는 필수입니다.")
        UUID productId,
        @NotNull(message = "주문 ID는 필수입니다.")
        UUID orderId,
        @NotNull(message = "평점은 필수입니다.")
        @Min(value = 1, message = "최소 점수는 1점입니다.")
        @Max(value = 5, message = "최대 점수는 5점입니다.")
        Integer rating,
        @Size(max = 200, message = "리뷰 내용은 200자 이내로 입력해주세요.")
        String body
) {
    public ReviewCreateCommand toCommand() {
        return new ReviewCreateCommand(productId, orderId, rating, body);
    }
}
