package com.node5.catalogservice.product.application.port;

import com.node5.common.event.ProductDiscontinuedEvent;

public interface ProductDiscontinuedEventPort {
	void publish(ProductDiscontinuedEvent event);
}
