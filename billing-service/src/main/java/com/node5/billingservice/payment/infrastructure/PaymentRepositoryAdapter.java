package com.node5.billingservice.payment.infrastructure;

import com.node5.billingservice.payment.domain.Payment;
import com.node5.billingservice.payment.domain.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Page<Payment> findAllByMemberId(UUID memberId, Pageable pageable) {
        return paymentJpaRepository.findAllByMemberId(memberId, pageable);
    }

    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return paymentJpaRepository.findByOrderId(orderId);
    }

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
