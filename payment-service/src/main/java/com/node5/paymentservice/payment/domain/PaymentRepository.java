package com.node5.paymentservice.payment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Page<Payment> findAllByMemberId(UUID walletId, Pageable pageable);

    Optional<Payment> findByOrderId(String orderId);

    void saveAndFlush(Payment payment);

    Payment save(Payment payment);

    Payment findById(UUID id);
}
