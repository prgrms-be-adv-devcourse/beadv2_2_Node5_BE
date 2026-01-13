package com.node5.catalogservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EnabledIfSystemProperty(named = "integrationTest", matches = "true")
class CatalogServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
