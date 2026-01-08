package com.node5.catalogservice.search.infrastructure.elasticsearch;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SearchIndexProperties.class)
public class SearchIndexNameConfig {

	@Bean
	public String productIndexName(SearchIndexProperties props) {
		return props.getProduct();
	}
}
