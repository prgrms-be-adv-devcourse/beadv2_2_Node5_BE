package com.node5.subscriptionservice.subscription.domain;

import com.node5.common.domain.BaseEntity;
import com.node5.subscriptionservice.subscription.exception.SubscriptionException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

import static com.node5.subscriptionservice.subscription.exception.SubscriptionErrorCode.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "\"subscription_recurrence_rule\"", schema = "subscription")
public class SubscriptionRecurrenceRule extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "subscription_id", nullable = false)
    private UUID subscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_type", nullable = false)
    private RecurrenceType recurrenceType;

    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name = "day_of_month")
    private Integer dayOfMonth;

    public static SubscriptionRecurrenceRule create(UUID subscriptionId, RecurrenceType recurrenceType, DayOfWeek dayOfWeek, Integer dayOfMonth) {
        return new SubscriptionRecurrenceRule(
                UUID.randomUUID(),
                subscriptionId,
                recurrenceType,
                dayOfWeek,
                dayOfMonth
        );
    }

    public LocalDate calculateNextRunDate(LocalDate baseDate) {
        if (this.recurrenceType == RecurrenceType.WEEKLY) {
            return calculateWeekly(baseDate);
        } else if (this.recurrenceType == RecurrenceType.MONTHLY) {
            return calculateMonthly(baseDate);
        }
        throw new SubscriptionException(INVALID_RECURRENCE_TYPE);
    }

    private LocalDate calculateWeekly(LocalDate base) {
        int todayValue = base.getDayOfWeek().getValue();
        int target = dayOfWeek.getValue();

        if (todayValue < target) {
            // 날짜가 구독 요일을 지나지 않은 경우 계산
            return base.plusDays(target - todayValue);
        } else {
            // 날짜가 구독 요일을 지났을 경우 다음주로 계산
            return base.plusDays(7 - (todayValue - target));
        }
    }

    private LocalDate calculateMonthly(LocalDate base) {
        int todayDay = base.getDayOfMonth();
        int lastDayOfMonth = base.lengthOfMonth();
        int targetDay = Math.min(dayOfMonth, lastDayOfMonth);

        if (todayDay < targetDay) {
            return base.withDayOfMonth(targetDay);
        }

        LocalDate nextMonth = base.plusMonths(1);
        int nextMonthDay = Math.min(dayOfMonth, nextMonth.lengthOfMonth());
        return nextMonth.withDayOfMonth(nextMonthDay);
    }
}
