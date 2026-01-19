package com.node5.catalogservice.product.application.port;

public interface S3ObjectPromotionPort {
	void copy(String sourceKey, String targetKey);
	void delete(String key);
}
