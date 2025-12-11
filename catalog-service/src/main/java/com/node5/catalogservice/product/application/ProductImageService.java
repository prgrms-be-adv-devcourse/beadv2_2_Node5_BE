package com.node5.catalogservice.product.application;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.node5.catalogservice.product.application.dto.PresignedUrlInfo;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class ProductImageService {

	private final S3Presigner s3Presigner;

	@Value("${app.s3.bucket}")
	private String bucket;

	@Value("${app.s3.presigned-url-expiration-seconds:600}")
	private long expirationSeconds;

	public PresignedUrlInfo createUploadUrl(String fileName, String contentType) {
		String key = "product/" + UUID.randomUUID() + "-" + fileName;

		PutObjectRequest objectRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.contentType(contentType)
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofSeconds(expirationSeconds))
			.putObjectRequest(objectRequest)
			.build();

		PresignedPutObjectRequest presignedRequest =
			s3Presigner.presignPutObject(presignRequest);

		String url = presignedRequest.url().toString();

		return new PresignedUrlInfo(url, key);
	}
}
