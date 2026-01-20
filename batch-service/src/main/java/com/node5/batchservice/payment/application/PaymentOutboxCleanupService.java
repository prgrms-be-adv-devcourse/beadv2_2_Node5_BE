package com.node5.batchservice.payment.application;

import com.node5.batchservice.payment.client.PaymentClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentOutboxCleanupService {
    private final PaymentClient paymentClient;

    public void cleanupOldMessages() {
        paymentClient.deleteOldPaymentOutboxRecords();
    }
}
