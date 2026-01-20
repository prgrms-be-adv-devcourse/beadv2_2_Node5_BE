package com.node5.supportservice.search.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.node5.common.event.ProductIndexEvent;
import com.node5.supportservice.search.domain.ProductDocument;
import com.node5.supportservice.search.infrastructure.ProductSearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProductIndexEventConsumer {

	private final ProductSearchRepository productSearchRepository;

	@KafkaListener(topics = "catalog-service.product-index.v1", groupId = "support-service")
	public void consume(ProductIndexEvent event) {

		String productId = event.productId().toString();

		log.info("Kafka 상품 색인 이벤트 수신, productId={}, type={}", productId, event.type());

		try {
			ProductDocument document = new ProductDocument(
				productId,
				event.shopId().toString(),
				event.name(),
				event.nameAutocomplete(),
				event.category(),
				event.thumbnailKey(),
				event.price(),
				event.status(),
				null,
				event.createdAt(),
				event.modifiedAt()
			);

			productSearchRepository.save(document);

			log.info("ES 상품 색인 완료, productId={}", productId);

		} catch (Exception e) {
			log.error("ES 상품 색인 실패, productId={}", productId, e);
			throw e;
		}
	}
}
