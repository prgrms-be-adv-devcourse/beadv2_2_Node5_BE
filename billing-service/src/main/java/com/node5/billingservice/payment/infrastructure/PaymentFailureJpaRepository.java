package com.node5.billingservice.payment.infrastructure;

import com.node5.billingservice.payment.domain.PaymentFailure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentFailureJpaRepository extends JpaRepository<PaymentFailure, UUID> {
    Page<PaymentFailure> findAllByMemberId(UUID walletId, Pageable pageable);
}
