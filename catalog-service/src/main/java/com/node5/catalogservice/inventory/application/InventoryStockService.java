package com.node5.catalogservice.inventory.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.node5.catalogservice.inventory.domain.Stock;
import com.node5.catalogservice.inventory.domain.StockRepository;
import com.node5.catalogservice.inventory.exception.InventoryErrorCode;
import com.node5.catalogservice.inventory.presentation.dto.StockResponse;
import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductRepository;
import com.node5.catalogservice.shop.client.ShopOwnershipClient;
import com.node5.common.exception.BaseException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryStockService {

	private final StockRepository stockRepository;
	private final ProductRepository productRepository;
	private final ShopOwnershipClient shopOwnershipClient;

	@Transactional
	public StockResponse updateStockQuantity(UUID memberId, UUID productId, int quantity) {
		validateSeller(memberId, productId);

		Stock stock = stockRepository.findById(productId)
			.orElseGet(() -> Stock.create(productId, 0));

		stock.updateQuantity(quantity);

		Stock saved = stockRepository.save(stock);
		return StockResponse.from(saved);
	}

	@Transactional(readOnly = true)
	public StockResponse getStock(UUID productId) {
		Stock stock = stockRepository.findById(productId)
			.orElseThrow(() -> new BaseException(InventoryErrorCode.INVENTORY_NOT_FOUND));

		return StockResponse.from(stock);
	}

	private void validateSeller(UUID memberId, UUID productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new BaseException(InventoryErrorCode.PRODUCT_NOT_FOUND));

		try {
			UUID ownerMemberId = shopOwnershipClient.getOwnerMemberId(product.getShopId());

			if (!ownerMemberId.equals(memberId)) {
				throw new BaseException(InventoryErrorCode.SELLER_FORBIDDEN);
			}
		} catch (FeignException.NotFound e) {
			throw new BaseException(InventoryErrorCode.SHOP_NOT_FOUND);
		} catch (FeignException e) {
			throw new BaseException(InventoryErrorCode.SHOP_SERVICE_UNAVAILABLE);
		}
	}
}
