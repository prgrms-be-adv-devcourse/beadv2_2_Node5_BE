package com.node5.catalogservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
	"spring.cloud.config.enabled=false",
	"spring.cloud.config.fail-fast=false",
	"spring.config.import="
})
@ActiveProfiles("test")
class CatalogServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
