package com.node5.supportservice.search.infrastructure.elasticsearch.query;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.node5.supportservice.search.application.dto.ProductSearchCommand;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

@Component
public class ProductSearchQueryBuilder {

	public Query build(ProductSearchCommand command) {
		return Query.of(q -> q.bool(b -> {

			// filter: 판매 중인 상품만 검색 대상
			b.filter(f -> f.term(t -> t.field("status")
				.value(FieldValue.of("ON_SALE"))));

			// filter: 판매자 기준 제한
			if (command.shopId() != null) {
				b.filter(f -> f.term(t -> t.field("shopId")
					.value(FieldValue.of(command.shopId().toString()))));
			}

			// filter: 카테고리 기준 제한
			if (command.category() != null) {
				b.filter(f -> f.term(t -> t.field("category")
					.value(FieldValue.of(command.category().name()))));
			}

			// must: 검색어 매칭
			if (StringUtils.hasText(command.keyword())) {
				b.must(m -> m.match(mm -> mm
					.field("name")
					.query(command.keyword())
					.operator(Operator.And)
				));
			}

			// filter: 가격 범위 제한
			if (command.minPrice() != null && command.maxPrice() != null) {
				double min = command.minPrice().doubleValue();
				double max = command.maxPrice().doubleValue();

				b.filter(f -> f.range(r -> r.number(n -> n
					.field("price")
					.gte(min)
					.lte(max)
				)));
			}

			return b;
		}));
	}
}
