package com.node5.catalogservice.product.application.dto;

public record PresignedUrlInfo(
	String url,
	String tempKey
) {
}
