package com.node5.catalogservice.product.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.node5.catalogservice.config.s3.S3Properties;
import com.node5.catalogservice.product.application.dto.PresignedUrlInfo;
import com.node5.catalogservice.product.application.dto.S3ObjectMeta;
import com.node5.catalogservice.product.application.port.S3ObjectMetaPort;
import com.node5.catalogservice.product.application.port.S3ObjectPromotionPort;
import com.node5.catalogservice.product.application.port.S3PresignedUrlPort;
import com.node5.catalogservice.product.exception.ImageErrorCode;
import com.node5.common.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductImageService {

	private final S3PresignedUrlPort s3PresignedUrlPort;
	private final S3ObjectMetaPort s3ObjectMetaPort;
	private final S3ObjectPromotionPort s3ObjectPromotionPort;
	private final S3Properties props;

	public PresignedUrlInfo createUploadUrl(String contentType) {
		validateContentType(contentType);
		return s3PresignedUrlPort.createPutObjectUrl(contentType, props.getTempPrefix());
	}

	public String confirm(String tempKey) {
		validateTempKey(tempKey);

		S3ObjectMeta meta = s3ObjectMetaPort.head(tempKey);
		validateUploadedMeta(meta);

		String productKey = props.getProductPrefix() + UUID.randomUUID();

		s3ObjectPromotionPort.copy(tempKey, productKey);
		s3ObjectPromotionPort.delete(tempKey);

		return productKey;
	}

	private void validateContentType(String contentType) {
		if (contentType == null || contentType.isBlank()) {
			throw new BaseException(ImageErrorCode.UNSUPPORTED_IMAGE_CONTENT_TYPE);
		}
		if (!ImageUploadPolicy.ALLOWED_CONTENT_TYPES.contains(contentType)) {
			throw new BaseException(ImageErrorCode.UNSUPPORTED_IMAGE_CONTENT_TYPE);
		}
	}

	private void validateTempKey(String tempKey) {
		if (tempKey == null || tempKey.isBlank()) {
			throw new BaseException(ImageErrorCode.INVALID_IMAGE_KEY);
		}
		if (!tempKey.startsWith(props.getTempPrefix())) {
			throw new BaseException(ImageErrorCode.INVALID_IMAGE_KEY);
		}
	}

	private void validateUploadedMeta(S3ObjectMeta meta) {
		if (meta.contentLength() <= 0) {
			throw new BaseException(ImageErrorCode.INVALID_IMAGE_KEY);
		}
		if (meta.contentLength() > props.getMaxImageBytes()) {
			throw new BaseException(ImageErrorCode.IMAGE_TOO_LARGE);
		}
		if (!ImageUploadPolicy.ALLOWED_CONTENT_TYPES.contains(meta.contentType())) {
			throw new BaseException(ImageErrorCode.UNSUPPORTED_IMAGE_CONTENT_TYPE);
		}
	}
}
