package com.node5.paymentservice.payment.infrastructure;

import com.node5.paymentservice.payment.domain.PaymentOutbox;
import com.node5.paymentservice.payment.domain.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentOutboxRepositoryAdapter implements PaymentOutboxRepository {
    private final PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @Override
    public void save(PaymentOutbox paymentOutbox) {
        paymentOutboxJpaRepository.save(paymentOutbox);
    }

    @Override
    public List<PaymentOutbox> findReadyForUpdate(Pageable pageable) {
        return paymentOutboxJpaRepository.findReadyForUpdate(pageable);
    }

    @Override
    public int deleteOldPublishedMessages(LocalDateTime threshold) {
        return paymentOutboxJpaRepository.deleteOldPublishedMessages(threshold);
    }
}
