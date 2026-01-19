package com.node5.catalogservice.product.application.dto;

public record S3ObjectMeta(
	String contentType,
	long contentLength
) {
}
