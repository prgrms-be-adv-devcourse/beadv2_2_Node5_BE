package com.node5.catalogservice.product.application;

import java.util.HashMap;
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
import com.node5.catalogservice.product.presentation.dto.ProductIndexSummaryListResponse;
import com.node5.catalogservice.product.presentation.dto.ProductIndexSummaryResponse;
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
	public List<UUID> getProductIdsByShopIds(List<UUID> shopIds) {
		if (shopIds == null || shopIds.isEmpty()) {
			return List.of();
		}
		List<UUID> distinctShopIds = shopIds.stream().distinct().toList();
		return productRepository.findIdsByShopIdIn(distinctShopIds);
	}

	@Transactional(readOnly = true)
	public boolean isReviewable(UUID productId) {
		return productRepository.findByIdAndStatus(productId, ProductStatus.ON_SALE).isPresent();
	}


	@Transactional(readOnly = true)
	public ProductIndexSummaryListResponse getProductIndexSummaries(List<UUID> productIds) {
		if (productIds == null || productIds.isEmpty()) {
			return new ProductIndexSummaryListResponse(List.of());
		}

		Map<UUID, Integer> order = new HashMap<>();
		for (int i = 0; i < productIds.size(); i++) order.put(productIds.get(i), i);

		List<UUID> distinctIds = productIds.stream().distinct().toList();
		List<Product> products = productRepository.findAllByIdInAndStatus(distinctIds, ProductStatus.ON_SALE);

		List<ProductIndexSummaryResponse> summaries = products.stream()
			.map(p -> new ProductIndexSummaryResponse(
				p.getId(),
				p.getShopId(),
				p.getName(),
				buildNameAutocomplete(p.getName()),
				p.getCategory().name(),
				p.getThumbnailKey(),
				toLongPrice(p.getPrice()),
				p.getStatus().name(),
				p.getCreatedAt(),
				p.getModifiedAt()
			))
			.sorted(java.util.Comparator.comparingInt(s -> order.getOrDefault(s.productId(), Integer.MAX_VALUE)))
			.toList();

		return new ProductIndexSummaryListResponse(summaries);
	}

	private String buildNameAutocomplete(String name) {
		return name;
	}

	private long toLongPrice(java.math.BigDecimal price) {
		if (price == null) return 0L;
		return price.longValue();
	}
}
