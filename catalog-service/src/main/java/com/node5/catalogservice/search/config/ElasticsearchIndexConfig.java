package com.node5.catalogservice.search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchIndexConfig {

	@Value("${app.search.product-index-name:products}")
	private String indexName;

	@Bean
	public String productIndexName() {
		return indexName; // @Document에서 #{@productIndexName} 로 참조
	}
}
