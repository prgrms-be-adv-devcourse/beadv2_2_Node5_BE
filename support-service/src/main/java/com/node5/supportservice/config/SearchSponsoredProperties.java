package com.node5.supportservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.search.sponsored")
public class SearchSponsoredProperties {
	private int limit = 3;
}
