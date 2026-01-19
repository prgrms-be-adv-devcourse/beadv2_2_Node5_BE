package com.node5.catalogservice.config.s3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3PresignerConfig {

	@Bean
	public S3Presigner s3Presigner(S3Properties props) {
		return S3Presigner.builder()
			.region(Region.of(props.getRegion()))
			.build();
	}

	@Bean
	public S3Client s3Client(S3Properties props) {
		return S3Client.builder()
			.region(Region.of(props.getRegion()))
			.build();
	}
}
