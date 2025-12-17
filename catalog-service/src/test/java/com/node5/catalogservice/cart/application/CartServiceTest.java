package com.node5.catalogservice.cart.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
import com.node5.catalogservice.testsupport.ProductTestFactory;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@Mock
	private CartItemRepository cartItemRepository;

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private CartService cartService;

	private static final Pageable DEFAULT_PAGE = PageRequest.of(0, 10);

	@Test
	void 판매중이_아닌_상품은_장바구니에_담을_수_없다() {
		// given
		UUID memberId = uuid();

		Product hiddenProduct = ProductTestFactory.hidden();
		UUID productId = hiddenProduct.getId();

		CartItemCommand command = command(memberId, productId, 1);
		given(productRepository.findById(productId)).willReturn(Optional.of(hiddenProduct));

		// when & then
		assertThatThrownBy(() -> cartService.addItem(command))
			.isInstanceOf(CartProductNotOnSaleException.class);

		then(cartItemRepository).shouldHaveNoInteractions();
	}

	@Test
	void 상품이_없으면_장바구니에_담을_수_없다() {
		// given
		UUID memberId = uuid();
		UUID productId = uuid();

		CartItemCommand command = command(memberId, productId, 1);
		given(productRepository.findById(productId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> cartService.addItem(command))
			.isInstanceOf(CartProductNotFoundException.class);

		then(cartItemRepository).shouldHaveNoInteractions();
	}

	@Test
	void 동일_상품이_이미_담긴_경우_수량을_증가시킨다() {
		// given
		UUID memberId = uuid();
		int addQty = 3;

		Product onSaleProduct = ProductTestFactory.onSale();
		UUID productId = onSaleProduct.getId();

		CartItemCommand command = command(memberId, productId, addQty);
		given(productRepository.findById(productId)).willReturn(Optional.of(onSaleProduct));

		CartItem existing = mock(CartItem.class);
		given(existing.getProductId()).willReturn(productId);

		given(cartItemRepository.findByMemberIdAndProductId(memberId, productId))
			.willReturn(Optional.of(existing));
		given(cartItemRepository.save(existing)).willReturn(existing);

		// when
		CartItemInfo result = cartService.addItem(command);

		// then
		assertThat(result).isNotNull();
		then(existing).should().increaseQuantity(addQty);
		then(cartItemRepository).should().save(existing);
	}

	@Test
	void 장바구니_항목_수정시_소유자가_아니면_예외가_발생한다() {
		// given
		UUID memberId = uuid();
		UUID otherMemberId = uuid();
		UUID cartItemId = uuid();

		CartItem cartItem = cartItemOwnedBy(otherMemberId);
		given(cartItemRepository.findById(cartItemId)).willReturn(Optional.of(cartItem));

		CartItemUpdateCommand command = updateCommand(5);

		// when & then
		assertThatThrownBy(() -> cartService.updateItem(memberId, cartItemId, command))
			.isInstanceOf(CartItemForbiddenException.class);

		then(cartItemRepository).should(never()).save(any());
	}

	@Test
	void 장바구니_항목_수정시_상품이_유실되면_예외가_발생한다() {
		// given
		UUID memberId = uuid();
		UUID cartItemId = uuid();
		UUID productId = uuid();

		CartItem cartItem = cartItemOwnedBy(memberId);
		given(cartItem.getProductId()).willReturn(productId);

		given(cartItemRepository.findById(cartItemId)).willReturn(Optional.of(cartItem));
		given(cartItemRepository.save(cartItem)).willReturn(cartItem);

		given(productRepository.findById(productId)).willReturn(Optional.empty());

		CartItemUpdateCommand command = updateCommand(2);

		// when & then
		assertThatThrownBy(() -> cartService.updateItem(memberId, cartItemId, command))
			.isInstanceOf(CartProductNotFoundException.class);

		then(cartItem).should().updateQuantity(2);
		then(cartItemRepository).should().save(cartItem);
	}

	@Test
	void 장바구니_항목_삭제시_소유자면_삭제된다() {
		// given
		UUID memberId = uuid();
		UUID cartItemId = uuid();

		CartItem cartItem = cartItemOwnedBy(memberId);
		given(cartItemRepository.findById(cartItemId)).willReturn(Optional.of(cartItem));

		// when
		cartService.removeItem(memberId, cartItemId);

		// then
		then(cartItemRepository).should().deleteById(cartItemId);
	}

	@Test
	void 장바구니_항목_삭제시_소유자가_아니면_예외가_발생한다() {
		// given
		UUID memberId = uuid();
		UUID otherMemberId = uuid();
		UUID cartItemId = uuid();

		CartItem cartItem = cartItemOwnedBy(otherMemberId);
		given(cartItemRepository.findById(cartItemId)).willReturn(Optional.of(cartItem));

		// when & then
		assertThatThrownBy(() -> cartService.removeItem(memberId, cartItemId))
			.isInstanceOf(CartItemForbiddenException.class);

		then(cartItemRepository).should(never()).deleteById(any());
	}

	@Test
	void 장바구니_항목_삭제시_대상이_없으면_예외가_발생한다() {
		// given
		UUID memberId = uuid();
		UUID cartItemId = uuid();

		given(cartItemRepository.findById(cartItemId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> cartService.removeItem(memberId, cartItemId))
			.isInstanceOf(CartItemNotFoundException.class);

		then(cartItemRepository).should(never()).deleteById(any());
	}

	@Test
	void 장바구니_전체_비우기시_memberId로_일괄삭제된다() {
		// given
		UUID memberId = uuid();

		// when
		cartService.clearCart(memberId);

		// then
		then(cartItemRepository).should().deleteByMemberId(memberId);
	}

	@Test
	void 장바구니_조회시_상품정보를_결합하여_반환한다() {
		// given
		UUID memberId = uuid();

		Product p1 = ProductTestFactory.onSale();
		Product p2 = ProductTestFactory.onSale();

		CartItem item1 = cartItemWithProduct(p1.getId());
		CartItem item2 = cartItemWithProduct(p2.getId());

		Page<CartItem> cartItems = new PageImpl<>(List.of(item1, item2), DEFAULT_PAGE, 2);
		given(cartItemRepository.findByMemberId(memberId, DEFAULT_PAGE)).willReturn(cartItems);

		given(productRepository.findAllByIdIn(List.of(p1.getId(), p2.getId())))
			.willReturn(List.of(p1, p2));

		// when
		Page<CartItemInfo> result = cartService.getCartItems(memberId, DEFAULT_PAGE);

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).hasSize(2);
	}

	@Test
	void 장바구니_조회시_상품이_DB에_없으면_예외가_발생한다() {
		// given
		UUID memberId = uuid();
		UUID productId = uuid();

		CartItem item = cartItemWithProduct(productId);

		Page<CartItem> cartItems = new PageImpl<>(List.of(item), DEFAULT_PAGE, 1);
		given(cartItemRepository.findByMemberId(memberId, DEFAULT_PAGE)).willReturn(cartItems);

		given(productRepository.findAllByIdIn(List.of(productId))).willReturn(List.of());

		// when & then
		assertThatThrownBy(() -> cartService.getCartItems(memberId, DEFAULT_PAGE))
			.isInstanceOf(CartProductNotFoundException.class);
	}

	@Test
	void 장바구니에_없는_상품이면_새_항목으로_저장된다() {
		// given
		UUID memberId = uuid();
		int qty = 2;

		Product onSaleProduct = ProductTestFactory.onSale();
		UUID productId = onSaleProduct.getId();

		CartItemCommand command = command(memberId, productId, qty);

		given(productRepository.findById(productId)).willReturn(Optional.of(onSaleProduct));
		given(cartItemRepository.findByMemberIdAndProductId(memberId, productId))
			.willReturn(Optional.empty());

		CartItem saved = mock(CartItem.class);
		given(saved.getId()).willReturn(uuid());
		given(saved.getProductId()).willReturn(productId);
		given(saved.getQuantity()).willReturn(qty);
		given(saved.getCreatedAt()).willReturn(LocalDateTime.now());
		given(saved.getModifiedAt()).willReturn(LocalDateTime.now());

		given(cartItemRepository.save(any(CartItem.class))).willReturn(saved);

		// when
		CartItemInfo result = cartService.addItem(command);

		// then
		assertThat(result).isNotNull();
		then(cartItemRepository).should().save(any(CartItem.class));
	}

	private CartItem cartItemOwnedBy(UUID memberId) {
		CartItem item = mock(CartItem.class);
		given(item.getMemberId()).willReturn(memberId);
		return item;
	}

	private CartItem cartItemWithProduct(UUID productId) {
		CartItem item = mock(CartItem.class);
		given(item.getProductId()).willReturn(productId);
		return item;
	}

	private static UUID uuid() {
		return UUID.randomUUID();
	}

	private CartItemCommand command(UUID memberId, UUID productId, int quantity) {
		return new CartItemCommand(memberId, productId, quantity);
	}

	private CartItemUpdateCommand updateCommand(int quantity) {
		return new CartItemUpdateCommand(quantity);
	}
}
