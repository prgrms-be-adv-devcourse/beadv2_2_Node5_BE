package com.node5.supportservice.notification.worker;

import com.node5.supportservice.notification.domain.message.NotificationMessage;
import com.node5.supportservice.notification.infrastructure.rabbitmq.config.RabbitmqConfig;
import com.node5.supportservice.notification.infrastructure.sender.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailWorker implements NotificationWorker {

    private final EmailSender emailSender;

    @Override
    @RabbitListener(queues = RabbitmqConfig.EMAIL_WORKER_QUEUE)
    public void consume(NotificationMessage message) {
        emailSender.send(message);
    }
}
