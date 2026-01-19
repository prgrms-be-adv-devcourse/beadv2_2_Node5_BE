package com.node5.catalogservice.product.infrastructure.s3;

import org.springframework.stereotype.Component;

import com.node5.catalogservice.config.s3.S3Properties;
import com.node5.catalogservice.product.application.port.S3ObjectPromotionPort;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;

@Component
@RequiredArgsConstructor
public class S3ObjectPromotionAdapter implements S3ObjectPromotionPort {

	private final S3Client s3Client;
	private final S3Properties props;

	@Override
	public void copy(String sourceKey, String targetKey) {
		s3Client.copyObject(b -> b
			.sourceBucket(props.getBucket())
			.sourceKey(sourceKey)
			.destinationBucket(props.getBucket())
			.destinationKey(targetKey)
		);
	}

	@Override
	public void delete(String key) {
		s3Client.deleteObject(b -> b
			.bucket(props.getBucket())
			.key(key)
		);
	}
}
