package com.node5.catalogservice.product.application;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.node5.catalogservice.product.application.dto.PresignedUrlInfo;
import com.node5.catalogservice.product.application.port.S3PresignedUrlPort;
import com.node5.catalogservice.product.exception.ProductErrorCode;
import com.node5.common.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductImageService {

	private static final Set<String> ALLOWED_IMAGE_CONTENT_TYPES = Set.of(
		"image/png",
		"image/jpeg"
	);

	private final S3PresignedUrlPort s3PresignedUrlPort;

	public PresignedUrlInfo createUploadUrl(String contentType) {
		validateContentType(contentType);
		return s3PresignedUrlPort.createPutObjectUrl(contentType, "product/");
	}

	private void validateContentType(String contentType) {
		if (contentType == null || contentType.isBlank()) {
			throw new BaseException(ProductErrorCode.UNSUPPORTED_IMAGE_CONTENT_TYPE);
		}
		if (!ALLOWED_IMAGE_CONTENT_TYPES.contains(contentType)) {
			throw new BaseException(ProductErrorCode.UNSUPPORTED_IMAGE_CONTENT_TYPE);
		}
	}
}
