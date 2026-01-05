package com.node5.catalogservice.product.application.port;

import com.node5.catalogservice.product.domain.Product;

public interface ProductIndexEventPort {
	void publishCreate(Product product);
	void publishUpdate(Product product);
}
