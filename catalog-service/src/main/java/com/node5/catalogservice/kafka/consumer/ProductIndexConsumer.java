package com.node5.catalogservice.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.node5.catalogservice.kafka.dto.ProductIndexEvent;
import com.node5.catalogservice.search.domain.ProductDocument;
import com.node5.catalogservice.search.infrastructure.ProductSearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

		String productId = event.productId().toString();

		log.info("Kafka 상품 색인 이벤트 수신, productId={}, type={}",
			productId, event.type());

		try {
			ProductDocument document = new ProductDocument(
				productId,
				event.shopId().toString(),
				event.name(),
				event.category(),
				event.thumbnailUrl(),
				event.price(),
				event.status(),
				event.createdAt()
			);

			productSearchRepository.save(document);

			log.info("ES 상품 색인 완료, productId={}", productId);

		} catch (Exception e) {
			log.error("ES 상품 색인 실패, productId={}", productId, e);
			throw e;
		}
	}
}
