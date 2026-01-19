package com.node5.catalogservice.product.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.node5.catalogservice.product.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductDiscontinueService {

	private final ProductRepository productRepository;

	@Transactional
	public int discontinueByShopId(UUID shopId) {
		return productRepository.discontinueByShopId(shopId);
	}
}
