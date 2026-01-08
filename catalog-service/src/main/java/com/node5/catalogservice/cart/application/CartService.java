package com.node5.catalogservice.cart.application;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.node5.catalogservice.cart.application.dto.CartItemCommand;
import com.node5.catalogservice.cart.application.dto.CartItemInfo;
import com.node5.catalogservice.cart.application.dto.CartItemUpdateCommand;
import com.node5.catalogservice.cart.domain.Cart;
import com.node5.catalogservice.cart.domain.CartItem;
import com.node5.catalogservice.cart.domain.CartItemRepository;
import com.node5.catalogservice.cart.domain.CartRepository;
import com.node5.catalogservice.cart.exception.CartErrorCode;
import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductRepository;
import com.node5.catalogservice.product.domain.ProductStatus;
import com.node5.common.exception.BaseException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;

	public Page<CartItemInfo> getCartItems(UUID memberId, Pageable pageable) {
		UUID cartId = getOrCreateCartId(memberId);

		Page<CartItem> cartItems = cartItemRepository.findByCartId(cartId, pageable);

		List<UUID> productIds = cartItems.getContent().stream()
			.map(CartItem::getProductId)
			.distinct()
			.toList();

		Map<UUID, Product> productMap = productIds.isEmpty()
			? Collections.emptyMap()
			: productRepository.findAllByIdIn(productIds).stream()
			.collect(Collectors.toMap(Product::getId, Function.identity()));

		return cartItems.map(item -> {
			Product product = productMap.get(item.getProductId());
			if (product == null) {
				throw new BaseException(CartErrorCode.CART_PRODUCT_NOT_FOUND);
			}
			return CartItemInfo.from(item, product);
		});
	}

	public CartItemInfo addItem(UUID memberId, CartItemCommand command) {
		UUID productId = command.productId();
		int quantity = command.quantity();

		Product product = getOnSaleProductOrThrow(productId);
		UUID cartId = getOrCreateCartId(memberId);

		CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, product.getId())
			.map(existing -> {
				existing.increaseQuantity(quantity);
				return existing;
			})
			.orElseGet(() -> CartItem.create(cartId, product.getId(), quantity));

		CartItem saved = cartItemRepository.save(cartItem);
		return CartItemInfo.from(saved, product);
	}

	public CartItemInfo updateItem(UUID memberId, UUID cartItemId, CartItemUpdateCommand command) {
		UUID cartId = getOrCreateCartId(memberId);

		CartItem cartItem = cartItemRepository.findByIdAndCartId(cartItemId, cartId)
			.orElseThrow(() -> new BaseException(CartErrorCode.CART_ITEM_NOT_FOUND));

		cartItem.updateQuantity(command.quantity());
		CartItem saved = cartItemRepository.save(cartItem);

		Product product = getProductOrThrow(saved.getProductId());
		return CartItemInfo.from(saved, product);
	}

	public void removeItem(UUID memberId, UUID cartItemId) {
		UUID cartId = getOrCreateCartId(memberId);

		CartItem cartItem = cartItemRepository.findByIdAndCartId(cartItemId, cartId)
			.orElseThrow(() -> new BaseException(CartErrorCode.CART_ITEM_NOT_FOUND));

		cartItemRepository.deleteById(cartItem.getId());
	}

	@Transactional
	public void clearCart(UUID memberId) {
		cartRepository.findByMemberId(memberId)
			.ifPresent(cart -> cartItemRepository.deleteByCartId(cart.getId()));
	}

	private UUID getOrCreateCartId(UUID memberId) {
		return cartRepository.findByMemberId(memberId)
			.orElseGet(() -> cartRepository.save(Cart.create(memberId)))
			.getId();
	}

	private Product getProductOrThrow(UUID productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new BaseException(CartErrorCode.CART_PRODUCT_NOT_FOUND));
	}

	private Product getOnSaleProductOrThrow(UUID productId) {
		Product product = getProductOrThrow(productId);
		if (product.getStatus() != ProductStatus.ON_SALE) {
			throw new BaseException(CartErrorCode.CART_PRODUCT_NOT_ON_SALE);
		}
		return product;
	}
}
