package com.node5.catalogservice.product.infrastructure.s3;

import org.springframework.stereotype.Component;

import com.node5.catalogservice.config.s3.S3Properties;
import com.node5.catalogservice.product.application.dto.S3ObjectMeta;
import com.node5.catalogservice.product.application.port.S3ObjectMetaPort;
import com.node5.catalogservice.product.exception.ImageErrorCode;
import com.node5.common.exception.BaseException;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@Component
@RequiredArgsConstructor
public class S3ObjectMetaAdapter implements S3ObjectMetaPort {

	private final S3Client s3Client;
	private final S3Properties props;

	@Override
	public S3ObjectMeta head(String key) {
		try {
			HeadObjectResponse response = s3Client.headObject(
				b -> b.bucket(props.getBucket()).key(key)
			);

			return new S3ObjectMeta(
				response.contentType(),
				response.contentLength()
			);

		} catch (NoSuchKeyException e) {
			throw new BaseException(ImageErrorCode.IMAGE_NOT_FOUND);
		}
	}
}
