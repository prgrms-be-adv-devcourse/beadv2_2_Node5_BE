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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductIndexProducer productIndexProducer;

	public Page<ProductInfo> getOnSaleProducts(Pageable pageable) {
		Page<Product> page = productRepository.findByStatus(ProductStatus.ON_SALE, pageable);
		return page.map(ProductInfo::from);
	}

	public ProductInfo getOnSaleProduct(UUID id) {
		Product product = productRepository.findByIdAndStatus(id, ProductStatus.ON_SALE)
			.orElseThrow(() -> new IllegalArgumentException("판매 중인 상품이 아니거나 존재하지 않습니다. id=" + id));

		return ProductInfo.from(product);
	}

	public Page<ProductInfo> getProducts(Pageable pageable) {
		Page<Product> page = productRepository.findAll(pageable);
		return page.map(ProductInfo::from);
	}

	@Transactional
	public ProductInfo createProduct(ProductCommand command) {
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

	public ProductInfo updateProduct(UUID id, ProductUpdateCommand command) {
		Product product = productRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Product not found. id=" + id));

		product.applyPatch(
			command.name(),
			command.description(),
			command.price(),
			command.stock(),
			command.category(),
			command.thumbnailUrl()
		);

		Product saved = productRepository.save(product);
		return ProductInfo.from(saved);
	}

	public ProductInfo updateStatus(UUID id, ProductStatus status) {
		Product product = productRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Product not found. id=" + id));

		product.changeStatus(status);
		Product saved = productRepository.save(product);
		return ProductInfo.from(saved);
	}

	public void discontinueProduct(UUID id) {
		Product product = productRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Product not found. id=" + id));

		product.discontinue();
		productRepository.save(product);
	}
}
