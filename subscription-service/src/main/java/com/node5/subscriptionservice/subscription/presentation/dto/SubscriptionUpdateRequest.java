package com.node5.subscriptionservice.subscription.presentation.dto;

import com.node5.subscriptionservice.subscription.application.dto.SubscriptionUpdateCommand;
import com.node5.subscriptionservice.subscription.domain.RecurrenceType;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;

public record SubscriptionUpdateRequest(
        BigDecimal pricePerItem,
        Integer quantity,
        String deliveryAddress,
        String recurrenceType,
        List<Integer> dayOfWeek,
        Integer dayOfMonth
) {
    public SubscriptionUpdateCommand toCommand() {
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
