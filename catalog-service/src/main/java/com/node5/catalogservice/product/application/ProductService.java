package com.node5.catalogservice.product.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.node5.catalogservice.kafka.producer.ProductIndexProducer;
import com.node5.catalogservice.product.application.dto.ProductCommand;
import com.node5.catalogservice.product.application.dto.ProductInfo;
import com.node5.catalogservice.product.application.dto.ProductUpdateCommand;
import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductRepository;
import com.node5.catalogservice.product.domain.ProductStatus;
import com.node5.catalogservice.product.exception.OnSaleProductNotFoundException;
import com.node5.catalogservice.product.exception.ProductNotFoundException;
import com.node5.catalogservice.product.exception.ShopForbiddenException;
import com.node5.catalogservice.product.exception.ShopNotFoundException;
import com.node5.catalogservice.shop.client.ShopServiceClient;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * 상품 도메인의 핵심 비즈니스 로직을 담당하는 서비스 계층.
 * <p>
 * - 판매중 상품 조회/전체 조회 제공<br>
 * - 상품 생성/수정/상태 변경/판매 중단 처리<br>
 * - Shop-Service 연동을 통한 상점 소유권 검증 수행<br>
 * - Kafka 이벤트 발행을 통해 Elasticsearch 색인(동기화) 트리거
 */
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductIndexProducer productIndexProducer;
	private final ShopServiceClient shopServiceClient;

	/**
	 * 판매 중(ON_SALE) 상품 목록을 페이징 조회합니다.
	 *
	 * @param pageable 페이징/정렬 정보
	 * @return 판매 중 상품 목록(Page)
	 */
	public Page<ProductInfo> getOnSaleProducts(Pageable pageable) {
		return productRepository.findByStatus(ProductStatus.ON_SALE, pageable)
			.map(ProductInfo::from);
	}

	/**
	 * 판매 중(ON_SALE) 상품 단건을 조회합니다.
	 *
	 * @param id 상품 ID
	 * @return 판매 중 상품 정보
	 * @throws OnSaleProductNotFoundException 판매 중 상품이 아니거나 존재하지 않는 경우
	 */
	public ProductInfo getOnSaleProduct(UUID id) {
		return ProductInfo.from(getOnSaleProductOrThrow(id));
	}

	/**
	 * 전체 상품을 페이징 조회합니다. (운영/관리자 성격의 조회에 사용)
	 *
	 * @param pageable 페이징/정렬 정보
	 * @return 전체 상품 목록(Page)
	 */
	public Page<ProductInfo> getProducts(Pageable pageable) {
		return productRepository.findAll(pageable)
			.map(ProductInfo::from);
	}

	/**
	 * 상품을 생성합니다.
	 * <p>
	 * 생성 전 Shop-Service를 통해 요청자(memberId)가 해당 shopId의 소유자인지 검증합니다.
	 * 생성 성공 시 Elasticsearch 색인을 위해 Kafka 색인 이벤트를 발행합니다.
	 *
	 * @param memberId 요청자(판매자) ID
	 * @param command  상품 생성 커맨드
	 * @return 생성된 상품 정보
	 * @throws ShopNotFoundException   상점이 존재하지 않는 경우
	 * @throws ShopForbiddenException 요청자가 해당 상점의 소유자가 아닌 경우
	 */
	@Transactional
	public ProductInfo createProduct(UUID memberId, ProductCommand command) {
		validateShopOwnership(memberId, command.shopId());

		Product product = Product.create(
			command.shopId(),
			command.name(),
			command.description(),
			command.price(),
			command.stock(),
			command.status(),
			command.category(),
			command.thumbnailUrl()
		);

		Product saved = productRepository.save(product);
		productIndexProducer.sendProductIndexEvent(saved);

		return ProductInfo.from(saved);
	}

	/**
	 * 상품 정보를 수정합니다.
	 * <p>
	 * 수정 전 상품 존재 여부를 확인하고, Shop-Service를 통해 소유권을 검증합니다.
	 * 수정 성공 시 Elasticsearch 색인 동기화를 위해 Kafka 업데이트 이벤트를 발행합니다.
	 *
	 * @param memberId  요청자(판매자) ID
	 * @param productId 수정 대상 상품 ID
	 * @param command   상품 수정 커맨드
	 * @return 수정된 상품 정보
	 * @throws ProductNotFoundException 상품이 존재하지 않는 경우
	 * @throws ShopNotFoundException    상점이 존재하지 않는 경우
	 * @throws ShopForbiddenException  요청자가 해당 상점의 소유자가 아닌 경우
	 */
	@Transactional
	public ProductInfo updateProduct(UUID memberId, UUID productId, ProductUpdateCommand command) {
		Product product = getProductOrThrow(productId);

		validateShopOwnership(memberId, product.getShopId());

		product.applyPatch(
			command.name(),
			command.description(),
			command.price(),
			command.stock(),
			command.category(),
			command.thumbnailUrl()
		);

		Product saved = productRepository.save(product);
		productIndexProducer.sendProductUpdateEvent(saved);

		return ProductInfo.from(saved);
	}

	/**
	 * 상품 상태를 변경합니다. (예: ON_SALE, HIDDEN, DISCONTINUED)
	 *
	 * @param id     상품 ID
	 * @param status 변경할 상태
	 * @return 변경된 상품 정보
	 * @throws ProductNotFoundException 상품이 존재하지 않는 경우
	 */
	public ProductInfo updateStatus(UUID id, ProductStatus status) {
		Product product = getProductOrThrow(id);

		product.changeStatus(status);
		Product saved = productRepository.save(product);

		productIndexProducer.sendProductUpdateEvent(saved);

		return ProductInfo.from(saved);
	}

	/**
	 * 상품을 판매 중단(DISCONTINUED) 처리합니다.
	 *
	 * @param id 상품 ID
	 * @throws ProductNotFoundException 상품이 존재하지 않는 경우
	 */
	public void discontinueProduct(UUID id) {
		Product product = getProductOrThrow(id);

		product.discontinue();
		productRepository.save(product);
	}

	/**
	 * 상품을 ID로 조회하고, 존재하지 않으면 예외를 발생시킵니다.
	 *
	 * @param productId 상품 ID
	 * @return 조회된 상품 엔티티
	 * @throws ProductNotFoundException 상품이 존재하지 않는 경우
	 */
	private Product getProductOrThrow(UUID productId) {
		return productRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);
	}

	/**
	 * 판매 중(ON_SALE) 상품을 ID로 조회하고, 존재하지 않으면 예외를 발생시킵니다.
	 *
	 * @param productId 상품 ID
	 * @return 판매 중 상태의 상품 엔티티
	 * @throws OnSaleProductNotFoundException 판매 중 상품이 아니거나 존재하지 않는 경우
	 */
	private Product getOnSaleProductOrThrow(UUID productId) {
		return productRepository.findByIdAndStatus(productId, ProductStatus.ON_SALE)
			.orElseThrow(OnSaleProductNotFoundException::new);
	}

	/**
	 * Shop-Service를 통해 상점 존재 여부 및 소유권을 검증합니다.
	 *
	 * <ul>
	 *   <li>404 Not Found : 상점이 존재하지 않음</li>
	 *   <li>403 Forbidden : 요청자가 해당 상점의 소유자가 아님</li>
	 * </ul>
	 *
	 * @param memberId 요청자 ID
	 * @param shopId   상점 ID
	 * @throws ShopNotFoundException   상점이 존재하지 않는 경우
	 * @throws ShopForbiddenException 요청자가 해당 상점의 소유자가 아닌 경우
	 */
	private void validateShopOwnership(UUID memberId, UUID shopId) {
		try {
			shopServiceClient.getShopInfo(memberId, shopId);
		} catch (FeignException.NotFound e) {
			throw new ShopNotFoundException();
		} catch (FeignException.Forbidden e) {
			throw new ShopForbiddenException();
		}
	}
}
