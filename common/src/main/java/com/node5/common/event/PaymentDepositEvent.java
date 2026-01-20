package com.node5.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentDepositEvent(
        UUID paymentId,     // 아웃박스의 aggregateId와 매칭
        UUID memberId,      // 결제 주체
        String orderId,     // 주문 번호 (Kafka Key로 사용 가능)
        Long amount,        // 결제 금액
        LocalDateTime createdAt // 이벤트 생성 시각 (지연 처리 확인용)
) {
}
