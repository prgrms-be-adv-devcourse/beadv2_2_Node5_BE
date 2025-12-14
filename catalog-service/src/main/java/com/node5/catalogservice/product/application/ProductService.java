package com.node5.catalogservice.product.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import lombok.RequiredArgsConstructor;

/**
 * 상품(Product) 도메인의 비즈니스 로직을 담당합니다.
 * <p>
 * - 상품 조회 / 생성 / 수정 / 상태 변경<br>
 * - 판매자 소유권 검증 (Shop-Service 연동)<br>
 * - 변경 발생 시 검색 색인 동기화를 위한 Kafka 이벤트 발행
 */
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductIndexProducer productIndexProducer;
	private final ShopServiceClient shopServiceClient;

	public Page<ProductInfo> getOnSaleProducts(Pageable pageable) {
		return productRepository.findByStatus(ProductStatus.ON_SALE, pageable)
			.map(ProductInfo::from);
	}

	public ProductInfo getOnSaleProduct(UUID id) {
		return ProductInfo.from(getOnSaleProductOrThrow(id));
	}

	public Page<ProductInfo> getProducts(Pageable pageable) {
		return productRepository.findAll(pageable)
			.map(ProductInfo::from);
	}

	/**
	 * 상품을 생성합니다.
	 * <p>
	 * - 상점 소유권 검증 (Shop-Service)<br>
	 * - 생성 후 검색 색인 이벤트 발행
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
	 * - 상품 존재 확인<br>
	 * - 상점 소유권 검증<br>
	 * - 수정 후 검색 색인 업데이트 이벤트 발행
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
	 * 상품 판매 상태를 변경합니다.
	 * <p>
	 * - 상점 소유권 검증<br>
	 * - 변경 후 검색 색인 업데이트 이벤트 발행
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
	 * <p>
	 * - 변경 후 검색 색인 업데이트 이벤트 발행
	 */
	@Transactional
	public void discontinueProduct(UUID id) {
		Product product = getProductOrThrow(id);

		product.discontinue();

		Product saved = productRepository.save(product);
		productIndexProducer.sendProductUpdateEvent(saved);
	}

	/**
	 * 특정 상점의 상품 목록을 조회합니다.
	 * <p>
	 * - 요청자가 해당 상점의 소유자인지 검증 후 조회
	 */
	public Page<ProductInfo> getProductsByShop(UUID memberId, UUID shopId, Pageable pageable) {
		validateShopOwnership(memberId, shopId);

		return productRepository.findByShopId(shopId, pageable)
			.map(ProductInfo::from);
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
