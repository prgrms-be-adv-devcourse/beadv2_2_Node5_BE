package com.node5.supportservice.search.application.port;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.node5.supportservice.search.application.dto.ProductAutocompleteCommand;
import com.node5.supportservice.search.application.dto.ProductSearchCommand;
import com.node5.supportservice.search.domain.ProductDocument;

public interface ProductSearchPort {

	Page<ProductDocument> search(ProductSearchCommand command, Pageable pageable);

	List<String> autocomplete(ProductAutocompleteCommand command);

	List<ProductDocument> searchSponsored(ProductSearchCommand command, int limit);
}
