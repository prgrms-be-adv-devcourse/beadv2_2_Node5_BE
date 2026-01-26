package com.node5.catalogservice.product.infrastructure.s3;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.node5.catalogservice.config.s3.S3Properties;
import com.node5.catalogservice.product.application.dto.PresignedUrlInfo;
import com.node5.catalogservice.product.application.port.S3PresignedUrlPort;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@RequiredArgsConstructor
public class S3PresignedUrlAdapter implements S3PresignedUrlPort {

	private final S3Presigner s3Presigner;
	private final S3Properties props;

	@Override
	public PresignedUrlInfo createPutObjectUrl(String contentType, String keyPrefix) {
		String key = keyPrefix + UUID.randomUUID();

		PutObjectRequest objectRequest = PutObjectRequest.builder()
			.bucket(props.getBucket())
			.key(key)
			.contentType(contentType)
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofSeconds(props.getPresignedUrlExpirationSeconds()))
			.putObjectRequest(objectRequest)
			.build();

		PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

		return new PresignedUrlInfo(presignedRequest.url().toString(), key);
	}
}
