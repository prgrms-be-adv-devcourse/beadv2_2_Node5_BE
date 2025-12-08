package com.node5.subscriptionservice.subscription.application;

import com.node5.common.domain.ApiResponseDto;
import com.node5.common.domain.PageInfoDto;
import com.node5.common.domain.PagedResponseDto;
import com.node5.subscriptionservice.subscription.application.dto.SubscriptionCreateCommand;
import com.node5.subscriptionservice.subscription.application.dto.SubscriptionInfo;
import com.node5.subscriptionservice.subscription.application.dto.SubscriptionUpdateCommand;
import com.node5.subscriptionservice.subscription.domain.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionRecurrenceRuleRepository subscriptionRecurrenceRuleRepository;

    public ResponseEntity<ApiResponseDto<SubscriptionInfo>> findById(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + id));

        SubscriptionInfo subscriptionInfo = toSubscriptionInfo(subscription);

        ApiResponseDto<SubscriptionInfo> responseDto = new ApiResponseDto<>(HttpStatus.OK.value(), "구독 조회 성공", subscriptionInfo);
        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<ApiResponseDto<PagedResponseDto<SubscriptionInfo>>> findAllByMemberId(UUID memberId, Pageable pageable) {
        Page<Subscription> page = subscriptionRepository.findAllByMemberId(memberId, pageable);

        List<SubscriptionInfo> subscriptionInfos = page.getContent().stream()
                .map(subscription -> toSubscriptionInfo(subscription))
                .toList();

        PageInfoDto pageInfo = new PageInfoDto(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        PagedResponseDto<SubscriptionInfo> pagedResponse =
                new PagedResponseDto<>(subscriptionInfos, pageInfo);

        ApiResponseDto<PagedResponseDto<SubscriptionInfo>> response =
                new ApiResponseDto<>(HttpStatus.OK.value(), "구독 목록 조회 성공", pagedResponse);

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<SubscriptionInfo>> create(SubscriptionCreateCommand command) {
        Subscription subscription = Subscription.create(
                command.memberId(),
                command.productId(),
                command.pricePerItem(),
                command.quantity(),
                command.deliveryAddress()
        );

        List<SubscriptionRecurrenceRule> rules = createSubscriptionRecurrenceRule(
                subscription.getId(),
                command.recurrenceType(),
                command.dayOfWeek(),
                command.dayOfMonth()
        );

        if (rules.isEmpty()) {
            throw new IllegalArgumentException("No recurrence rules created");
        }

        subscription.calculateNextRunDate(rules);

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        subscriptionRecurrenceRuleRepository.saveAll(rules);

        SubscriptionInfo subscriptionInfo = toSubscriptionInfo(savedSubscription);

        ApiResponseDto<SubscriptionInfo> responseDto = new ApiResponseDto<>(HttpStatus.CREATED.value(), "구독 생성 성공", subscriptionInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<SubscriptionInfo>> update(SubscriptionUpdateCommand command, UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + id));

        subscription.update(
                command.pricePerItem(),
                command.quantity(),
                command.deliveryAddress()
        );

        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        if(command.recurrenceType() != null) {

            List<SubscriptionRecurrenceRule> newRules = createSubscriptionRecurrenceRule(
                    id,
                    command.recurrenceType(),
                    command.dayOfWeek(),
                    command.dayOfMonth()
            );

            subscriptionRecurrenceRuleRepository.deleteAllBySubscriptionId(id);
            subscription.calculateNextRunDate(newRules);
            subscriptionRecurrenceRuleRepository.saveAll(newRules);
        }

        SubscriptionInfo subscriptionInfo = toSubscriptionInfo(updatedSubscription);

        ApiResponseDto<SubscriptionInfo> responseDto = new ApiResponseDto<>(HttpStatus.OK.value(), "구독 수정 성공", subscriptionInfo);
        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<SubscriptionInfo>> pause(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + id));

        subscription.pause();
        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        SubscriptionInfo subscriptionInfo = toSubscriptionInfo(updatedSubscription);

        ApiResponseDto<SubscriptionInfo> responseDto = new ApiResponseDto<>(HttpStatus.OK.value(), "구독 일시정지 성공", subscriptionInfo);
        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<SubscriptionInfo>> resume(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + id));

        subscription.resume();
        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        SubscriptionInfo subscriptionInfo = toSubscriptionInfo(updatedSubscription);

        ApiResponseDto<SubscriptionInfo> responseDto = new ApiResponseDto<>(HttpStatus.OK.value(), "구독 재개 성공", subscriptionInfo);
        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<SubscriptionInfo>> delete(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + id));

        subscription.delete();
        subscriptionRepository.save(subscription);

        ApiResponseDto<SubscriptionInfo> responseDto = new ApiResponseDto<>(HttpStatus.NO_CONTENT.value(), "구독 삭제 성공", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseDto);
    }

    private List<SubscriptionRecurrenceRule> createSubscriptionRecurrenceRule(UUID subscriptionId, RecurrenceType recurrenceType, List<DayOfWeek> dayOfWeek, Integer dayOfMonth) {
        if (recurrenceType == RecurrenceType.WEEKLY) {
            if (dayOfWeek == null || dayOfWeek.isEmpty()) {
                throw new IllegalArgumentException("DayOfWeek not included");
            }

            return dayOfWeek.stream()
                    .map(day -> SubscriptionRecurrenceRule.create(
                            subscriptionId,
                            recurrenceType,
                            day,
                            dayOfMonth))
                    .toList();
        }  else if (recurrenceType ==  RecurrenceType.MONTHLY) {
            if (dayOfMonth == null) {
                throw new IllegalArgumentException("dayOfMonth not included");
            }
            if (dayOfMonth < 1 || dayOfMonth > 31) {
                throw new IllegalArgumentException("dayOfMonth must be between 1 and 31");
            }
            return List.of(SubscriptionRecurrenceRule.create(
                    subscriptionId,
                    recurrenceType,
                    null,
                    dayOfMonth)
            );
        }
        return List.of();
    }

    private SubscriptionInfo toSubscriptionInfo(Subscription subscription) {
        List<SubscriptionRecurrenceRule> rules =
                subscriptionRecurrenceRuleRepository.findBySubscriptionId(subscription.getId());

        if (rules.isEmpty()) {
            throw new EntityNotFoundException("Recurrence rules not found for subscription: " + subscription.getId());
        }

        RecurrenceType ruleType = rules.get(0).getRecurrenceType();
        List<DayOfWeek> dayOfWeek = rules.stream()
                .map(SubscriptionRecurrenceRule::getDayOfWeek)
                .filter(Objects::nonNull)
                .toList();
        Integer dayOfMonth = rules.get(0).getDayOfMonth();

        return SubscriptionInfo.from(subscription, ruleType, dayOfWeek, dayOfMonth);
    }
}
