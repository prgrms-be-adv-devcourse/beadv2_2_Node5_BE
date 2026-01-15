package com.node5.supportservice.notification.infrastructure.rabbitmq.consumer;

import com.node5.common.event.OrderStatusChangedEvent;
import com.node5.supportservice.notification.application.NotificationHandler;
import com.node5.supportservice.notification.infrastructure.rabbitmq.config.RabbitmqConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusChangedConsumer {

    private final NotificationHandler notificationHandler;

    @RabbitListener(queues = RabbitmqConfig.ORDER_STATUS_QUEUE)
    public void consume(OrderStatusChangedEvent event) {
        notificationHandler.orderStatusChangedHandle(event);
    }
}
