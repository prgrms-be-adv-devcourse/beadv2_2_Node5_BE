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
import com.node5.catalogservice.cart.exception.CartItemNotFoundException;
import com.node5.catalogservice.cart.exception.CartProductNotFoundException;
import com.node5.catalogservice.cart.exception.CartProductNotOnSaleException;
import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductRepository;
import com.node5.catalogservice.product.domain.ProductStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * 장바구니(Cart) 기능을 제공하는 서비스 계층.
 * <p>
 * - 장바구니 항목 조회/추가/수정/삭제/비우기 기능 제공<br>
 * - 장바구니 담기 전 상품 존재 여부 및 판매 상태(ON_SALE) 검증 수행
 */
@Service
@RequiredArgsConstructor
public class CartService {

	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;

	/**
	 * 회원의 장바구니 항목을 페이징 조회합니다.
	 *
	 * @param memberId 회원 ID
	 * @param pageable 페이징/정렬 정보
	 * @return 장바구니 항목 목록(Page)
	 */
	public Page<CartItemInfo> getCartItems(UUID memberId, Pageable pageable) {
		return cartItemRepository.findByMemberId(memberId,pageable)
			.map(CartItemInfo::from);
	}

	/**
	 * 장바구니에 상품을 담습니다.
	 * <p>
	 * - 상품이 존재하고 판매 중(ON_SALE) 상태인지 검증합니다.<br>
	 * - 이미 담긴 상품이면 수량을 증가시킵니다.
	 *
	 * @param command 장바구니 담기 커맨드
	 * @return 저장된 장바구니 항목 정보
	 * @throws CartProductNotFoundException  상품이 존재하지 않는 경우
	 * @throws CartProductNotOnSaleException 상품이 판매 중 상태가 아닌 경우
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
		return CartItemInfo.from(saved);
	}

	/**
	 * 장바구니 항목의 수량을 변경합니다.
	 *
	 * @param cartItemId 장바구니 항목 ID
	 * @param command    수량 변경 커맨드
	 * @return 변경된 장바구니 항목 정보
	 * @throws CartItemNotFoundException 장바구니 항목이 존재하지 않는 경우
	 */
	public CartItemInfo updateItem(UUID cartItemId, CartItemUpdateCommand command) {
		CartItem cartItem = getCartItemOrThrow(cartItemId);

		cartItem.updateQuantity(command.quantity()); // 도메인에서 유효성 검사

		CartItem saved = cartItemRepository.save(cartItem);
		return CartItemInfo.from(saved);
	}

	/**
	 * 장바구니 항목을 삭제합니다.
	 *
	 * @param cartItemId 장바구니 항목 ID
	 */
	public void removeItem(UUID cartItemId) {
		cartItemRepository.deleteById(cartItemId);
	}

	/**
	 * 회원의 장바구니를 비웁니다.
	 *
	 * @param memberId 회원 ID
	 */
	@Transactional
	public void clearCart(UUID memberId) {
		cartItemRepository.deleteByMemberId(memberId);
	}

	/**
	 * 상품을 조회하고, 존재하지 않으면 예외를 발생시킵니다.
	 */
	private Product getProductOrThrow(UUID productId) {
		return productRepository.findById(productId)
			.orElseThrow(CartProductNotFoundException::new);
	}

	/**
	 * 판매 중(ON_SALE) 상품인지 검증하여 반환합니다.
	 * <p>
	 * - 상품이 없으면 CartProductNotFoundException<br>
	 * - 상품 상태가 ON_SALE이 아니면 CartProductNotOnSaleException
	 */
	private Product getOnSaleProductOrThrow(UUID productId) {
		Product product = getProductOrThrow(productId);

		if (product.getStatus() != ProductStatus.ON_SALE) {
			throw new CartProductNotOnSaleException();
		}
		return product;
	}

	/**
	 * 장바구니 항목을 조회하고, 존재하지 않으면 예외를 발생시킵니다.
	 */
	private CartItem getCartItemOrThrow(UUID cartItemId) {
		return cartItemRepository.findById(cartItemId)
			.orElseThrow(CartItemNotFoundException::new);
	}
}
