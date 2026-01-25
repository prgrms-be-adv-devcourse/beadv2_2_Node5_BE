package com.node5.orderservice.order.application;

import com.node5.orderservice.order.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import static com.node5.orderservice.order.domain.OrderProgress.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderTransactionService orderTransactionService;

    // OrderItem status PAID -> DELIVERY_ING 갱신
    @Scheduled(cron = "0 0/3 * * * *")
    public void updateItemStatusFromPaidToIng() {
        orderTransactionService.updateOrderItemStatus(PAID, DELIVERY_ING);
    }

    // OrderItem status DELIVERY_ING -> DELIVERY_COMPLETED 갱신
    @Scheduled(cron = "0 1/3 * * * *")
    public void updateItemStatusFromIngToCompleted() {
        orderTransactionService.updateOrderItemStatus(DELIVERY_ING, DELIVERY_COMPLETED);
    }

    // OrderItem status DELIVERY_COMPLETED -> CONFIRMED 갱신
    @Scheduled(cron = "0 2/3 * * * *")
    public void updateItemStatusFromCompletedToConfirmed() {
        orderTransactionService.updateOrderItemStatus(DELIVERY_COMPLETED, CONFIRMED);
    }

    // 매일 CONFIRMED 상태의 주문 상품만 조회하여 정산 API를 호출
    @Scheduled(cron = "${scheduling.cron.collect-settlement-source:0 */5 * * * *}")
    public void processSettlementRequest(){
        orderTransactionService.processSettlementRequest();
    }

}
