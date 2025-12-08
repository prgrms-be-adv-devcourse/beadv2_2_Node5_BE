package com.node5.subscriptionservice.subscription.domain;

import com.node5.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "\"subscription_recurrence_rule\"", schema = "public")
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
        }
        if (this.recurrenceType == RecurrenceType.MONTHLY) {
            return calculateMonthly(baseDate);
        }
        throw new IllegalStateException("Not valid recurrence type: " + recurrenceType);
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

        if (todayDay < dayOfMonth) {
            return base.withDayOfMonth(dayOfMonth);
        }

        return base.plusMonths(1).withDayOfMonth(dayOfMonth);
    }
}
