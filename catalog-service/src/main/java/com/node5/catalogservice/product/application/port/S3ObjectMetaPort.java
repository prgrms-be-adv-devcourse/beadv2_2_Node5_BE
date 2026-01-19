package com.node5.catalogservice.product.application.port;

import com.node5.catalogservice.product.application.dto.S3ObjectMeta;

public interface S3ObjectMetaPort {
	S3ObjectMeta head(String key);
}
