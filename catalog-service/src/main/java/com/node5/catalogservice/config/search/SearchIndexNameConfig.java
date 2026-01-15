package com.node5.catalogservice.config.search;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SearchIndexProperties.class)
public class SearchIndexNameConfig {

	@Bean(name = "productIndexName")
	public String productIndexName(SearchIndexProperties props) {
		return props.getProduct();
	}
}
