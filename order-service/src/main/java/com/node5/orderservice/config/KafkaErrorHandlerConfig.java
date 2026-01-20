package com.node5.orderservice.config;

import com.node5.orderservice.subscription.infrastructure.kafka.FailedKafkaRecordRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            FailedKafkaRecordRepository failedKafkaRecordRepository,
            @Value("${kafka.consumer.retry.backoff-ms:1000}") long backoffMs,
            @Value("${kafka.consumer.retry.max-attempts:3}") long maxAttempts) {
        ConsumerRecordRecoverer recoverer =
                (record, ex) -> failedKafkaRecordRepository.save(record, ex);
        FixedBackOff backOff = new FixedBackOff(backoffMs, maxAttempts);
        return new DefaultErrorHandler(recoverer, backOff);
    }
}
