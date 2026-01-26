package com.node5.supportservice.search.infrastructure.elasticsearch.sort;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.node5.supportservice.search.domain.ProductSearchSort;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;

@Component
public class ProductSortOptionsBuilder {

	public List<SortOptions> build(ProductSearchSort sort) {
		Objects.requireNonNull(sort);

		return switch (sort) {
			case LATEST -> List.of(SortOptions.of(s -> s.field(f -> f.field("createdAt").order(SortOrder.Desc))));
			case LOW_PRICE -> List.of(SortOptions.of(s -> s.field(f -> f.field("price").order(SortOrder.Asc))));
			case HIGH_PRICE -> List.of(SortOptions.of(s -> s.field(f -> f.field("price").order(SortOrder.Desc))));
		};
	}
}
