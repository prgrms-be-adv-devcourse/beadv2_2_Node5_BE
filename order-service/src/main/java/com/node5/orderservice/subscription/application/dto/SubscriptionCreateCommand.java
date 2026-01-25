package com.node5.orderservice.subscription.application.dto;

import com.node5.orderservice.subscription.domain.RecurrenceType;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public record SubscriptionCreateCommand(
        UUID productId,
        Integer quantity,
        String deliveryAddress,
        RecurrenceType recurrenceType,
        List<DayOfWeek> dayOfWeek,
        Integer dayOfMonth
) {
}
