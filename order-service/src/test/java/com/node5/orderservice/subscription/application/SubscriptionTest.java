package com.node5.orderservice.subscription.application;

import com.node5.orderservice.subscription.domain.RecurrenceType;
import com.node5.orderservice.subscription.domain.Subscription;
import com.node5.orderservice.subscription.domain.SubscriptionRecurrenceRule;
import com.node5.orderservice.subscription.domain.SubscriptionStatus;
import com.node5.orderservice.subscription.exception.SubscriptionErrorCode;
import com.node5.orderservice.subscription.exception.SubscriptionException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubscriptionTest {

    @Test
    void 구독을_생성하면_기본상태와_필드가_설정된다() {
        LocalDate today = LocalDate.now();

        Subscription subscription = Subscription.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "테스트 상품",
                "http://thumb",
                BigDecimal.valueOf(1200),
                2,
                "서울"
        );

        assertThat(subscription.getSubscriptionStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(subscription.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(2400));
        assertThat(subscription.getNextRunDate()).isEqualTo(today.plusDays(1));
    }

    @Test
    void 해지된_구독은_수정에_실패한다() {
        Subscription subscription = createSubscription();
        subscription.cancel();

        assertThatThrownBy(() -> subscription.update("서울"))
                .isInstanceOf(SubscriptionException.class)
                .extracting("errorCode")
                .isEqualTo(SubscriptionErrorCode.SUBSCRIPTION_INVALID_STATE);
    }

    @Test
    void 반복규칙이_여러개_있으면_가장_빠른_실행일을_선택한다() {
        Subscription subscription = createSubscription();
        LocalDate today = LocalDate.now();

        DayOfWeek weeklyDay = today.plusDays(1).getDayOfWeek();
        SubscriptionRecurrenceRule weeklyRule = SubscriptionRecurrenceRule.create(
                subscription.getId(),
                RecurrenceType.WEEKLY,
                weeklyDay,
                null
        );

        int targetDayOfMonth = 15;
        SubscriptionRecurrenceRule monthlyRule = SubscriptionRecurrenceRule.create(
                subscription.getId(),
                RecurrenceType.MONTHLY,
                null,
                targetDayOfMonth
        );

        LocalDate weeklyNext = today.plusDays(1);
        LocalDate monthlyNext = today.getDayOfMonth() < targetDayOfMonth
                ? today.withDayOfMonth(targetDayOfMonth)
                : today.plusMonths(1).withDayOfMonth(targetDayOfMonth);
        LocalDate expected = weeklyNext.isBefore(monthlyNext) ? weeklyNext : monthlyNext;

        subscription.calculateNextRunDate(List.of(weeklyRule, monthlyRule), LocalDate.now());

        assertThat(subscription.getNextRunDate()).isEqualTo(expected);
    }

    @Test
    void 반복규칙이_없으면_다음_실행일_계산에_실패한다() {
        Subscription subscription = createSubscription();

        assertThatThrownBy(() -> subscription.calculateNextRunDate(List.of(), LocalDate.now()))
                .isInstanceOf(SubscriptionException.class)
                .extracting("errorCode")
                .isEqualTo(SubscriptionErrorCode.SUBSCRIPTION_RULE_NOT_FOUND);
    }

    @Test
    void ACTIVE_상태이면_일시정지된다() {
        Subscription subscription = createSubscription();

        subscription.pause();

        assertThat(subscription.getSubscriptionStatus()).isEqualTo(SubscriptionStatus.PAUSED);
    }

    @Test
    void FAILED_상태이면_일시정지로_전환된다() {
        Subscription subscription = subscriptionWithStatus(SubscriptionStatus.FAILED);

        subscription.pause();

        assertThat(subscription.getSubscriptionStatus()).isEqualTo(SubscriptionStatus.PAUSED);
    }

    @Test
    void 이미_일시정지이면_다시_정지할_수_없다() {
        Subscription subscription = createSubscription();
        subscription.pause();

        assertThatThrownBy(subscription::pause)
                .isInstanceOf(SubscriptionException.class)
                .extracting("errorCode")
                .isEqualTo(SubscriptionErrorCode.SUBSCRIPTION_INVALID_STATE);
    }

    @Test
    void 일시정지_상태이면_재개된다() {
        Subscription subscription = createSubscription();
        subscription.pause();

        subscription.resume();

        assertThat(subscription.getSubscriptionStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void ACTIVE_상태이면_재개_실패한다() {
        Subscription subscription = createSubscription();

        assertThatThrownBy(subscription::resume)
                .isInstanceOf(SubscriptionException.class)
                .extracting("errorCode")
                .isEqualTo(SubscriptionErrorCode.SUBSCRIPTION_INVALID_STATE);
    }

    @Test
    void ACTIVE_상태이면_해지된다() {
        Subscription subscription = createSubscription();

        subscription.cancel();

        assertThat(subscription.getSubscriptionStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
    }

    @Test
    void 이미_해지된_구독은_해지에_실패한다() {
        Subscription subscription = createSubscription();
        subscription.cancel();

        assertThatThrownBy(subscription::cancel)
                .isInstanceOf(SubscriptionException.class)
                .extracting("errorCode")
                .isEqualTo(SubscriptionErrorCode.SUBSCRIPTION_INVALID_STATE);
    }

    private Subscription createSubscription() {
        return Subscription.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "테스트 상품",
                "http://thumb",
                BigDecimal.valueOf(1000),
                1,
                "서울"
        );
    }

    private Subscription subscriptionWithStatus(SubscriptionStatus status) {
        Subscription subscription = createSubscription();
        setStatus(subscription, status);
        return subscription;
    }

    private void setStatus(Subscription subscription, SubscriptionStatus status) {
        try {
            Field field = Subscription.class.getDeclaredField("subscriptionStatus");
            field.setAccessible(true);
            field.set(subscription, status);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("구독 상태를 설정할 수 없습니다.", e);
        }
    }
}
