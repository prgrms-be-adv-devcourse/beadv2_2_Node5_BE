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
 * 상품(Product) 도메인의 비즈니스 로직을 담당합니다.
 *
 * - 상품 조회 / 생성 / 수정 / 상태 변경
 * - 판매자 소유권 검증 (Shop-Service 연동)
 * - 상품 변경 시 검색 색인을 위한 Kafka 이벤트 발행
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
	 * @param pageable 페이징 및 정렬 정보
	 * @return 판매 중 상품 목록
	 */
	public Page<ProductInfo> getOnSaleProducts(Pageable pageable) {
		return productRepository.findByStatus(ProductStatus.ON_SALE, pageable)
			.map(ProductInfo::from);
	}

	/**
	 * 판매 중(ON_SALE) 상품을 단건 조회합니다.
	 *
	 * @param id 상품 ID
	 * @return 판매 중 상품 정보
	 */
	public ProductInfo getOnSaleProduct(UUID id) {
		return ProductInfo.from(getOnSaleProductOrThrow(id));
	}

	/**
	 * 전체 상품 목록을 페이징 조회합니다.
	 *
	 * @param pageable 페이징 및 정렬 정보
	 * @return 전체 상품 목록
	 */
	public Page<ProductInfo> getProducts(Pageable pageable) {
		return productRepository.findAll(pageable)
			.map(ProductInfo::from);
	}

	/**
	 * 상품을 생성합니다.
	 *
	 * - 요청자가 해당 상점의 소유자인지 검증합니다.
	 * - 생성 후 검색 색인을 위한 Kafka 이벤트를 발행합니다.
	 *
	 * @param memberId 요청자 ID
	 * @param command 상품 생성 정보
	 * @return 생성된 상품 정보
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
	 *
	 * - 상품 존재 여부 확인
	 * - 상점 소유권 검증
	 * - 수정 후 Kafka 업데이트 이벤트 발행
	 *
	 * @param memberId 요청자 ID
	 * @param productId 상품 ID
	 * @param command 수정 정보
	 * @return 수정된 상품 정보
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
	 * 상품의 판매 상태를 변경합니다.
	 *
	 * 요청자는 상품이 속한 상점의 소유자여야 합니다.
	 *
	 * @param memberId 요청자 ID
	 * @param productId 상품 ID
	 * @param status 변경할 상태
	 * @return 상태가 변경된 상품 정보
	 */
	@Transactional
	public ProductInfo updateStatus(UUID memberId, UUID productId, ProductStatus status) {
		Product product = getProductOrThrow(productId);

		validateShopOwnership(memberId, product.getShopId());

		product.changeStatus(status);

		Product saved = productRepository.save(product);
		productIndexProducer.sendProductUpdateEvent(saved);

		return ProductInfo.from(saved);
	}

	/**
	 * 상품을 판매 중단(DISCONTINUED) 처리합니다.
	 *
	 * @param id 상품 ID
	 */
	public void discontinueProduct(UUID id) {
		Product product = getProductOrThrow(id);

		product.discontinue();
		productRepository.save(product);
	}

	private Product getProductOrThrow(UUID productId) {
		return productRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);
	}

	private Product getOnSaleProductOrThrow(UUID productId) {
		return productRepository.findByIdAndStatus(productId, ProductStatus.ON_SALE)
			.orElseThrow(OnSaleProductNotFoundException::new);
	}

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
