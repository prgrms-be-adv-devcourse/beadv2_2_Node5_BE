package com.node5.orderservice.subscription.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FailedKafkaRecordRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public void save(ConsumerRecord<?, ?> record, Exception ex) {
        jdbcTemplate.update(
                """
                insert into subscription.kafka_consumer_failures
                    (topic, partition, record_offset, record_key, payload, exception_class, exception_message)
                values (?, ?, ?, ?, ?, ?, ?)
                """,
                record.topic(),
                record.partition(),
                record.offset(),
                toJson(record.key()),
                toJson(record.value()),
                ex.getClass().getName(),
                ex.getMessage()
        );
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return String.valueOf(value);
        }
    }
}
