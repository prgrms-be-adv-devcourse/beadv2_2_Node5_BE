package com.node5.paymentservice.payment.batch;

import com.node5.paymentservice.payment.application.PaymentOutboxService;
import com.node5.paymentservice.payment.domain.PaymentOutbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentOutboxScheduler {
    private final PaymentOutboxService paymentOutboxService;

    @Scheduled(fixedDelay = 1000)
    public void publish() {
        List<PaymentOutbox> batch = paymentOutboxService.getBatchForUpdate();

        for (PaymentOutbox outbox : batch) {
            log.info("Publishing payment outbox {}", outbox);
            paymentOutboxService.publish(outbox);
        }
    }
}
