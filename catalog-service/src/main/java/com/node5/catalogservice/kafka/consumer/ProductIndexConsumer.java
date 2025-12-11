package com.node5.catalogservice.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.node5.catalogservice.kafka.dto.ProductIndexEvent;
import com.node5.catalogservice.search.domain.ProductDocument;
import com.node5.catalogservice.search.infrastructure.ProductSearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka 이벤트 받아서 ES 색인하기
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductIndexConsumer {

	private final ProductSearchRepository productSearchRepository;

	@KafkaListener(
		topics = "${app.search.kafka.topics.product-index:product-index-topic}",
		groupId = "${spring.kafka.consumer.group-id:catalog-service-search}"
	)
	public void consume(ProductIndexEvent event) {

		log.info("Kafka 상품 색인 이벤트 수신, id={}, name={}, status={}, type={}",
			event.productId(), event.name(), event.status(), event.type());

		// Kafka 이벤트 -> ES 문서로 변환
		ProductDocument document = new ProductDocument(
			event.productId().toString(),
			event.name(),
			event.category(),
			event.thumbnailUrl(),
			event.price(),
			event.status(),
			event.createdAt()
		);

		// ES 색인
		productSearchRepository.save(document);

		log.info("ES 상품 색인 완료, id={}, type={}", document.getProductId(), event.type());
	}
}
