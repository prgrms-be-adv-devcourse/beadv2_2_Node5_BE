package com.node5.billingservice.payment.infrastructure;

import com.node5.billingservice.payment.domain.PaymentFailure;
import com.node5.billingservice.payment.domain.PaymentFailureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentFailureRepositoryAdapter implements PaymentFailureRepository {

    private final PaymentFailureJpaRepository paymentFailureJpaRepository;

    @Override
    public Page<PaymentFailure> findAllByWalletId(UUID walletId, Pageable pageable) {
        return paymentFailureJpaRepository.findAllByWalletId(walletId, pageable);
    }

    @Override
    public PaymentFailure save(PaymentFailure paymentFailure) {
        return paymentFailureJpaRepository.save(paymentFailure);
    }
}
