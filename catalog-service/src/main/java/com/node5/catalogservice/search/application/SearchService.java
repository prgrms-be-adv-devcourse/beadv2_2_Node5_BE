package com.node5.catalogservice.search.application;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.node5.catalogservice.search.application.dto.ProductSearchCommand;
import com.node5.catalogservice.search.application.dto.ProductSearchResponse;
import com.node5.catalogservice.search.domain.ProductDocument;
import com.node5.catalogservice.search.domain.ProductSearchSort;
import com.node5.catalogservice.search.exception.SearchErrorCode;
import com.node5.catalogservice.search.exception.SearchException;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;

/**
 * Elasticsearch 기반 상품 검색 유스케이스를 담당합니다.
 * <p>
 * - 항상 판매 중(ON_SALE) 상품만 검색 대상<br>
 * - 키워드/카테고리/상점(shopId)/가격 범위 조건을 조합하여 검색<br>
 * - 가격 범위는 (min, max) 쌍으로만 허용하며, 유효하지 않으면 예외를 반환<br>
 * - 정렬(LATEST/LOW_PRICE/HIGH_PRICE)을 지원하며, 기본 정렬은 LATEST 입니다.
 */
@Service
@RequiredArgsConstructor
public class SearchService {

	private final ElasticsearchOperations elasticsearchOperations;

	/**
	 * 상품을 검색합니다.
	 * <p>
	 * - 요청 조건을 조합하여 BoolQuery를 생성합니다.<br>
	 * - 조건이 없더라도 status=ON_SALE 필터는 항상 적용됩니다.<br>
	 * - 가격 범위(min/max)는 둘 중 하나만 전달되면 예외를 반환합니다.<br>
	 * - 정렬 조건이 없으면 기본 정렬(LATEST)을 적용합니다.
	 */
	public Page<ProductSearchResponse> search(ProductSearchCommand command, Pageable pageable) {
		validatePriceRange(command.minPrice(), command.maxPrice());

		Query query = buildBoolQuery(command);

		ProductSearchSort sort =
			command.sort() != null ? command.sort() : ProductSearchSort.LATEST;

		NativeQueryBuilder queryBuilder = new NativeQueryBuilder()
			.withQuery(query)
			.withPageable(pageable)
			.withSort(buildSortOptions(sort));

		NativeQuery nativeQuery = queryBuilder.build();

		SearchHits<ProductDocument> hits =
			elasticsearchOperations.search(nativeQuery, ProductDocument.class);

		SearchPage<ProductDocument> page =
			SearchHitSupport.searchPageFor(hits, pageable);

		return page.map(hit -> ProductSearchResponse.from(hit.getContent()));
	}

	private Query buildBoolQuery(ProductSearchCommand command) {
		return Query.of(q -> q.bool(b -> {

			// filter:
			// - 점수(scoring)에 영향을 주지 않는 조건
			// - 캐시 가능 → 성능 유리
			// - 항상 적용되는 필터 조건에 적합
			b.filter(f -> f.term(t -> t.field("status").value(FieldValue.of("ON_SALE"))));

			// filter:
			// - exact match 조건
			// - 검색 결과 점수에 영향 없음
			if (command.shopId() != null) {
				b.filter(f -> f.term(t -> t.field("shopId").value(FieldValue.of(command.shopId().toString()))));
			}

			// filter:
			// - enum 값 정확 일치 조건
			if (command.category() != null) {
				b.filter(f -> f.term(t -> t.field("category").value(FieldValue.of(command.category().name()))));
			}

			// must:
			// - 텍스트 검색 조건
			// - relevance score 계산 대상
			// - 키워드 검색은 점수 기반 정렬에 의미가 있으므로 must 사용
			if (StringUtils.hasText(command.keyword())) {
				b.must(m -> m.match(mm -> mm
					.field("name")
					.query(command.keyword())
					.operator(Operator.And)
				));
			}

			// filter:
			// - 범위 조건은 점수와 무관
			// - 숫자 필터는 filter로 처리하여 성능 최적화
			if (command.minPrice() != null && command.maxPrice() != null) {
				long min = command.minPrice().longValue();
				long max = command.maxPrice().longValue();

				b.filter(f -> f.range(r -> r
					.number(n -> n
						.field("price")
						.gte((double) min)
						.lte((double) max)
					)
				));
			}

			return b;
		}));
	}

	private List<SortOptions> buildSortOptions(ProductSearchSort sort) {
		Objects.requireNonNull(sort);

		return switch (sort) {
			case LATEST -> List.of(SortOptions.of(s -> s.field(f -> f.field("createdAt").order(SortOrder.Desc))));
			case LOW_PRICE -> List.of(SortOptions.of(s -> s.field(f -> f.field("price").order(SortOrder.Asc))));
			case HIGH_PRICE -> List.of(SortOptions.of(s -> s.field(f -> f.field("price").order(SortOrder.Desc))));
		};
	}

	/**
	 * 가격 범위(min/max) 요청 값의 유효성을 검증합니다.
	 * <p>
	 * - min/max 중 하나만 전달되면 PRICE_RANGE_INCOMPLETE 예외<br>
	 * - min > max 인 경우 INVALID_PRICE_RANGE 예외
	 */
	private void validatePriceRange(Integer minPrice, Integer maxPrice) {
		if ((minPrice == null) != (maxPrice == null)) {
			throw new SearchException(SearchErrorCode.PRICE_RANGE_INCOMPLETE);
		}
		if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
			throw new SearchException(SearchErrorCode.INVALID_PRICE_RANGE);
		}
	}
}
