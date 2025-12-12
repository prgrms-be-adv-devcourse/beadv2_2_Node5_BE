package com.node5.subscriptionservice.subscription.application;

import com.node5.subscriptionservice.subscription.domain.RecurrenceType;
import com.node5.subscriptionservice.subscription.domain.SubscriptionRecurrenceRule;
import com.node5.subscriptionservice.subscription.exception.SubscriptionErrorCode;
import com.node5.subscriptionservice.subscription.exception.SubscriptionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubscriptionRecurrenceRuleTest {

    @Test
    void 아직_도달하지_않은_요일이면_같은_주로_계산한다() {
        LocalDate base = LocalDate.of(2025, 12, 1); // Monday
        SubscriptionRecurrenceRule rule = SubscriptionRecurrenceRule.create(
                UUID.randomUUID(),
                RecurrenceType.WEEKLY,
                DayOfWeek.WEDNESDAY,
                null
        );

        LocalDate nextRunDate = rule.calculateNextRunDate(base);

        assertThat(nextRunDate).isEqualTo(LocalDate.of(2025, 12, 3));
    }

    @Test
    void 이미_지난_요일이면_다음_주로_계산한다() {
        LocalDate base = LocalDate.of(2025, 12, 3); // Wednesday
        SubscriptionRecurrenceRule rule = SubscriptionRecurrenceRule.create(
                UUID.randomUUID(),
                RecurrenceType.WEEKLY,
                DayOfWeek.MONDAY,
                null
        );

        LocalDate nextRunDate = rule.calculateNextRunDate(base);

        assertThat(nextRunDate).isEqualTo(LocalDate.of(2025, 12, 8));
    }

    @Test
    void 이번달에_남은_날짜가_있으면_이번달로_계산한다() {
        LocalDate base = LocalDate.of(2025, 12, 1);
        SubscriptionRecurrenceRule rule = SubscriptionRecurrenceRule.create(
                UUID.randomUUID(),
                RecurrenceType.MONTHLY,
                null,
                15
        );

        LocalDate nextRunDate = rule.calculateNextRunDate(base);

        assertThat(nextRunDate).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void 날짜가_지났으면_다음달로_계산한다() {
        LocalDate base = LocalDate.of(2025, 12, 20);
        SubscriptionRecurrenceRule rule = SubscriptionRecurrenceRule.create(
                UUID.randomUUID(),
                RecurrenceType.MONTHLY,
                null,
                5
        );

        LocalDate nextRunDate = rule.calculateNextRunDate(base);

        assertThat(nextRunDate).isEqualTo(LocalDate.of(2026, 1, 5));
    }

    @Test
    void 반복_주기는_대소문자_구분하지_않는다() {
        assertThat(RecurrenceType.from("weekly")).isEqualTo(RecurrenceType.WEEKLY);
        assertThat(RecurrenceType.from("MONTHLY")).isEqualTo(RecurrenceType.MONTHLY);
        assertThat(RecurrenceType.from("unknown")).isNull();
    }
}
