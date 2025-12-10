package com.node5.catalogservice.search.application;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.time.LocalDateTime;
import java.util.Comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;

import com.node5.catalogservice.search.application.dto.ProductSearchResponse;
import com.node5.catalogservice.search.config.ElasticsearchIndexConfig;
import com.node5.catalogservice.search.domain.ProductDocument;
import com.node5.catalogservice.search.domain.ProductSearchSort;
import com.node5.catalogservice.search.infrastructure.ProductSearchRepository;
import com.node5.catalogservice.search.presentation.dto.ProductSearchRequest;

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

	@MockBean
	private JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@Autowired
	private ProductSearchRepository productSearchRepository;

	@Autowired
	private SearchService searchService;

	@Autowired
	private ElasticsearchOperations operations;

	@BeforeEach
	public void setup() {
		// 1) ProductDocument 기반으로 인덱스 초기화
		IndexOperations indexOps = operations.indexOps(ProductDocument.class);

		if (indexOps.exists()) {
			indexOps.delete(); // 혹시 남아 있으면 삭제
		}
		indexOps.create();     // 인덱스 생성
		indexOps.putMapping(); // @Field 기반 매핑 반영
		indexOps.refresh();

		// 2) 문서 초기화
		productSearchRepository.deleteAll();

		LocalDateTime base = LocalDateTime.of(2025, 1, 1, 0, 0);

		productSearchRepository.save(new ProductDocument(
			"1", "테스트 상품", "TEST", 1000L, "ON_SALE", base.plusDays(1))
		);
		productSearchRepository.save(new ProductDocument(
			"2", "사과 상품", "FOOD", 2000L, "ON_SALE", base.plusDays(2))
		);
		productSearchRepository.save(new ProductDocument(
			"3", "숨긴 상품", "TEST", 5000L, "HIDDEN", base.plusDays(3))
		);
	}


	@Test
	void 검색_키워드만_사용하면_이름_포함된_상품만_반환된다() {
		// given
		ProductSearchRequest request = new ProductSearchRequest(
			"테스트", null, null, null, null
		);

		// when
		Page<ProductSearchResponse> result =
			searchService.search(request, PageRequest.of(0, 10));

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).name()).contains("테스트");
	}

	@Test
	void 최신순_정렬이_기본값이다() {
		Page<ProductSearchResponse> result =
			searchService.search(new ProductSearchRequest(
				"상품", null, null, null, null
				),
				PageRequest.of(0, 10));

		assertThat(result.getContent())
			.extracting(ProductSearchResponse::createdAt)
			.isSortedAccordingTo(Comparator.reverseOrder());
	}

	@Test
	void 정렬_LATEST_지정시_createdAt_내림차순으로_정렬된다() {
		// given
		ProductSearchRequest request = new ProductSearchRequest(
			null, null, null, null, ProductSearchSort.LATEST
		);

		// when
		Page<ProductSearchResponse> result =
			searchService.search(request, PageRequest.of(0, 10));

		// then
		assertThat(result.getContent())
			.extracting(ProductSearchResponse::createdAt)
			.isSortedAccordingTo(Comparator.reverseOrder()); // 내림차순
	}

	@Test
	void 정렬_LOW_PRICE_이면_가격_오름차순으로_정렬된다() {
		// given
		ProductSearchRequest request = new ProductSearchRequest(
			null, null, null, null, ProductSearchSort.LOW_PRICE
		);

		// when
		Page<ProductSearchResponse> result =
			searchService.search(request, PageRequest.of(0, 10));

		// then
		assertThat(result.getContent()).hasSize(2); // HIDDEN 한 개 제외

		assertThat(result.getContent())
			.extracting(ProductSearchResponse::price)
			.isSorted(); // 오름차순
	}

	@Test
	void 정렬_HIGH_PRICE_이면_가격_내림차순으로_정렬된다() {
		// given
		ProductSearchRequest request = new ProductSearchRequest(
			null, null, null, null, ProductSearchSort.HIGH_PRICE
		);

		// when
		Page<ProductSearchResponse> result =
			searchService.search(request, PageRequest.of(0, 10));

		// then
		assertThat(result.getContent()).hasSize(2);

		assertThat(result.getContent())
			.extracting(ProductSearchResponse::price)
			.isSortedAccordingTo(Comparator.reverseOrder()); // 내림차순
	}

	@Test
	void 항상_ON_SALE_상태의_상품만_검색된다() {
		// given
		ProductSearchRequest request = new ProductSearchRequest(
			null, null, null, null, null
		);

		// when
		Page<ProductSearchResponse> result =
			searchService.search(request, PageRequest.of(0, 10));

		// then
		assertThat(result.getContent())
			.hasSize(2) // HIDDEN 한 개 제외
			.extracting(ProductSearchResponse::status)
			.containsOnly("ON_SALE");
	}
}
