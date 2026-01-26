package com.node5.catalogservice.product.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.node5.catalogservice.client.ShopOwnershipPort;
import com.node5.catalogservice.product.application.dto.ProductCommand;
import com.node5.catalogservice.product.application.dto.ProductInfo;
import com.node5.catalogservice.product.application.dto.ProductUpdateCommand;
import com.node5.catalogservice.product.application.port.ProductDiscontinuedEventPort;
import com.node5.catalogservice.product.application.port.ProductEmbeddingEventPort;
import com.node5.catalogservice.product.application.port.ProductIndexEventPort;
import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.product.domain.ProductIdempotency;
import com.node5.catalogservice.product.domain.ProductIdempotencyRepository;
import com.node5.catalogservice.product.domain.ProductRepository;
import com.node5.catalogservice.product.domain.ProductStatus;
import com.node5.catalogservice.product.exception.ProductErrorCode;
import com.node5.catalogservice.testsupport.ProductTestFactory;
import com.node5.common.event.ProductIndexEventType;
import com.node5.common.exception.BaseException;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductIdempotencyRepository productIdempotencyRepository;

	@Mock
	private ProductIndexEventPort productIndexEventPort;

	@Mock
	private ProductEmbeddingEventPort productEmbeddingEventPort;

	@Mock
	private ProductDiscontinuedEventPort productDiscontinuedEventPort;

	@Mock
	private ShopOwnershipPort shopOwnershipPort;

	@InjectMocks
	private ProductService productService;

	@Test
	void 상품_생성_키없음_성공시_소유권검증후_저장하고_색인과_임베딩_이벤트를_발행한다() {
		// given
		UUID memberId = uuid();
		UUID shopId = uuid();

		ProductCommand command = new ProductCommand(
			"name",
			"desc",
			BigDecimal.valueOf(1000),
			ProductStatus.ON_SALE,
			anyCategory(),
			"thumb.png"
		);

		given(shopOwnershipPort.getOwnerMemberId(shopId)).willReturn(memberId);

		Product saved = ProductTestFactory.onSale();
		given(productRepository.save(any(Product.class))).willReturn(saved);

		// when (idempotencyKey 없음)
		ProductInfo result = productService.createProduct(memberId, shopId, command, null);

		// then
		assertThat(result).isNotNull();
		then(shopOwnershipPort).should().getOwnerMemberId(shopId);
		then(productRepository).should().save(any(Product.class));

		then(productIdempotencyRepository).shouldHaveNoInteractions();

		then(productIndexEventPort).should().publish(argThat(e ->
			e != null
				&& e.productId().equals(saved.getId())
				&& e.type() == ProductIndexEventType.CREATE
		));

		then(productEmbeddingEventPort).should().publish(argThat(e ->
			e != null && e.productId().equals(saved.getId())
		));

		then(productDiscontinuedEventPort).shouldHaveNoInteractions();
	}

	@Test
	void 상품_생성_키있음_최초요청이면_PROCESSING선점후_상품을생성하고_idempotency를_COMPLETED로_업데이트한다() {
		// given
		UUID memberId = uuid();
		UUID shopId = uuid();
		String idempotencyKey = "idem-001";

		ProductCommand command = new ProductCommand(
			"name",
			"desc",
			BigDecimal.valueOf(1000),
			ProductStatus.ON_SALE,
			anyCategory(),
			"thumb.png"
		);

		given(shopOwnershipPort.getOwnerMemberId(shopId)).willReturn(memberId);

		given(productIdempotencyRepository.tryStartProcessing(idempotencyKey)).willReturn(true);

		ProductIdempotency processing = ProductIdempotency.processing(idempotencyKey);
		given(productIdempotencyRepository.findByKey(idempotencyKey)).willReturn(Optional.of(processing));

		Product saved = ProductTestFactory.onSale();
		given(productRepository.save(any(Product.class))).willReturn(saved);

		// when
		ProductInfo result = productService.createProduct(memberId, shopId, command, idempotencyKey);

		// then
		assertThat(result).isNotNull();

		then(productIdempotencyRepository).should().tryStartProcessing(idempotencyKey);
		then(productRepository).should().save(any(Product.class));

		then(productIdempotencyRepository).should().save(argThat(idem ->
			idem != null
				&& idem.getIdempotencyKey().equals(idempotencyKey)
				&& idem.getStatus() == ProductIdempotency.Status.COMPLETED
				&& saved.getId().equals(idem.getProductId())
		));

		then(productIndexEventPort).should().publish(argThat(e ->
			e != null
				&& e.productId().equals(saved.getId())
				&& e.type() == ProductIndexEventType.CREATE
		));

		then(productEmbeddingEventPort).should().publish(argThat(e ->
			e != null && e.productId().equals(saved.getId())
		));

		then(productDiscontinuedEventPort).shouldHaveNoInteractions();
	}

	@Test
	void 상품_생성_키있음_이미_COMPLETED면_기존상품을_반환하고_이벤트는_발행하지않는다() {
		// given
		UUID memberId = uuid();
		UUID shopId = uuid();
		String idempotencyKey = "idem-002";

		ProductCommand command = new ProductCommand(
			"name",
			"desc",
			BigDecimal.valueOf(1000),
			ProductStatus.ON_SALE,
			anyCategory(),
			"thumb.png"
		);

		given(shopOwnershipPort.getOwnerMemberId(shopId)).willReturn(memberId);

		given(productIdempotencyRepository.tryStartProcessing(idempotencyKey)).willReturn(false);

		Product existingProduct = ProductTestFactory.onSale();
		ProductIdempotency completed = ProductIdempotency.processing(idempotencyKey);
		completed.complete(existingProduct.getId());

		given(productIdempotencyRepository.findByKey(idempotencyKey)).willReturn(Optional.of(completed));
		given(productRepository.findById(existingProduct.getId())).willReturn(Optional.of(existingProduct));

		// when
		ProductInfo result = productService.createProduct(memberId, shopId, command, idempotencyKey);

		// then
		assertThat(result).isNotNull();

		then(productRepository).should(never()).save(any(Product.class));
		then(productIndexEventPort).shouldHaveNoInteractions();
		then(productEmbeddingEventPort).shouldHaveNoInteractions();
		then(productDiscontinuedEventPort).shouldHaveNoInteractions();
	}

	@Test
	void 상품_생성_키있음_PROCESSING이면_409_IDEMPOTENCY_REQUEST_IN_PROGRESS() {
		// given
		UUID memberId = uuid();
		UUID shopId = uuid();
		String idempotencyKey = "idem-003";

		ProductCommand command = new ProductCommand(
			"name",
			"desc",
			BigDecimal.valueOf(1000),
			ProductStatus.ON_SALE,
			anyCategory(),
			"thumb.png"
		);

		given(shopOwnershipPort.getOwnerMemberId(shopId)).willReturn(memberId);

		given(productIdempotencyRepository.tryStartProcessing(idempotencyKey)).willReturn(false);

		ProductIdempotency processing = ProductIdempotency.processing(idempotencyKey);
		given(productIdempotencyRepository.findByKey(idempotencyKey)).willReturn(Optional.of(processing));

		// when & then
		assertThatThrownBy(() -> productService.createProduct(memberId, shopId, command, idempotencyKey))
			.isInstanceOf(BaseException.class)
			.satisfies(ex -> {
				BaseException be = (BaseException) ex;
				assertThat(be.getErrorCode()).isEqualTo(ProductErrorCode.IDEMPOTENCY_REQUEST_IN_PROGRESS);
			});

		then(productRepository).should(never()).save(any(Product.class));
		then(productIndexEventPort).shouldHaveNoInteractions();
		then(productEmbeddingEventPort).shouldHaveNoInteractions();
		then(productDiscontinuedEventPort).shouldHaveNoInteractions();
	}

	@Test
	void 상품_수정_성공시_저장하고_색인과_임베딩_이벤트를_발행한다() {
		// given
		UUID memberId = uuid();

		Product existing = ProductTestFactory.onSale();
		UUID productId = existing.getId();
		UUID shopId = existing.getShopId();

		ProductUpdateCommand patch = new ProductUpdateCommand(
			"newName",
			"newDesc",
			BigDecimal.valueOf(2000),
			anyCategory(),
			"newThumb.png"
		);

		given(productRepository.findById(productId)).willReturn(Optional.of(existing));
		given(shopOwnershipPort.getOwnerMemberId(shopId)).willReturn(memberId);
		given(productRepository.save(existing)).willReturn(existing);

		// when
		ProductInfo result = productService.updateProduct(memberId, productId, patch);

		// then
		assertThat(result).isNotNull();
		then(shopOwnershipPort).should().getOwnerMemberId(shopId);
		then(productRepository).should().save(existing);

		then(productIndexEventPort).should().publish(argThat(e ->
			e != null
				&& e.productId().equals(existing.getId())
				&& e.type() == ProductIndexEventType.UPDATE
		));

		then(productEmbeddingEventPort).should().publish(argThat(e ->
			e != null && e.productId().equals(existing.getId())
		));

		then(productDiscontinuedEventPort).shouldHaveNoInteractions();
	}

	@Test
	void 상품_수정시_상품이_없으면_PRODUCT_NOT_FOUND_BaseException() {
		// given
		UUID memberId = uuid();
		UUID productId = uuid();

		given(productRepository.findById(productId)).willReturn(Optional.empty());

		ProductUpdateCommand patch = new ProductUpdateCommand(
			"newName",
			null,
			null,
			null,
			null
		);

		// when & then
		assertThatThrownBy(() -> productService.updateProduct(memberId, productId, patch))
			.isInstanceOf(BaseException.class)
			.satisfies(ex -> {
				BaseException be = (BaseException) ex;
				assertThat(be.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND);
			});

		then(productRepository).should(never()).save(any());
		then(productIndexEventPort).shouldHaveNoInteractions();
		then(productEmbeddingEventPort).shouldHaveNoInteractions();
		then(productDiscontinuedEventPort).shouldHaveNoInteractions();
		then(shopOwnershipPort).shouldHaveNoInteractions();
	}

	@Test
	void 판매중_상품_단건조회는_findByIdAndStatus로_조회한다() {
		// given
		Product onSale = ProductTestFactory.onSale();
		UUID productId = onSale.getId();

		given(productRepository.findByIdAndStatus(productId, ProductStatus.ON_SALE))
			.willReturn(Optional.of(onSale));

		// when
		ProductInfo result = productService.getOnSaleProduct(productId);

		// then
		assertThat(result).isNotNull();
		then(productRepository).should().findByIdAndStatus(productId, ProductStatus.ON_SALE);

		then(productIndexEventPort).shouldHaveNoInteractions();
		then(productEmbeddingEventPort).shouldHaveNoInteractions();
		then(productDiscontinuedEventPort).shouldHaveNoInteractions();
		then(shopOwnershipPort).shouldHaveNoInteractions();
	}

	private static UUID uuid() {
		return UUID.randomUUID();
	}

	private static ProductCategory anyCategory() {
		ProductCategory[] values = ProductCategory.values();
		if (values.length == 0) {
			throw new IllegalStateException("ProductCategory enum에 정의된 값이 없습니다.");
		}
		return values[0];
	}
}
