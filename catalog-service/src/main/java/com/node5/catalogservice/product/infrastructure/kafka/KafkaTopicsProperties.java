package com.node5.catalogservice.product.infrastructure.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.search.kafka.topics")
public class KafkaTopicsProperties {
	private String productIndex;
}
