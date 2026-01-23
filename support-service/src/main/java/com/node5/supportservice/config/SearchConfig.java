package com.node5.supportservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
	SearchIndexProperties.class,
	SearchReindexProperties.class,
	SearchSponsoredProperties.class
})
public class SearchConfig {

	@Bean(name = "productIndexName")
	public String productIndexName(SearchIndexProperties props) {
		return props.getProduct();
	}
}
