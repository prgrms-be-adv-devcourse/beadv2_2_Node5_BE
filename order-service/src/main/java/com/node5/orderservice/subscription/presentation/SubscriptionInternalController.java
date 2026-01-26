package com.node5.orderservice.subscription.presentation;

import com.node5.common.domain.PagedResponseDto;
import com.node5.orderservice.subscription.application.SubscriptionInternalService;
import com.node5.orderservice.subscription.application.dto.SubscriptionBatchTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/subscriptions/batch")
public class SubscriptionInternalController {

    private final SubscriptionInternalService subscriptionInternalService;

    @GetMapping("/targets")
    public ResponseEntity<PagedResponseDto<SubscriptionBatchTarget>> getTargets(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate runDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        LocalDate targetDate = runDate != null ? runDate : LocalDate.now();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(subscriptionInternalService.findBatchTargets(targetDate, pageable));
    }
}
