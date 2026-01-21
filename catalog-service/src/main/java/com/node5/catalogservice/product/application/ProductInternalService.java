package com.node5.catalogservice.product.application;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductRepository;
import com.node5.catalogservice.product.domain.ProductStatus;
import com.node5.catalogservice.product.exception.ProductErrorCode;
import com.node5.common.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductInternalService {

	private final ProductRepository productRepository;

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

		return products.stream().collect(Collectors.toMap(Product::getId, Product::getShopId));
	}

	@Transactional(readOnly = true)
	public List<Product> getProductsByIds(List<UUID> productIds) {
		if (productIds == null || productIds.isEmpty()) {
			return List.of();
		}
		return productRepository.findAllByIdInAndStatus(productIds, ProductStatus.ON_SALE);
	}

	@Transactional(readOnly = true)
	public List<UUID> getOnSaleProductIds(Pageable pageable) {
		return productRepository.findByStatus(ProductStatus.ON_SALE, pageable)
			.map(Product::getId)
			.getContent();
	}

	@Transactional(readOnly = true)
	public boolean isReviewable(UUID productId) {
		return productRepository.findByIdAndStatus(productId, ProductStatus.ON_SALE).isPresent();
	}
}
