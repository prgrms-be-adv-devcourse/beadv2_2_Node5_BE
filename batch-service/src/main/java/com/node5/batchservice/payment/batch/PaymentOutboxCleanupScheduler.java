package com.node5.batchservice.payment.batch;

import com.node5.batchservice.payment.application.PaymentOutboxCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class PaymentOutboxCleanupScheduler {
    private final PaymentOutboxCleanupService paymentOutboxService;

    //매주 월요일 06:00시에 만료된 Outbox 메시지 정리 작업 실행
    @Scheduled(cron = "0 0 6 * * MON")
    public void cleanUpOldOutboxMessages() {
        paymentOutboxService.cleanupOldMessages();
    }
}
