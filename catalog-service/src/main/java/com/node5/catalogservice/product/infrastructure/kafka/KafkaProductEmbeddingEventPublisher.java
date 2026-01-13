package com.node5.catalogservice.product.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.node5.catalogservice.config.kafka.KafkaTopicsProperties;
import com.node5.catalogservice.product.application.port.ProductEmbeddingEventPort;
import com.node5.common.event.ProductEmbeddingEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProductEmbeddingEventPublisher implements ProductEmbeddingEventPort {

	private final KafkaTemplate<String, ProductEmbeddingEvent> kafkaTemplate;
	private final KafkaTopicsProperties topics;

	@Override
	public void publish(ProductEmbeddingEvent event) {
		send(event);
	}

	private void send(ProductEmbeddingEvent event) {
		String topic = topics.getProductEmbedding();
		String key = event.productId().toString();

		kafkaTemplate.send(topic, key, event)
			.whenComplete((result, ex) -> {
				if (ex != null) {
					log.error(
						"Kafka 상품 임베딩 이벤트 발행 실패, topic={}, key={}, productId={}, status={}, modifiedAt={}",
						topic, key, event.productId(), event.status(), event.modifiedAt(), ex
					);
				} else {
					log.info(
						"Kafka 상품 임베딩 이벤트 발행 성공, topic={}, key={}, productId={}, status={}, modifiedAt={}, partition={}, offset={}",
						topic, key, event.productId(), event.status(), event.modifiedAt(),
						result.getRecordMetadata().partition(),
						result.getRecordMetadata().offset()
					);
				}
			});
	}
}
