package com.node5.paymentservice.payment.domain;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface PaymentOutboxRepository {
    void save(PaymentOutbox paymentOutbox);
    List<PaymentOutbox> findReadyForUpdate(Pageable pageable);
    int deleteOldPublishedMessages(LocalDateTime threshold);
}
