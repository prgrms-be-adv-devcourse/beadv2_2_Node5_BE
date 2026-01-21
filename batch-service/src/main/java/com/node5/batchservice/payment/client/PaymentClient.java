package com.node5.batchservice.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @DeleteMapping("/internal/payments/outbox/cleanup")
    ResponseEntity<Integer> deleteOldPaymentOutboxRecords();
}
