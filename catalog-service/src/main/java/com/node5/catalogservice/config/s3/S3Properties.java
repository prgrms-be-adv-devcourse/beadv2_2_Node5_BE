package com.node5.catalogservice.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.s3")
public class S3Properties {
	private String bucket;
	private String region;
	private long presignedUrlExpirationSeconds;
	private String tempPrefix;
	private String productPrefix;
	private long maxImageBytes;
}
