package com.node5.catalogservice.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.node5.catalogservice.kafka.dto.ProductIndexEvent;
import com.node5.catalogservice.product.domain.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductIndexProducer {

	private final KafkaTemplate<String, ProductIndexEvent> kafkaTemplate;

	@Value("${app.search.kafka.topics.product-index:product-index-topic}")
	private String productIndexTopic;

	public void sendProductIndexEvent(Product product) {
		send(ProductIndexEvent.create(product));
	}

	public void sendProductUpdateEvent(Product product) {
		send(ProductIndexEvent.update(product));
	}

	private void send(ProductIndexEvent event) {
		String key = event.productId().toString();

		log.info("Kafka 상품 색인 이벤트 발행, topic={}, key={}",
			productIndexTopic, key);

		kafkaTemplate
			.send(productIndexTopic, key, event)
			.whenComplete((result, ex) -> {
				if (ex != null) {
					log.error("Kafka 상품 색인 이벤트 발행 실패, key={}", key, ex);
				} else {
					log.info("Kafka 상품 색인 이벤트 발행 성공, key={}, offset={}",
						key, result.getRecordMetadata().offset()
					);
				}
			});
	}
}
