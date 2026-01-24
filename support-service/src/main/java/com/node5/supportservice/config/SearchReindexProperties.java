package com.node5.supportservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.search.reindex")
public class SearchReindexProperties {
	private int pageSize = 500;
}
