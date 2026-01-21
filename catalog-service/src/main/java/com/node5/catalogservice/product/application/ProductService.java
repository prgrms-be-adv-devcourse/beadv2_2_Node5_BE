package com.node5.catalogservice.product.application;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.node5.catalogservice.product.application.dto.ProductCommand;
import com.node5.catalogservice.product.application.dto.ProductInfo;
import com.node5.catalogservice.product.application.dto.ProductUpdateCommand;
import com.node5.catalogservice.product.application.mapper.ProductEmbeddingEventMapper;
import com.node5.catalogservice.product.application.mapper.ProductIndexEventMapper;
import com.node5.catalogservice.product.application.port.ProductDiscontinuedEventPort;
import com.node5.catalogservice.product.application.port.ProductEmbeddingEventPort;
import com.node5.catalogservice.product.application.port.ProductIndexEventPort;
import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductRepository;
import com.node5.catalogservice.product.domain.ProductStatus;
import com.node5.catalogservice.product.exception.ProductErrorCode;
import com.node5.catalogservice.shop.client.ShopOwnershipClient;
import com.node5.common.event.ProductDiscontinuedEvent;
import com.node5.common.exception.BaseException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductIndexEventPort productIndexEventPort;
	private final ProductEmbeddingEventPort productEmbeddingEventPort;
	private final ProductDiscontinuedEventPort productDiscontinuedEventPort;
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
		productIndexEventPort.publish(ProductIndexEventMapper.forCreate(saved));
		productEmbeddingEventPort.publish(ProductEmbeddingEventMapper.from(saved));
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

	private Product getProductOrThrow(UUID productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new BaseException(ProductErrorCode.PRODUCT_NOT_FOUND));
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
}
