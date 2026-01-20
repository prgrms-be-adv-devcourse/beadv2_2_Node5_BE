package com.node5.batchservice.payment.presentation;

import com.node5.batchservice.payment.application.PaymentOutboxCleanupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/batch/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentBatchController {
    private final PaymentOutboxCleanupService paymentOutboxCleanupService;

    @Operation(summary = "내부 배치용 결제 아웃박스 레코드 삭제", description = "내부 배치용 결제 아웃박스 레코드 삭제")
    @DeleteMapping("/outbox/cleanup")
    public void deleteOldPaymentOutboxRecords() {
        log.info("deleteOldPaymentOutboxRecords");
        paymentOutboxCleanupService.cleanupOldMessages();
    }
}
