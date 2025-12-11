package com.node5.subscriptionservice.subscription.presentation.dto;

import com.node5.subscriptionservice.subscription.application.dto.SubscriptionUpdateCommand;
import com.node5.subscriptionservice.subscription.domain.RecurrenceType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;

public record SubscriptionUpdateRequest(
        @NotNull(message = "상품 단가는 필수 입력값입니다.")
        @DecimalMin(value = "0.0", message = "주문 단가는 0보다 커야 합니다.")
        BigDecimal pricePerItem,

        @NotNull(message = "수량은 필수 입력값입니다.")
        @Positive(message = "수량은 1개 이상이어야 합니다.")
        Integer quantity,

        @NotBlank(message = "배송지는 필수 값입니다.")
        String deliveryAddress,

        @NotBlank(message = "구독 주기 타입은 필수 입력값입니다.")
        @Pattern(regexp = "weekly|monthly",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "지원하지 않는 주기 타입입니다.")
        String recurrenceType,

        List<@Min(1)@Max(7) Integer> dayOfWeek,

        @Min(value = 1, message = "dayOfMonth는 1 이상이어야 합니다.")
        @Max(value = 31, message = "dayOfMonth는 31 이하여야 합니다.")
        Integer dayOfMonth
) {
    @AssertTrue(message = "반복 유형에 맞는 요일/날짜 값이 올바르지 않습니다.")
    public boolean isValidRecurrenceRule() {
        RecurrenceType type = RecurrenceType.from(recurrenceType);

        switch (type) {
            case WEEKLY -> {
                if (dayOfWeek == null || dayOfWeek.isEmpty()) {
                    return false;
                }
                boolean dayOfWeekValid = dayOfWeek.stream()
                        .allMatch(d -> d != null && d >= 1 && d <= 7);
                return dayOfWeekValid;
            }
            case MONTHLY -> {
                if (dayOfMonth == null) {
                    return false;
                }
            }
        };
        return true;
    }

    public SubscriptionUpdateCommand toCommand() {
        RecurrenceType type = RecurrenceType.from(recurrenceType);

        List<DayOfWeek> days = null;
        if (type == RecurrenceType.WEEKLY && dayOfWeek != null) {
            days = dayOfWeek.stream()
                    .map(DayOfWeek::of)
                    .toList();
        }

        return new SubscriptionUpdateCommand(
                pricePerItem,
                quantity,
                deliveryAddress,
                type,
                days,
                dayOfMonth
        );
    }
}
