package com.node5.catalogservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

	@Bean
	public S3Presigner s3Presigner(
		@Value("${app.s3.region:ap-northeast-2}") String region
	) {
		return S3Presigner.builder()
			.region(Region.of(region))
			.build();
	}
}
