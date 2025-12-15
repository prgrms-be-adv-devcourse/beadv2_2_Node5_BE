package com.node5.orderservice.order.application;

import com.node5.orderservice.order.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderTransactionService orderTransactionService;

    @Scheduled(cron = "0 */1 * * * *")
    public void updateOrderStatus() {
        log.info("** Order status update scheduler started at {}", LocalDateTime.now());

        LocalDateTime ago = LocalDateTime.now().minusMinutes(1); //TODO 테스트용 설정

        // PAID -> DELIVERY_ING 갱신
        orderTransactionService.updateToDeliveryIng(ago);

        // DELIVERY_ING -> DELIVERY_COMPLETED 갱신
        orderTransactionService.updateToDeliveryCompleted(ago);

        // DELIVERY_COMPLETED -> CONFIRMED 갱신
        orderTransactionService.updateToConfirmed(ago);

        log.info("** Order status update scheduler finished at {}", LocalDateTime.now());
    }

    // 매일 CONFIRMED 상태의 주문만 조회하여 정산 API를 호출
    //@Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 */3 * * * *") //테스트: 3분
    public void processSettlementRequest(){
        orderTransactionService.processSettlementRequest();
    }

}
