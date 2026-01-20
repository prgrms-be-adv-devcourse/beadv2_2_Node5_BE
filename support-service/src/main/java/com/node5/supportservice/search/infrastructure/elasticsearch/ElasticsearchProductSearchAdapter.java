package com.node5.supportservice.search.infrastructure.elasticsearch;

import java.util.List;

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

import com.node5.supportservice.search.application.dto.ProductAutocompleteCommand;
import com.node5.supportservice.search.application.dto.ProductSearchCommand;
import com.node5.supportservice.search.application.port.ProductSearchPort;
import com.node5.supportservice.search.domain.ProductDocument;
import com.node5.supportservice.search.domain.ProductSearchSort;
import com.node5.supportservice.search.infrastructure.elasticsearch.query.ProductAutocompleteQueryBuilder;
import com.node5.supportservice.search.infrastructure.elasticsearch.query.ProductSearchQueryBuilder;
import com.node5.supportservice.search.infrastructure.elasticsearch.sort.ProductSortOptionsBuilder;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ElasticsearchProductSearchAdapter implements ProductSearchPort {

	private static final int AUTOCOMPLETE_LIMIT = 10;

	private final ElasticsearchOperations elasticsearchOperations;

	private final ProductAutocompleteQueryBuilder autocompleteQueryBuilder;
	private final ProductSearchQueryBuilder searchQueryBuilder;
	private final ProductSortOptionsBuilder sortOptionsBuilder;

	@Override
	public List<String> autocomplete(ProductAutocompleteCommand command) {

		Query query = autocompleteQueryBuilder.build(command);

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

		Query query = searchQueryBuilder.build(command);

		ProductSearchSort sort = command.sort() != null ? command.sort() : ProductSearchSort.LATEST;

		NativeQuery nativeQuery = new NativeQueryBuilder()
			.withQuery(query)
			.withPageable(pageable)
			.withSort(sortOptionsBuilder.build(sort))
			.build();

		SearchHits<ProductDocument> hits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);

		SearchPage<ProductDocument> page = SearchHitSupport.searchPageFor(hits, pageable);

		return page.map(hit -> hit.getContent());
	}
}
