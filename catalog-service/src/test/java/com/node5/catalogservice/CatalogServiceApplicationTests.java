package com.node5.catalogservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
	"spring.cloud.config.enabled=false",
	"spring.cloud.config.fail-fast=false",
	"spring.config.import="
})
@ActiveProfiles("test")
@DisabledIfEnvironmentVariable(
	named = "CI",
	matches = "true",
	disabledReason = "CI 환경에서는 전체 컨텍스트 로딩 테스트를 건너뜁니다."
)
class CatalogServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
