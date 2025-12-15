package com.node5.orderservice.order.application;

import com.node5.orderservice.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@Slf4j
@RequiredArgsConstructor
public class OrderStatusScheduler {

    private final OrderRepository orderRepository;
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

}
