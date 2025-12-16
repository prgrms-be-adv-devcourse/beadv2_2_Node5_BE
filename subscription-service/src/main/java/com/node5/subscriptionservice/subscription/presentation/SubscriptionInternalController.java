package com.node5.subscriptionservice.subscription.presentation;

import com.node5.subscriptionservice.subscription.application.SubscriptionInternalService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/subscriptions")
public class SubscriptionInternalController {
    private final SubscriptionInternalService internalService;

    @Operation(summary = "구독 주문 배치 실행", description = "구독 주문을 수동으로 실행합니다. runDate가 없으면 오늘 날짜로 실행됩니다.")
    @PostMapping("/batch/run")
    public ResponseEntity<Void> runBatch(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate runDate // "yyyy-MM-dd"
    ) {
        internalService.runBatch(runDate);
        return ResponseEntity.ok().build();
    }
}
