package com.node5.paymentservice.payment.client.openfeign.dto;

import java.util.UUID;

public record WalletRequest(UUID memberId, String orderId, Long amount) {
}
