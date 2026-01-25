package com.node5.paymentservice.payment.presentation;

import com.node5.paymentservice.payment.application.PaymentOutboxService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("internal/payments")
@RequiredArgsConstructor
public class PaymentInternalController {

    private final PaymentOutboxService paymentOutboxService;

    @Operation(summary = "내부 배치용 결제 아웃박스 레코드 삭제", description = "내부 배치용 결제 아웃박스 레코드 삭제")
    @DeleteMapping("/outbox/cleanup")
    public void deleteOldPaymentOutboxRecords() {
        paymentOutboxService.cleanupOldMessages();
    }
}
