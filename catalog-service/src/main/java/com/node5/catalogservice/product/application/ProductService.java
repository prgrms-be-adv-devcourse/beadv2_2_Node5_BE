package com.node5.catalogservice.product.application;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
 * - 상품 변경 시 검색 색인 동기화를 위한 Kafka 이벤트를 발행
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

	@Transactional
	public ProductInfo updateStatus(UUID memberId, UUID productId, ProductStatus status) {
		Product product = getProductOrThrow(productId);
		validateShopOwnership(memberId, product.getShopId());

		product.changeStatus(status);

		Product saved = productRepository.save(product);
		productIndexProducer.sendProductUpdateEvent(saved);

		return ProductInfo.from(saved);
	}

	@Transactional
	public void discontinueProduct(UUID memberId, UUID productId) {
		Product product = getProductOrThrow(productId);
		validateShopOwnership(memberId, product.getShopId());

		product.discontinue();

		Product saved = productRepository.save(product);
		productIndexProducer.sendProductUpdateEvent(saved);
	}

	public Page<ProductInfo> getProductsByShop(UUID memberId, UUID shopId, Pageable pageable) {
		validateShopOwnership(memberId, shopId);

		return productRepository.findByShopId(shopId, pageable)
			.map(ProductInfo::from);
	}

	/**
	 * 상품 ID 목록에 대해 상품의 상점 ID를 조회합니다.
	 * <p>
	 * - 요청된 모든 상품이 존재해야 하며<br>
	 * - 하나라도 존재하지 않으면 예외를 반환합니다.
	 */
	@Transactional(readOnly = true)
	public Map<UUID, UUID> getShopIdsByProductIds(List<UUID> productIds) {

		if (productIds == null || productIds.isEmpty()) {
			return Map.of();
		}

		List<UUID> distinctIds = productIds.stream().distinct().toList();

		List<Product> products = productRepository.findAllByIdIn(distinctIds);

		if (products.size() != distinctIds.size()) {
			throw new ProductNotFoundException();
		}

		return products.stream()
			.collect(Collectors.toMap(Product::getId, Product::getShopId));
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
