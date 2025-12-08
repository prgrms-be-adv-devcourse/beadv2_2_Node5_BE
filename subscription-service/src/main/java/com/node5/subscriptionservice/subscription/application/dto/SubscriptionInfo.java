package com.node5.subscriptionservice.subscription.application.dto;

import com.node5.subscriptionservice.subscription.domain.RecurrenceType;
import com.node5.subscriptionservice.subscription.domain.Subscription;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record SubscriptionInfo(
        UUID id,
        String subscriptionStatus,
        BigDecimal pricePerItem,
        Integer quantity,
        BigDecimal totalPrice,
        String deliveryAddress,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        LocalDate nextRunDate,
        String recurrenceType,
        List<Integer> dayOfWeek,
        Integer dayOfMonth
) {
    public static SubscriptionInfo from(Subscription subscription, RecurrenceType recurrenceType, List<DayOfWeek> dayOfWeek, Integer dayOfMonth) {
        List<Integer> dayOfWeekList =
                dayOfWeek == null
                        ? List.of()
                        : dayOfWeek.stream().map(DayOfWeek::getValue).toList();

        return new SubscriptionInfo(
                subscription.getId(),
                subscription.getSubscriptionStatus().toString(),
                subscription.getPricePerItem(),
                subscription.getQuantity(),
                subscription.getTotalPrice(),
                subscription.getDeliveryAddress(),
                subscription.getCreatedAt(),
                subscription.getModifiedAt(),
                subscription.getNextRunDate(),
                recurrenceType.name().toLowerCase(),
                dayOfWeekList,
                dayOfMonth
        );
    }
}
