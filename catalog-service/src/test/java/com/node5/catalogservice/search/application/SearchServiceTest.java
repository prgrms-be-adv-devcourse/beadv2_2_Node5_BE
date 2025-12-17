package com.node5.catalogservice.search.application;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.time.LocalDateTime;
import java.util.Comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.search.application.dto.ProductSearchCommand;
import com.node5.catalogservice.search.application.dto.ProductSearchResponse;
import com.node5.catalogservice.search.config.ElasticsearchIndexConfig;
import com.node5.catalogservice.search.domain.ProductDocument;
import com.node5.catalogservice.search.domain.ProductSearchSort;
import com.node5.catalogservice.search.infrastructure.ProductSearchRepository;

/**
 * NOTE:
 * 이 테스트는 실제 Elasticsearch 인덱스를 생성하므로
 * CI 환경에서는 실행되지 않도록 조건부로 비활성화되어 있습니다.
 */
@DataElasticsearchTest(properties = {
	"spring.cloud.config.enabled=false",
	"spring.cloud.config.fail-fast=false",
	"spring.config.import="
})
@ActiveProfiles("test")
@Import({SearchService.class, ElasticsearchIndexConfig.class})
@DisabledIfEnvironmentVariable(
	named = "CI",
	matches = "true",
	disabledReason = "CI 환경에서는 검색 테스트를 건너뜁니다."
)
public class SearchServiceTest {

	private static final PageRequest DEFAULT_PAGE = PageRequest.of(0, 10);

	@MockitoBean
	private JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@Autowired
	private ProductSearchRepository productSearchRepository;

	@Autowired
	private SearchService searchService;

	@Autowired
	private ElasticsearchOperations operations;

	@BeforeEach
	void setup() {
		IndexOperations indexOps = operations.indexOps(ProductDocument.class);

		if (indexOps.exists()) {
			indexOps.delete();
		}
		indexOps.create();
		indexOps.putMapping();
		indexOps.refresh();

		productSearchRepository.deleteAll();

		LocalDateTime base = LocalDateTime.of(2025, 1, 1, 0, 0);

		productSearchRepository.save(new ProductDocument(
			"1", "1", "테스트 상품", "TEST", "product/test-thumb-1.png",
			1000L, "ON_SALE", base.plusDays(1)
		));

		productSearchRepository.save(new ProductDocument(
			"2", "2", "사과 상품", "FOOD", "product/apple-thumb.png",
			2000L, "ON_SALE", base.plusDays(2)
		));

		productSearchRepository.save(new ProductDocument(
			"3", "3", "숨긴 상품", "TEST", "product/hidden-thumb.png",
			5000L, "HIDDEN", base.plusDays(3)
		));
	}

	@Test
	void 검색_키워드만_사용하면_이름_포함된_상품만_반환된다() {
		// when
		Page<ProductSearchResponse> result =
			searchService.search(command("테스트", null, null, null, null, null), DEFAULT_PAGE);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).name()).contains("테스트");
	}

	@Test
	void 정렬_조건을_주지_않으면_기본값은_LATEST이다() {
		// when
		Page<ProductSearchResponse> result =
			searchService.search(command(null, null, null, null, null, null), DEFAULT_PAGE);

		// then
		assertThat(result.getContent())
			.extracting(ProductSearchResponse::createdAt)
			.isSortedAccordingTo(Comparator.reverseOrder());
	}

	@Test
	void 정렬_LATEST_지정시_createdAt_내림차순으로_정렬된다() {
		// when
		Page<ProductSearchResponse> result =
			searchService.search(command(null, null, null, null, null, ProductSearchSort.LATEST), DEFAULT_PAGE);

		// then
		assertThat(result.getContent())
			.extracting(ProductSearchResponse::createdAt)
			.isSortedAccordingTo(Comparator.reverseOrder());
	}

	@Test
	void 정렬_LOW_PRICE_이면_가격_오름차순으로_정렬된다() {
		// when
		Page<ProductSearchResponse> result =
			searchService.search(command(null, null, null, null, null, ProductSearchSort.LOW_PRICE), DEFAULT_PAGE);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent())
			.extracting(ProductSearchResponse::price)
			.isSorted();
	}

	@Test
	void 정렬_HIGH_PRICE_이면_가격_내림차순으로_정렬된다() {
		// when
		Page<ProductSearchResponse> result =
			searchService.search(command(null, null, null, null, null, ProductSearchSort.HIGH_PRICE), DEFAULT_PAGE);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent())
			.extracting(ProductSearchResponse::price)
			.isSortedAccordingTo(Comparator.reverseOrder());
	}

	@Test
	void 항상_ON_SALE_상태의_상품만_검색된다() {
		// when
		Page<ProductSearchResponse> result =
			searchService.search(command(null, null, null, null, null, null), DEFAULT_PAGE);

		// then
		assertThat(result.getContent())
			.hasSize(2)
			.extracting(ProductSearchResponse::status)
			.containsOnly("ON_SALE");
	}

	private ProductSearchCommand command(
		String keyword,
		java.util.UUID shopId,
		ProductCategory category,
		Integer minPrice,
		Integer maxPrice,
		ProductSearchSort sort
	) {
		return new ProductSearchCommand(keyword, shopId, category, minPrice, maxPrice, sort);
	}
}
