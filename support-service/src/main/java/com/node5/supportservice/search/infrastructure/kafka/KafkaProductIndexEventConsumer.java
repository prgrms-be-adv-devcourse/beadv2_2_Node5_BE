package com.node5.supportservice.search.infrastructure.kafka;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.node5.common.event.ProductIndexEvent;
import com.node5.supportservice.search.domain.ProductDocument;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProductIndexEventConsumer {

	private static final DateTimeFormatter ES_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	private final ElasticsearchOperations elasticsearchOperations;

	@KafkaListener(topics = "catalog-service.product-index.v1", groupId = "support-service")
	public void consume(ProductIndexEvent event) {

		String productId = event.productId().toString();

		log.info("Kafka 상품 색인 이벤트 수신, productId={}, type={}", productId, event.type());

		try {
			Map<String, Object> doc = new HashMap<>();
			doc.put("productId", productId);
			doc.put("shopId", event.shopId().toString());
			doc.put("name", event.name());
			doc.put("name_autocomplete", event.nameAutocomplete());
			doc.put("category", event.category());
			doc.put("thumbnailKey", event.thumbnailKey());
			doc.put("price", event.price());
			doc.put("status", event.status());
			doc.put("createdAt", event.createdAt().format(ES_DATE_TIME));
			doc.put("modifiedAt", event.modifiedAt().format(ES_DATE_TIME));

			UpdateQuery updateQuery = UpdateQuery.builder(productId.toString())
				.withDocument(Document.from(doc))
				.withDocAsUpsert(true)
				.build();

			var index = elasticsearchOperations.getIndexCoordinatesFor(ProductDocument.class);
			elasticsearchOperations.update(updateQuery, index);

			log.info("ES 상품 색인 완료, productId={}", productId);

		} catch (Exception e) {
			log.error("ES 상품 색인 실패, productId={}", productId, e);
			throw e;
		}
	}
}
