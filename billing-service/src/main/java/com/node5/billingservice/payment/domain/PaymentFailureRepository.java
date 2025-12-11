package com.node5.billingservice.payment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentFailureRepository {

    Page<PaymentFailure> findAllByWalletId(UUID walletId, Pageable pageable);

    PaymentFailure save(PaymentFailure paymentFailure);
}
