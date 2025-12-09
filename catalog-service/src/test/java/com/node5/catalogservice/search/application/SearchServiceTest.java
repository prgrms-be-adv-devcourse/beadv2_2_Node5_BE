package com.node5.catalogservice.search.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

import com.node5.catalogservice.search.application.dto.ProductSearchResponse;
import com.node5.catalogservice.search.domain.ProductDocument;
import com.node5.catalogservice.search.infrastrucutre.ProductSearchRepository;
import com.node5.catalogservice.search.presentation.dto.ProductSearchRequest;

@DataElasticsearchTest
@Import(SearchService.class)
public class SearchServiceTest {

	@MockBean
	private JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@Autowired
	private ProductSearchRepository productSearchRepository;

	@Autowired
	private SearchService searchService;

	@BeforeEach
	public void setup() {
		productSearchRepository.deleteAll();
		productSearchRepository.save(new ProductDocument("1", "테스트 상품", "TEST", 1000L, "ON_SALE"));
		productSearchRepository.save(new ProductDocument("2", "사과 상품", "FOOD", 2000L, "ON_SALE"));
		productSearchRepository.save(new ProductDocument("3", "숨긴 상품 ", "TEST", 5000L, "HIDDEN"));
	}

	@Test
	void 검색_키워드만_사용하면_이름_포함된_상품만_반환된다() {
		/**
		 * given: ES 인덱스에 3개 문서 저장
		 * - ON_SALE + 테스트 상품
		 * - ON_SALE + 사과 상품
		 * - HIDDEN + 숨긴 상품
		 */
		ProductSearchRequest request = new ProductSearchRequest(
			"테스트", null, null, null, null, null
		);

		/**
		 * when: keyword = 테스트 로 검색
		 */
		Page<ProductSearchResponse> result =
			searchService.search(request, PageRequest.of(0, 10));

		/**
		 * then:
		 * - 결과 개수는 1개여야 하고
		 * - 그 상품 이름에 테스트 가 포함되어야 한다
		 */
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).name()).contains("테스트");
	}
}
