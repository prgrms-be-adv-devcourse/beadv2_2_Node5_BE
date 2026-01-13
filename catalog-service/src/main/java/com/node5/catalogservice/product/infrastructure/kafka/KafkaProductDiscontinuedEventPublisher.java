package com.node5.catalogservice.product.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.node5.catalogservice.config.kafka.KafkaTopicsProperties;
import com.node5.catalogservice.product.application.port.ProductDiscontinuedEventPort;
import com.node5.common.event.ProductDiscontinuedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProductDiscontinuedEventPublisher implements ProductDiscontinuedEventPort {

	private final KafkaTemplate<String, ProductDiscontinuedEvent> kafkaTemplate;
	private final KafkaTopicsProperties topics;

	@Override
	public void publish(ProductDiscontinuedEvent event) {
		String topic = topics.getProductDiscontinued();
		String key = event.productId().toString();

		kafkaTemplate.send(topic, key, event)
			.whenComplete((result, ex) -> {
				if (ex != null) {
					log.error("Kafka 상품 판매중단 이벤트 발행 실패, topic={}, key={}, productId={}",
						topic, key, event.productId(), ex);
				} else {
					log.info("Kafka 상품 판매중단 이벤트 발행 성공, topic={}, key={}, productId={}, partition={}, offset={}",
						topic, key, event.productId(),
						result.getRecordMetadata().partition(),
						result.getRecordMetadata().offset());
				}
			});
	}
}
