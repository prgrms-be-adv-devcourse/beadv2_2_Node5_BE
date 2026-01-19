package com.node5.supportservice.search.infrastructure;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.node5.supportservice.search.domain.ProductDocument;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
}
