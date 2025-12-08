package com.node5.catalogservice.cart.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.node5.catalogservice.cart.application.dto.CartItemCommand;
import com.node5.catalogservice.cart.application.dto.CartItemInfo;
import com.node5.catalogservice.cart.application.dto.CartItemUpdateCommand;
import com.node5.catalogservice.cart.domain.CartItem;
import com.node5.catalogservice.cart.domain.CartItemRepository;
import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductRepository;
import com.node5.catalogservice.product.domain.ProductStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;

	public Page<CartItemInfo> getCartItems(UUID memberId, Pageable pageable) {
		return cartItemRepository.findByMemberId(memberId,pageable)
			.map(CartItemInfo::from);
	}

	public CartItemInfo addItem(CartItemCommand command) {
		UUID memberId = command.memberId();
		UUID productId = command.productId();
		int quantity = command.quantity();

		// 상품 상태 검증
		validateProduct(productId);

		CartItem cartItem = cartItemRepository.findByMemberIdAndProductId(memberId, productId)
			.map(existing -> {
				existing.increaseQuantity(quantity); // 수량 증가
				return existing;
			})
			.orElseGet(() -> CartItem.create(memberId, productId, quantity)); // 새로 담기

		CartItem saved = cartItemRepository.save(cartItem);
		return CartItemInfo.from(saved);
	}

	public CartItemInfo decreaseItem(UUID cartItemId, CartItemUpdateCommand command) {
		CartItem cartItem = getCartItemOrThrow(cartItemId);

		int amount = command.quantity(); // 줄이고 싶은 개수

		cartItem.decreaseQuantity(amount); // 도메인에서 유효성 검사

		CartItem saved = cartItemRepository.save(cartItem);
		return CartItemInfo.from(saved);
	}

	public CartItemInfo updateItem(UUID cartItemId, CartItemUpdateCommand command) {
		CartItem cartItem = getCartItemOrThrow(cartItemId);

		cartItem.updateQuantity(command.quantity()); // 도메인에서 유효성 검사

		CartItem saved = cartItemRepository.save(cartItem);
		return CartItemInfo.from(saved);
	}

	public void removeItem(UUID cartItemId) {
		cartItemRepository.deleteById(cartItemId);
	}

	@Transactional
	public void clearCart(UUID memberId) {
		cartItemRepository.deleteByMemberId(memberId);
	}

	private void validateProduct(UUID productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다. id=" + productId));

		if (product.getStatus() != ProductStatus.ON_SALE) {
			throw new IllegalArgumentException("해당 상품은 장바구니에 담을 수 없는 상태입니다. status=" + product.getStatus());
		}
	}

	private CartItem getCartItemOrThrow(UUID cartItemId) {
		return cartItemRepository.findById(cartItemId)
			.orElseThrow(() -> new IllegalArgumentException("장바구니 항목이 존재하지 않습니다. id=" + cartItemId));
	}
}
