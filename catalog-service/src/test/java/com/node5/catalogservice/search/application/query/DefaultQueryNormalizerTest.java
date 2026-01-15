package com.node5.catalogservice.search.application.query;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DefaultQueryNormalizerTest {

	private final DefaultQueryNormalizer normalizer = new DefaultQueryNormalizer();

	@Test
	void 구분자_통일_공백정규화_영문소문자_적용된다() {
		assertThat(normalizer.normalize("  Raspberry/__Choco-Cake  "))
			.isEqualTo("raspberry choco cake");
	}

	@Test
	void 탭과_개행은_공백하나로_정규화된다() {
		assertThat(normalizer.normalize("딸기\t3단\n케이크"))
			.isEqualTo("딸기 3단 케이크");
	}

	@Test
	void null_또는_blank는_빈문자열을_반환한다() {
		assertThat(normalizer.normalize(null)).isEqualTo("");
		assertThat(normalizer.normalize("   ")).isEqualTo("");
	}

	@Test
	void 최대길이를_초과하면_truncate한다() {
		String longText = "피스타치오티라미수".repeat(30);
		String normalized = normalizer.normalize(longText);

		assertThat(normalized).hasSize(64);
		assertThat(normalized).isEqualTo("피스타치오티라미수".repeat(30).substring(0, 64));
	}
}
