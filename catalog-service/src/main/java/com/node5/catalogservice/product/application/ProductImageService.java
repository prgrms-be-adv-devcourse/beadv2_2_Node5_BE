package com.node5.catalogservice.product.application;

import org.springframework.stereotype.Service;

import com.node5.catalogservice.config.s3.S3Properties;
import com.node5.catalogservice.product.application.dto.PresignedUrlInfo;
import com.node5.catalogservice.product.application.port.S3PresignedUrlPort;
import com.node5.catalogservice.product.exception.ProductErrorCode;
import com.node5.common.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductImageService {

	private final S3PresignedUrlPort s3PresignedUrlPort;
	private final S3Properties props;

	public PresignedUrlInfo createUploadUrl(String contentType) {
		validateContentType(contentType);
		return s3PresignedUrlPort.createPutObjectUrl(contentType, props.getTempPrefix());
	}

	private void validateContentType(String contentType) {
		if (contentType == null || contentType.isBlank()) {
			throw new BaseException(ProductErrorCode.UNSUPPORTED_IMAGE_CONTENT_TYPE);
		}
		if (!ImageUploadPolicy.ALLOWED_CONTENT_TYPES.contains(contentType)) {
			throw new BaseException(ProductErrorCode.UNSUPPORTED_IMAGE_CONTENT_TYPE);
		}
	}
}
