package com.node5.supportservice.search.infrastructure.elasticsearch.query;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.node5.supportservice.search.application.dto.ProductAutocompleteCommand;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

@Component
public class ProductAutocompleteQueryBuilder {

	public Query build(ProductAutocompleteCommand command) {
		return Query.of(q -> q.bool(b -> {

			// filter: 판매 중인 상품만 자동완성 대상
			b.filter(f -> f.term(t -> t.field("status")
				.value(FieldValue.of("ON_SALE"))));

			// must: 자동완성 필드(name_autocomplete) prefix 토큰 매칭
			if (StringUtils.hasText(command.keyword())) {
				b.must(m -> m.match(mm -> mm
					.field("name_autocomplete")
					.query(command.keyword())
					.operator(Operator.And)
				));
			}

			return b;
		}));
	}
}
