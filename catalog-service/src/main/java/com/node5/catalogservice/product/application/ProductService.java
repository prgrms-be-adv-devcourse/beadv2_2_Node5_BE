package com.node5.catalogservice.product.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.node5.catalogservice.product.application.dto.ProductCommand;
import com.node5.catalogservice.product.application.dto.ProductInfo;
import com.node5.catalogservice.product.application.dto.ProductUpdateCommand;
import com.node5.catalogservice.product.application.port.ProductEmbeddingEventPort;
import com.node5.catalogservice.product.application.port.ProductIndexEventPort;
import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductRepository;
import com.node5.catalogservice.product.domain.ProductStatus;
import com.node5.catalogservice.product.event.ProductIndexEvent;
import com.node5.catalogservice.product.exception.ProductErrorCode;
import com.node5.catalogservice.shop.client.ShopOwnershipClient;
import com.node5.common.exception.BaseException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductIndexEventPort productIndexEventPort;
	private final ProductEmbeddingEventPort productEmbeddingEventPort;
	private final ShopOwnershipClient shopOwnershipClient;

	public Page<ProductInfo> getOnSaleProducts(Pageable pageable) {
		return productRepository.findByStatus(ProductStatus.ON_SALE, pageable)
			.map(ProductInfo::from);
	}

	public ProductInfo getOnSaleProduct(UUID id) {
		return ProductInfo.from(getOnSaleProductOrThrow(id));
	}

	@Transactional
	public ProductInfo createProduct(UUID memberId, UUID shopId, ProductCommand command) {
		validateShopOwnership(memberId, shopId);

		Product product = Product.create(
			shopId,
			command.name(),
			command.description(),
			command.price(),
			command.status(),
			command.category(),
			command.thumbnailKey()
		);

		Product saved = productRepository.save(product);
		productIndexEventPort.publish(ProductIndexEvent.create(saved));
		productEmbeddingEventPort.publish(toEmbeddingEvent(saved));
		return ProductInfo.from(saved);
	}

	@Transactional
	public ProductInfo updateProduct(UUID memberId, UUID productId, ProductUpdateCommand command) {
		Product product = getProductOrThrow(productId);
		validateShopOwnership(memberId, product.getShopId());

		product.applyUpdate(
			command.name(),
			command.description(),
			command.price(),
			command.category(),
			command.thumbnailKey()
		);

		Product saved = productRepository.save(product);
		productIndexEventPort.publish(ProductIndexEvent.update(saved));
		productEmbeddingEventPort.publish(toEmbeddingEvent(saved));
		return ProductInfo.from(saved);
	}

	@Transactional
	public ProductInfo updateStatus(UUID memberId, UUID productId, ProductStatus status) {
		Product product = getProductOrThrow(productId);
		validateShopOwnership(memberId, product.getShopId());

		product.changeStatus(status);

		Product saved = productRepository.save(product);
		productIndexEventPort.publish(ProductIndexEvent.update(saved));
		productEmbeddingEventPort.publish(toEmbeddingEvent(saved));
		return ProductInfo.from(saved);
	}

	@Transactional
	public void discontinueProduct(UUID memberId, UUID productId) {
		Product product = getProductOrThrow(productId);
		validateShopOwnership(memberId, product.getShopId());

		product.discontinue();

		Product saved = productRepository.save(product);
		productIndexEventPort.publish(ProductIndexEvent.update(saved));
		productEmbeddingEventPort.publish(toEmbeddingEvent(saved));
	}

	public Page<ProductInfo> getProductsByShop(UUID memberId, UUID shopId, Pageable pageable) {
		validateShopOwnership(memberId, shopId);

		return productRepository.findByShopId(shopId, pageable)
			.map(ProductInfo::from);
	}

	@Transactional(readOnly = true)
	public Map<UUID, UUID> getShopIdsByProductIds(List<UUID> productIds) {

		if (productIds == null || productIds.isEmpty()) {
			return Map.of();
		}

		List<UUID> distinctIds = productIds.stream().distinct().toList();

		List<Product> products = productRepository.findAllByIdIn(distinctIds);

		if (products.size() != distinctIds.size()) {
			throw new BaseException(ProductErrorCode.PRODUCT_NOT_FOUND);
		}

		return products.stream()
			.collect(Collectors.toMap(Product::getId, Product::getShopId));
	}

	@Transactional(readOnly = true)
	public List<Product> getProductsByIds(List<UUID> productIds) {
		if (productIds == null || productIds.isEmpty()) {
			return List.of();
		}
		return productRepository.findAllByIdInAndStatus(productIds, ProductStatus.ON_SALE);
	}

	private Product getProductOrThrow(UUID productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new BaseException(ProductErrorCode.PRODUCT_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public boolean isReviewable(UUID productId) {
		return productRepository.findByIdAndStatus(productId, ProductStatus.ON_SALE).isPresent();
	}

	private Product getOnSaleProductOrThrow(UUID productId) {
		return productRepository.findByIdAndStatus(productId, ProductStatus.ON_SALE)
			.orElseThrow(() -> new BaseException(ProductErrorCode.ON_SALE_PRODUCT_NOT_FOUND));
	}

	private void validateShopOwnership(UUID memberId, UUID shopId) {
		try {
			UUID ownerMemberId = shopOwnershipClient.getOwnerMemberId(shopId);

			if (!ownerMemberId.equals(memberId)) {
				throw new BaseException(ProductErrorCode.SHOP_FORBIDDEN);
			}
		} catch (FeignException.NotFound e) {
			throw new BaseException(ProductErrorCode.SHOP_NOT_FOUND);
		} catch (FeignException e) {
			throw new BaseException(ProductErrorCode.SHOP_SERVICE_UNAVAILABLE);
		}
	}

	private com.node5.common.event.ProductEmbeddingEvent toEmbeddingEvent(Product product) {
		return new com.node5.common.event.ProductEmbeddingEvent(
			UUID.randomUUID(),
			product.getId(),
			product.getName(),
			product.getDescription(),
			product.getCategory().name(),
			product.getStatus().name(),
			product.getModifiedAt(),
			LocalDateTime.now()
		);
	}
}
