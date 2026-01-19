package com.node5.supportservice.search.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Getter;

@Getter
@Document(indexName = "#{@productIndexName}", createIndex = false)
public class ProductDocument {

	@Id
	private String productId;

	@Field(type = FieldType.Keyword)
	private String shopId;

	@Field(type = FieldType.Text)
	private String name;

	@Field(
		name = "name_autocomplete",
		type = FieldType.Text,
		analyzer = "autocomplete_index",
		searchAnalyzer = "autocomplete_search"
	)
	private String nameAutocomplete;

	@Field(type = FieldType.Keyword)
	private String category;

	@Field(type = FieldType.Keyword)
	private String thumbnailKey;

	@Field(type = FieldType.Long)
	private Long price;

	@Field(type = FieldType.Keyword)
	private String status;

	@Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdAt;

	protected ProductDocument() {
	}

	public ProductDocument(
		String id,
		String shopId,
		String name,
		String nameAutocomplete,
		String category,
		String thumbnailKey,
		Long price,
		String status,
		LocalDateTime createdAt
	) {
		this.productId = id;
		this.shopId = shopId;
		this.name = name;
		this.nameAutocomplete = nameAutocomplete;
		this.category = category;
		this.thumbnailKey = thumbnailKey;
		this.price = price;
		this.status = status;
		this.createdAt = createdAt;
	}
}
