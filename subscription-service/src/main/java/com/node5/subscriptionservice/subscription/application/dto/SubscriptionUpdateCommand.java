package com.node5.subscriptionservice.subscription.application.dto;

import com.node5.subscriptionservice.subscription.domain.RecurrenceType;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;

public record SubscriptionUpdateCommand(
        String deliveryAddress,
        List<DayOfWeek> dayOfWeek,
        Integer dayOfMonth
) {
}
