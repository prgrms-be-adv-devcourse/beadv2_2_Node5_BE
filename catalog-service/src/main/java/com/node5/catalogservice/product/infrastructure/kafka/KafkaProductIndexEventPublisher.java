package com.node5.catalogservice.product.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.node5.catalogservice.product.application.port.ProductIndexEventPort;
import com.node5.catalogservice.product.event.ProductIndexEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProductIndexEventPublisher implements ProductIndexEventPort {

	private final KafkaTemplate<String, ProductIndexEvent> kafkaTemplate;
	private final KafkaTopicsProperties topics;

	@Override
	public void publish(ProductIndexEvent event) {
		send(event);
	}

	private void send(ProductIndexEvent event) {
		String topic = topics.getProductIndex();
		String key = event.productId().toString();

		kafkaTemplate.send(topic, key, event)
			.whenComplete((result, ex) -> {
				if (ex != null) {
					log.error(
						"Kafka 상품 색인 이벤트 발행 실패, topic={}, key={}, productId={}, type={}",
						topic, key, event.productId(), event.type(), ex
					);
				} else {
					log.info(
						"Kafka 상품 색인 이벤트 발행 성공, topic={}, key={}, productId={}, type={}, partition={}, offset={}",
						topic, key, event.productId(), event.type(),
						result.getRecordMetadata().partition(),
						result.getRecordMetadata().offset()
					);
				}
			});
	}
}
