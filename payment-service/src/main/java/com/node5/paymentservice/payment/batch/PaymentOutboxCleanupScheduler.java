package com.node5.paymentservice.payment.batch;

import com.node5.paymentservice.payment.application.PaymentOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentOutboxCleanupScheduler {
    private final PaymentOutboxService paymentOutboxService;

    //매주 월요일 06:00시에 만료된 Outbox 메시지 정리 작업 실행
    @Scheduled(cron = "0 0 6 * * MON")
    public void cleanUpOldOutboxMessages() {
        paymentOutboxService.cleanupOldMessages();
    }
}
