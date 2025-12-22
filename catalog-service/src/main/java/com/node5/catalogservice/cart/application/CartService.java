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
import com.node5.catalogservice.cart.domain.CartItem;
import com.node5.catalogservice.cart.domain.CartItemRepository;
import com.node5.catalogservice.cart.exception.CartItemForbiddenException;
import com.node5.catalogservice.cart.exception.CartItemNotFoundException;
import com.node5.catalogservice.cart.exception.CartProductNotFoundException;
import com.node5.catalogservice.cart.exception.CartProductNotOnSaleException;
import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductRepository;
import com.node5.catalogservice.product.domain.ProductStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * 장바구니(Cart) 도메인의 비즈니스 로직을 담당합니다.
 * <p>
 * - 장바구니 조회 / 추가 / 수량 변경 / 삭제 / 비우기<br>
 * - 장바구니 담기 전 상품 존재 및 판매 상태(ON_SALE) 검증<br>
 * - 장바구니 항목 수정/삭제 시 소유권(memberId) 검증<br>
 * - 조회 시 CartItem에 포함된 productId 목록을 일괄 조회한 뒤 응답에 결합
 */
@Service
@RequiredArgsConstructor
public class CartService {

	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;

	public Page<CartItemInfo> getCartItems(UUID memberId, Pageable pageable) {
		Page<CartItem> cartItems = cartItemRepository.findByMemberId(memberId, pageable);

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
				throw new CartProductNotFoundException();
			}
			return CartItemInfo.from(item, product);
		});
	}

	/**
	 * 장바구니에 상품을 추가합니다.
	 * <p>
	 * - 상품 존재 여부 및 판매 상태(ON_SALE) 검증<br>
	 * - 동일 상품이 이미 담긴 경우 수량을 증가 처리
	 */
	public CartItemInfo addItem(CartItemCommand command) {
		UUID memberId = command.memberId();
		UUID productId = command.productId();
		int quantity = command.quantity();

		Product product = getOnSaleProductOrThrow(productId);

		CartItem cartItem = cartItemRepository.findByMemberIdAndProductId(memberId, product.getId())
			.map(existing -> {
				existing.increaseQuantity(quantity);
				return existing;
			})
			.orElseGet(() -> CartItem.create(memberId, product.getId(), quantity));

		CartItem saved = cartItemRepository.save(cartItem);
		return CartItemInfo.from(saved, product);
	}

	public CartItemInfo updateItem(UUID memberId, UUID cartItemId, CartItemUpdateCommand command) {
		CartItem cartItem = getCartItemOrThrow(cartItemId);
		validateOwnership(memberId, cartItem);

		cartItem.updateQuantity(command.quantity());
		CartItem saved = cartItemRepository.save(cartItem);

		Product product = getProductOrThrow(saved.getProductId());
		return CartItemInfo.from(saved, product);
	}

	public void removeItem(UUID memberId, UUID cartItemId) {
		CartItem cartItem = getCartItemOrThrow(cartItemId);
		validateOwnership(memberId, cartItem);

		cartItemRepository.deleteById(cartItemId);
	}

	@Transactional
	public void clearCart(UUID memberId) {
		cartItemRepository.deleteByMemberId(memberId);
	}

	private void validateOwnership(UUID memberId, CartItem cartItem) {
		if (!cartItem.getMemberId().equals(memberId)) {
			throw new CartItemForbiddenException();
		}
	}

	private Product getProductOrThrow(UUID productId) {
		return productRepository.findById(productId)
			.orElseThrow(CartProductNotFoundException::new);
	}

	private Product getOnSaleProductOrThrow(UUID productId) {
		Product product = getProductOrThrow(productId);
		if (product.getStatus() != ProductStatus.ON_SALE) {
			throw new CartProductNotOnSaleException();
		}
		return product;
	}

	private CartItem getCartItemOrThrow(UUID cartItemId) {
		return cartItemRepository.findById(cartItemId)
			.orElseThrow(CartItemNotFoundException::new);
	}
}
