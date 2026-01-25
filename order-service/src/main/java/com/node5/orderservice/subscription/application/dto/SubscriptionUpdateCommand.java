package com.node5.orderservice.subscription.application.dto;

import java.time.DayOfWeek;
import java.util.List;

public record SubscriptionUpdateCommand(
        String deliveryAddress,
        List<DayOfWeek> dayOfWeek,
        Integer dayOfMonth
) {
}
