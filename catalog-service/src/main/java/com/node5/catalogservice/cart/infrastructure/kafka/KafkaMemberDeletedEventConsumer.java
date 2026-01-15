package com.node5.catalogservice.cart.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.node5.catalogservice.cart.application.CartCleanupService;
import com.node5.common.event.MemberDeletedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMemberDeletedEventConsumer {

	private final CartCleanupService cartCleanupService;

	@KafkaListener(
		topics = "${app.kafka.topics.member-deleted}",
		groupId = "${spring.kafka.consumer.group-id:catalog-service-cart}"
	)
	public void consume(MemberDeletedEvent event) {

		if (event == null || event.memberId() == null) {
			log.warn("회원 탈퇴 이벤트가 null이거나 memberId가 없습니다.");
			return;
		}

		String memberId = event.memberId().toString();

		log.info("Kafka 회원 탈퇴 이벤트 수신, memberId={}", memberId);

		try {
			boolean cleaned = cartCleanupService.cleanupByMemberId(event.memberId());

			if (cleaned) {
				log.info("회원 장바구니 정리 완료, memberId={}", memberId);
			} else {
				log.info("회원 장바구니가 없어 정리 작업을 건너뜁니다., memberId={}", memberId);
			}

		} catch (Exception e) {
			log.error("회원 장바구니 정리 실패, memberId={}", memberId, e);
			throw e;
		}
	}
}
