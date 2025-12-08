package com.node5.subscriptionservice.subscription.presentation.dto;

import com.node5.subscriptionservice.subscription.application.dto.SubscriptionCreateCommand;
import com.node5.subscriptionservice.subscription.domain.RecurrenceType;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public record SubscriptionCreateRequest(
        UUID memberId,  // TODO: 회원ID를 내부에서 확인하도록 수정
        UUID productId,
        BigDecimal pricePerItem,
        Integer quantity,
        String deliveryAddress,
        String recurrenceType,
        List<Integer> dayOfWeek,
        Integer dayOfMonth
) {
    public SubscriptionCreateCommand toCommand() {
        RecurrenceType type = RecurrenceType.from(recurrenceType);
        List<DayOfWeek> days = null;
        if (type == RecurrenceType.WEEKLY) {
            if (dayOfWeek == null || dayOfWeek.isEmpty()) {
                throw new IllegalArgumentException("Weekly recurrenceType requires dayOfWeek values.");
            }
            days = dayOfWeek.stream()
                    .map(num -> {
                        try {
                            return DayOfWeek.of(num); // 1 = Monday ~ 7 = Sunday
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Invalid dayOfWeek value: " + num);
                        }
                    })
                    .toList();
        }

        return new SubscriptionCreateCommand(
                memberId,
                productId,
                pricePerItem,
                quantity,
                deliveryAddress,
                type,
                days,
                dayOfMonth
        );
    }
}
