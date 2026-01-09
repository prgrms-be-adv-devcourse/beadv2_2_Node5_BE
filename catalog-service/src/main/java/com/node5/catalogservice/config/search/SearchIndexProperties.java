package com.node5.catalogservice.config.search;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.search.index")
public class SearchIndexProperties {
	private String product;
}
