package com.node5.billingservice.payment.infrastructure;

import com.node5.billingservice.payment.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {
    Page<Payment> findAllByWalletId(UUID walletId, Pageable pageable);

    Optional<Payment> findByOrderId(String orderId);
}
