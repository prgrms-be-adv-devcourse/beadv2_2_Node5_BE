package com.node5.catalogservice.product.presentation.dto;

public record PresignedUrlResponse(
	String url,
	String key
) {
}
