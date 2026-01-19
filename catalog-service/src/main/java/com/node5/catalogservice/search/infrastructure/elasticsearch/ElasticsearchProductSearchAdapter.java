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

	private static final int AUTOCOMPLETE_LIMIT = 10;

	private final ElasticsearchOperations elasticsearchOperations;

	@Override
	public List<String> autocomplete(ProductAutocompleteCommand command) {

		Query query = Query.of(q -> q.bool(b -> {

			// filter:
			// - 판매 중인 상품만 자동완성 대상
			b.filter(f -> f.term(t -> t.field("status")
				.value(FieldValue.of("ON_SALE"))));

			// must:
			// - 자동완성 필드(name_autocomplete)로 검색어 매칭
			if (StringUtils.hasText(command.keyword())) {
				b.must(m -> m.match(mm -> mm
					.field("name_autocomplete")
					.query(command.keyword())
					.operator(Operator.And)
				));
			}

			return b;
		}));

		NativeQuery nativeQuery = new NativeQueryBuilder()
			.withQuery(query)
			.withMaxResults(AUTOCOMPLETE_LIMIT)
			.build();

		SearchHits<ProductDocument> hits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);

		return hits.getSearchHits().stream()
			.map(hit -> hit.getContent().getName())
			.filter(StringUtils::hasText)
			.toList();
	}

	@Override
	public Page<ProductDocument> search(ProductSearchCommand command, Pageable pageable) {
		Query query = buildBoolQuery(command);

		ProductSearchSort sort = command.sort() != null ? command.sort() : ProductSearchSort.LATEST;

		NativeQuery nativeQuery = new NativeQueryBuilder()
			.withQuery(query)
			.withPageable(pageable)
			.withSort(buildSortOptions(sort))
			.build();

		SearchHits<ProductDocument> hits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);

		SearchPage<ProductDocument> page = SearchHitSupport.searchPageFor(hits, pageable);

		return page.map(hit -> hit.getContent());
	}

	private Query buildBoolQuery(ProductSearchCommand command) {
		return Query.of(q -> q.bool(b -> {

			// filter:
			// - 판매 중인 상품만 검색 대상
			// - 결과를 걸러내는 조건 (점수에는 영향 없음)
			b.filter(f -> f.term(t -> t.field("status")
				.value(FieldValue.of("ON_SALE"))));

			// filter:
			// - 판매자 기준으로 결과 제한
			if (command.shopId() != null) {
				b.filter(f -> f.term(t -> t.field("shopId")
					.value(FieldValue.of(command.shopId().toString()))));
			}

			// filter:
			// - 카테고리 기준으로 결과 제한
			if (command.category() != null) {
				b.filter(f -> f.term(t -> t.field("category")
					.value(FieldValue.of(command.category().name()))));
			}

			// must:
			// - 검색어 매칭
			// - 여기서 점수 계산됨 (결과 순서에 영향)
			if (StringUtils.hasText(command.keyword())) {
				b.must(m -> m.match(mm -> mm
					.field("name")
					.query(command.keyword())
					.operator(Operator.And)
				));
			}

			// filter:
			// - 가격 범위로 결과 제한
			// - 점수에는 영향 없음
			if (command.minPrice() != null && command.maxPrice() != null) {
				double min = command.minPrice().doubleValue();
				double max = command.maxPrice().doubleValue();

				b.filter(f -> f.range(r -> r
					.number(n -> n
						.field("price")
						.gte(min)
						.lte(max)
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
