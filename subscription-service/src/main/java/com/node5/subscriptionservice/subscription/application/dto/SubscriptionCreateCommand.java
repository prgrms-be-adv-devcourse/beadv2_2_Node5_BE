package com.node5.subscriptionservice.subscription.application.dto;

import com.node5.subscriptionservice.subscription.domain.RecurrenceType;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public record SubscriptionCreateCommand(
        UUID memberId,  // TODO: 회원ID를 내부에서 확인하도록 수정
        UUID productId,
        Integer quantity,
        String deliveryAddress,
        RecurrenceType recurrenceType,
        List<DayOfWeek> dayOfWeek,
        Integer dayOfMonth
) {
}
