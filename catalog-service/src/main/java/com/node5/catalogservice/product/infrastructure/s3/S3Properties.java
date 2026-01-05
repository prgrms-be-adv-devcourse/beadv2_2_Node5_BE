package com.node5.catalogservice.product.infrastructure.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.s3")
@Component
public class S3Properties {
	private String bucket;
	private String region = "ap-northeast-2";
	private long presignedUrlExpirationSeconds = 600;
}
