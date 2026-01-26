package com.node5.orderservice.subscription.presentation.dto;

import com.node5.orderservice.subscription.application.dto.SubscriptionUpdateCommand;
import jakarta.validation.constraints.*;

import java.time.DayOfWeek;
import java.util.List;

public record SubscriptionUpdateRequest(
        @NotBlank(message = "배송지는 필수 값입니다.")
        String deliveryAddress,

        List<@Min(1)@Max(7) Integer> dayOfWeek,

        @Min(value = 1, message = "dayOfMonth는 1 이상이어야 합니다.")
        @Max(value = 31, message = "dayOfMonth는 31 이하여야 합니다.")
        Integer dayOfMonth
) {
    public SubscriptionUpdateCommand toCommand() {
        List<DayOfWeek> days = null;
        if (dayOfWeek != null) {
            days = dayOfWeek.stream()
                    .map(DayOfWeek::of)
                    .toList();
        }

        return new SubscriptionUpdateCommand(
                deliveryAddress,
                days,
                dayOfMonth
        );
    }
}
