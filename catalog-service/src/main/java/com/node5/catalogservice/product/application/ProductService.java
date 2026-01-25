package com.node5.catalogservice.product.application;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.node5.catalogservice.client.ShopOwnershipPort;
import com.node5.catalogservice.product.application.dto.ProductCommand;
import com.node5.catalogservice.product.application.dto.ProductInfo;
import com.node5.catalogservice.product.application.dto.ProductUpdateCommand;
import com.node5.catalogservice.product.application.mapper.ProductEmbeddingEventMapper;
import com.node5.catalogservice.product.application.mapper.ProductIndexEventMapper;
import com.node5.catalogservice.product.application.port.ProductDiscontinuedEventPort;
import com.node5.catalogservice.product.application.port.ProductEmbeddingEventPort;
import com.node5.catalogservice.product.application.port.ProductIndexEventPort;
import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductIdempotency;
import com.node5.catalogservice.product.domain.ProductIdempotencyRepository;
import com.node5.catalogservice.product.domain.ProductRepository;
import com.node5.catalogservice.product.domain.ProductStatus;
import com.node5.catalogservice.product.exception.ProductErrorCode;
import com.node5.common.event.ProductDiscontinuedEvent;
import com.node5.common.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductIdempotencyRepository productIdempotencyRepository;
	private final ProductIndexEventPort productIndexEventPort;
	private final ProductEmbeddingEventPort productEmbeddingEventPort;
	private final ProductDiscontinuedEventPort productDiscontinuedEventPort;
	private final ShopOwnershipPort shopOwnershipPort;

	public Page<ProductInfo> getOnSaleProducts(Pageable pageable) {
		return productRepository.findByStatus(ProductStatus.ON_SALE, pageable)
			.map(ProductInfo::from);
	}

	public ProductInfo getOnSaleProduct(UUID id) {
		return ProductInfo.from(getOnSaleProductOrThrow(id));
	}

	@Transactional
	public ProductInfo createProduct(UUID memberId, UUID shopId, ProductCommand command, String idempotencyKey) {
		validateShopOwnership(memberId, shopId);

		// Idempotency-Key 미포함 요청은 하위 호환을 위해 기존 방식으로 처리
		if (!StringUtils.hasText(idempotencyKey)) {
			return createProductInternal(shopId, command);
		}

		boolean started = productIdempotencyRepository.tryStartProcessing(idempotencyKey);

		if (!started) {
			ProductIdempotency existing = productIdempotencyRepository.findByKey(idempotencyKey)
				.orElseThrow(() -> new BaseException(ProductErrorCode.IDEMPOTENCY_DATA_CORRUPTED));

			return switch (existing.getStatus()) {
				case COMPLETED -> {
					UUID productId = existing.getProductId();
					if (productId == null) {
						throw new BaseException(ProductErrorCode.IDEMPOTENCY_DATA_CORRUPTED);
					}
					Product saved = productRepository.findById(productId)
						.orElseThrow(() -> new BaseException(ProductErrorCode.IDEMPOTENCY_DATA_CORRUPTED));
					yield ProductInfo.from(saved);
				}
				case PROCESSING -> throw new BaseException(ProductErrorCode.IDEMPOTENCY_REQUEST_IN_PROGRESS);
				case FAILED -> throw new BaseException(ProductErrorCode.IDEMPOTENCY_PREVIOUSLY_FAILED);
			};
		}

		try {
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

			ProductIdempotency idem = productIdempotencyRepository.findByKey(idempotencyKey)
				.orElseThrow(() -> new BaseException(ProductErrorCode.IDEMPOTENCY_DATA_CORRUPTED));
			idem.complete(saved.getId());
			productIdempotencyRepository.save(idem);

			productIndexEventPort.publish(ProductIndexEventMapper.forCreate(saved));
			productEmbeddingEventPort.publish(ProductEmbeddingEventMapper.from(saved));

			return ProductInfo.from(saved);

		} catch (RuntimeException e) {
			if (!(e instanceof BaseException)) {
				markIdempotencyFailed(idempotencyKey);
			}
			throw e;
		}
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
		productIndexEventPort.publish(ProductIndexEventMapper.forUpdate(saved));
		productEmbeddingEventPort.publish(ProductEmbeddingEventMapper.from(saved));
		return ProductInfo.from(saved);
	}

	@Transactional
	public ProductInfo updateStatus(UUID memberId, UUID productId, ProductStatus status) {
		Product product = getProductOrThrow(productId);
		validateShopOwnership(memberId, product.getShopId());

		product.changeStatus(status);

		Product saved = productRepository.save(product);
		productIndexEventPort.publish(ProductIndexEventMapper.forUpdate(saved));
		productEmbeddingEventPort.publish(ProductEmbeddingEventMapper.from(saved));
		return ProductInfo.from(saved);
	}

	@Transactional
	public void discontinueProduct(UUID memberId, UUID productId) {
		Product product = getProductOrThrow(productId);
		validateShopOwnership(memberId, product.getShopId());

		product.discontinue();

		Product saved = productRepository.save(product);
		productIndexEventPort.publish(ProductIndexEventMapper.forUpdate(saved));
		productEmbeddingEventPort.publish(ProductEmbeddingEventMapper.from(saved));

		productDiscontinuedEventPort.publish(
			new ProductDiscontinuedEvent(
				UUID.randomUUID(), saved.getId(), saved.getModifiedAt(), LocalDateTime.now()
			)
		);
	}

	public Page<ProductInfo> getProductsByShop(UUID memberId, UUID shopId, Pageable pageable) {
		validateShopOwnership(memberId, shopId);

		return productRepository.findByShopId(shopId, pageable)
			.map(ProductInfo::from);
	}

	private ProductInfo createProductInternal(UUID shopId, ProductCommand command) {
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
		productIndexEventPort.publish(ProductIndexEventMapper.forCreate(saved));
		productEmbeddingEventPort.publish(ProductEmbeddingEventMapper.from(saved));
		return ProductInfo.from(saved);
	}

	private void markIdempotencyFailed(String idempotencyKey) {
		Optional<ProductIdempotency> idemOpt = productIdempotencyRepository.findByKey(idempotencyKey);
		if (idemOpt.isEmpty()) {
			return;
		}
		ProductIdempotency idempotency = idemOpt.get();
		idempotency.fail();
		productIdempotencyRepository.save(idempotency);
	}

	private Product getProductOrThrow(UUID productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new BaseException(ProductErrorCode.PRODUCT_NOT_FOUND));
	}

	private Product getOnSaleProductOrThrow(UUID productId) {
		return productRepository.findByIdAndStatus(productId, ProductStatus.ON_SALE)
			.orElseThrow(() -> new BaseException(ProductErrorCode.ON_SALE_PRODUCT_NOT_FOUND));
	}

	private void validateShopOwnership(UUID memberId, UUID shopId) {
		UUID ownerMemberId = shopOwnershipPort.getOwnerMemberId(shopId);
		if (!ownerMemberId.equals(memberId)) {
			throw new BaseException(ProductErrorCode.SHOP_FORBIDDEN);
		}
	}
}
