package com.node5.catalogservice.product.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.node5.catalogservice.product.application.ProductDiscontinueService;
import com.node5.common.event.ShopDeletedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaShopDeletedEventConsumer {

	private final ProductDiscontinueService productDiscontinueService;

	@KafkaListener(
		topics = "${app.kafka.topics.shop-deleted}",
		groupId = "${spring.kafka.consumer.group-id:catalog-service-product}"
	)
	public void consume(ShopDeletedEvent event) {

		if (event == null || event.shopId() == null) {
			log.warn("가게 삭제 이벤트가 null이거나 shopId가 없습니다.");
			return;
		}

		String shopId = event.shopId().toString();

		log.info("Kafka 가게 삭제 이벤트 수신, shopId={}", shopId);

		try {
			int updatedCount = productDiscontinueService.discontinueByShopId(event.shopId());
			log.info("가게 상품 영구 중지 완료, shopId={}, updatedCount={}", shopId, updatedCount);

		} catch (Exception e) {
			log.error("가게 상품 영구 중지 실패, shopId={}", shopId, e);
			throw e;
		}
	}
}
