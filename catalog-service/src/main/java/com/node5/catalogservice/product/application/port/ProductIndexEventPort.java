package com.node5.catalogservice.product.application.port;

import com.node5.catalogservice.product.event.ProductIndexEvent;

public interface ProductIndexEventPort {
	void publish(ProductIndexEvent event);
}
