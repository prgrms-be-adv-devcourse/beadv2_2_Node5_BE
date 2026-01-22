package com.node5.supportservice.search.application;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import com.node5.supportservice.search.domain.ProductDocument;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SponsoredProductService {

	private final ElasticsearchOperations elasticsearchOperations;

	public void sponsor(UUID productId) {
		updateSponsored(productId, true);
	}

	public void unsponsor(UUID productId) {
		updateSponsored(productId, false);
	}

	private void updateSponsored(UUID productId, boolean isSponsored) {

		Document doc = Document.from(Map.of("isSponsored", isSponsored));

		UpdateQuery updateQuery = UpdateQuery.builder(productId.toString())
			.withDocument(doc)
			.build();

		var index = elasticsearchOperations.getIndexCoordinatesFor(ProductDocument.class);
		elasticsearchOperations.update(updateQuery, index);
	}
}
