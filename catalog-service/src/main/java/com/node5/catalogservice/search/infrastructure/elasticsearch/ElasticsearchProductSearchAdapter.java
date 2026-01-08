package com.node5.catalogservice.search.infrastructure.elasticsearch;

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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.node5.catalogservice.search.application.dto.ProductAutocompleteCommand;
import com.node5.catalogservice.search.application.dto.ProductSearchCommand;
import com.node5.catalogservice.search.application.port.ProductSearchPort;
import com.node5.catalogservice.search.domain.ProductDocument;
import com.node5.catalogservice.search.domain.ProductSearchSort;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ElasticsearchProductSearchAdapter implements ProductSearchPort {

	private final ElasticsearchOperations elasticsearchOperations;

	@Override
	public List<String> autocomplete(ProductAutocompleteCommand command) {
		int size = command.size();

		Query query = Query.of(q -> q.bool(b -> {

			// filter:
			// - 판매 중인 상품만 자동완성 대상
			// - 점수 계산과 무관한 고정 조건
			b.filter(f -> f.term(t -> t.field("status").value(FieldValue.of("ON_SALE"))));

			// must:
			// - 자동완성 전용 필드(name_autocomplete)로 prefix 매칭
			if (StringUtils.hasText(command.keyword())) {
				b.must(m -> m.match(mm -> mm
					.field("name_autocomplete")
					.query(command.keyword())
					.operator(Operator.And)
				));
			}

			return b;
		}));

		// 페이징 없이 결과 수 제한
		NativeQuery nativeQuery = new NativeQueryBuilder()
			.withQuery(query)
			.withMaxResults(size)
			.build();

		SearchHits<ProductDocument> hits =
			elasticsearchOperations.search(nativeQuery, ProductDocument.class);

		return hits.getSearchHits().stream()
			.map(hit -> hit.getContent().getName())
			.filter(StringUtils::hasText)
			.toList();
	}

	@Override
	public Page<ProductDocument> search(ProductSearchCommand command, Pageable pageable) {
		Query query = buildBoolQuery(command);

		ProductSearchSort sort =
			command.sort() != null ? command.sort() : ProductSearchSort.LATEST;

		NativeQuery nativeQuery = new NativeQueryBuilder()
			.withQuery(query)
			.withPageable(pageable)
			.withSort(buildSortOptions(sort))
			.build();

		SearchHits<ProductDocument> hits =
			elasticsearchOperations.search(nativeQuery, ProductDocument.class);

		SearchPage<ProductDocument> page =
			SearchHitSupport.searchPageFor(hits, pageable);

		return page.map(hit -> hit.getContent());
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
}
