package com.node5.supportservice.search.application.reindex;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.node5.common.exception.BaseException;
import com.node5.supportservice.config.SearchReindexProperties;
import com.node5.supportservice.search.exception.SearchErrorCode;
import com.node5.supportservice.search.infrastructure.client.CatalogInternalFeignClient;
import com.node5.supportservice.search.infrastructure.client.dto.ProductIdsRequest;
import com.node5.supportservice.search.infrastructure.client.dto.ProductIndexSummaryListResponse;
import com.node5.supportservice.search.infrastructure.client.dto.ProductIndexSummaryResponse;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Conflicts;

@Service
public class ProductReindexService {

	private static final DateTimeFormatter ES_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	private final ElasticsearchOperations elasticsearchOperations;
	private final ElasticsearchClient elasticsearchClient;
	private final CatalogInternalFeignClient feignClient;
	private final ReindexStatusStore statusStore;
	private final SearchReindexProperties reindexProps;
	private final String productIndexName;

	public ProductReindexService(
		ElasticsearchOperations elasticsearchOperations,
		ElasticsearchClient elasticsearchClient,
		CatalogInternalFeignClient feignClient,
		ReindexStatusStore statusStore,
		SearchReindexProperties reindexProps,
		@Qualifier("productIndexName") String productIndexName
	) {
		this.elasticsearchOperations = elasticsearchOperations;
		this.elasticsearchClient = elasticsearchClient;
		this.feignClient = feignClient;
		this.statusStore = statusStore;
		this.reindexProps = reindexProps;
		this.productIndexName = productIndexName;
	}

	public void reindexAll() {
		if (!statusStore.tryStart()) {
			return;
		}

		IndexCoordinates index = IndexCoordinates.of(productIndexName);

		try {
			deleteAllDocsKeepIndex(productIndexName);
			elasticsearchOperations.indexOps(index).refresh();

			int page = 0;
			int pageSize = reindexProps.getPageSize();

			while (true) {
				List<UUID> productIds = feignClient.getOnSaleProductIds(page, pageSize);
				if (productIds == null || productIds.isEmpty()) {
					break;
				}

				ProductIndexSummaryListResponse res =
					feignClient.getProductSummaries(new ProductIdsRequest(productIds));

				List<ProductIndexSummaryResponse> products =
					(res == null || res.products() == null) ? List.of() : res.products();

				if (products.isEmpty()) {
					page++;
					continue;
				}

				List<IndexQuery> queries = products.stream()
					.map(this::toIndexQuery)
					.toList();

				elasticsearchOperations.bulkIndex(queries, index);

				statusStore.addProcessed(products.size());
				page++;
			}

			elasticsearchOperations.indexOps(index).refresh();

			statusStore.markSuccess();

		} catch (Exception e) {
			statusStore.markFailed(e);
			throw new BaseException(SearchErrorCode.REINDEX_FAILED);
		}
	}

	private void deleteAllDocsKeepIndex(String indexName) throws Exception {
		elasticsearchClient.deleteByQuery(d -> d
			.index(indexName)
			.query(q -> q.matchAll(m -> m))
			.conflicts(Conflicts.Proceed)
		);
	}

	private IndexQuery toIndexQuery(ProductIndexSummaryResponse p) {
		Map<String, Object> doc = new HashMap<>();
		doc.put("productId", p.productId().toString());
		doc.put("shopId", p.shopId().toString());
		doc.put("name", p.name());
		doc.put("name_autocomplete",
			StringUtils.hasText(p.nameAutocomplete()) ? p.nameAutocomplete() : p.name());
		doc.put("category", p.category());
		doc.put("thumbnailKey", p.thumbnailKey());
		doc.put("price", p.price());
		doc.put("status", p.status());
		doc.put("createdAt", p.createdAt().format(ES_DATE_TIME));
		doc.put("modifiedAt", p.modifiedAt().format(ES_DATE_TIME));
		doc.put("isSponsored", false); // 정책으로 재색인 시 false

		return new IndexQueryBuilder()
			.withId(p.productId().toString())
			.withObject(doc)
			.build();
	}
}
