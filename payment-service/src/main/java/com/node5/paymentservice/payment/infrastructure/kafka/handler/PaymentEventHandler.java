package com.node5.paymentservice.payment.infrastructure.kafka.handler;

import com.node5.common.event.PaymentDepositEvent;
import com.node5.paymentservice.payment.infrastructure.kafka.producer.PaymentDepositEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventHandler {
    private final PaymentDepositEventProducer paymentDepositEventProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentDepositEvent event) {
        paymentDepositEventProducer.send(event);
    }

}