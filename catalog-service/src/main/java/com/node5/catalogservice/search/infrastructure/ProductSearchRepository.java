package com.node5.catalogservice.search.infrastructure;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.node5.catalogservice.search.domain.ProductDocument;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
}
