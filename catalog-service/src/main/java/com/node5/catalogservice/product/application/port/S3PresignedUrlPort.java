package com.node5.catalogservice.product.application.port;

import com.node5.catalogservice.product.application.dto.PresignedUrlInfo;

public interface S3PresignedUrlPort {
	PresignedUrlInfo createPutObjectUrl(String contentType, String keyPrefix);
}
