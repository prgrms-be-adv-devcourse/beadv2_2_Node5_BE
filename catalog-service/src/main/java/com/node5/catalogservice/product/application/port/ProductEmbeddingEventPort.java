package com.node5.catalogservice.product.application.port;

import com.node5.common.event.ProductEmbeddingEvent;

public interface ProductEmbeddingEventPort {
	void publish(ProductEmbeddingEvent event);
}
