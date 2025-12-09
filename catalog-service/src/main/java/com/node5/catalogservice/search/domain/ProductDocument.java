package com.node5.catalogservice.search.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.node5.catalogservice.product.domain.Product;

import lombok.Getter;

@Getter
@Document(indexName = "products")
public class ProductDocument {

	@Id
	private String productId;   // ES 문서 ID = 실제 상품 ID

	@Field(type = FieldType.Text)
	private String name;

	@Field(type = FieldType.Keyword)
	private String category;

	@Field(type = FieldType.Long)
	private Long price;

	@Field(type = FieldType.Keyword)
	private String status; // ON_SALE, HIDDEN, DISCONTINUED

	protected ProductDocument() {
	}

	public ProductDocument(String productId, String name, String category, Long price, String status) {
		this.productId = productId;
		this.name = name;
		this.category = category;
		this.price = price;
		this.status = status;
	}

	public static ProductDocument from(Product product) {
		return new ProductDocument(
			product.getId().toString(),
			product.getName(),
			product.getCategory(),
			product.getPrice().longValue(), // BigDecimal → long
			product.getStatus().name()
		);
	}
}
