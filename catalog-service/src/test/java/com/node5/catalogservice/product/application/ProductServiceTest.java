package com.node5.catalogservice.product.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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

import com.node5.catalogservice.kafka.producer.ProductIndexProducer;
import com.node5.catalogservice.product.application.dto.ProductCommand;
import com.node5.catalogservice.product.application.dto.ProductInfo;
import com.node5.catalogservice.product.application.dto.ProductUpdateCommand;
import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.product.domain.ProductRepository;
import com.node5.catalogservice.product.domain.ProductStatus;
import com.node5.catalogservice.product.exception.OnSaleProductNotFoundException;
import com.node5.catalogservice.product.exception.ProductErrorCode;
import com.node5.catalogservice.product.exception.ProductNotFoundException;
import com.node5.catalogservice.shop.client.ShopOwnershipClient;
import com.node5.catalogservice.testsupport.ProductTestFactory;
import com.node5.common.exception.BaseException;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductIndexProducer productIndexProducer;

	@Mock
	private ShopOwnershipClient shopOwnershipClient;

	@InjectMocks
	private ProductService productService;

	private static final Pageable DEFAULT_PAGE = PageRequest.of(0, 10);

	@Test
	void 상품_생성_성공시_상점소유권_검증후_저장하고_색인이벤트를_발행한다() {
		// given
		UUID memberId = uuid();
		UUID shopId = uuid();

		ProductCommand command = new ProductCommand(
			"name",
			"desc",
			BigDecimal.valueOf(1000),
			10,
			ProductStatus.ON_SALE,
			anyCategory(),
			"thumb.png"
		);

		given(shopOwnershipClient.getOwnerMemberId(shopId)).willReturn(memberId);

		Product saved = ProductTestFactory.onSale();
		given(productRepository.save(any(Product.class))).willReturn(saved);

		// when
		ProductInfo result = productService.createProduct(memberId, shopId, command);

		// then
		assertThat(result).isNotNull();
		then(shopOwnershipClient).should().getOwnerMemberId(shopId);
		then(productRepository).should().save(any(Product.class));
		then(productIndexProducer).should().sendProductIndexEvent(saved);
	}

	@Test
	void 상품_생성시_존재하지_않는_상점이면_SHOP_NOT_FOUND_BaseException() {
		// given
		UUID memberId = uuid();
		UUID shopId = uuid();

		ProductCommand command = new ProductCommand(
			"name",
			"desc",
			BigDecimal.valueOf(1000),
			10,
			ProductStatus.ON_SALE,
			anyCategory(),
			"thumb.png"
		);

		given(shopOwnershipClient.getOwnerMemberId(shopId)).willThrow(feignNotFound());

		// when & then
		assertThatThrownBy(() -> productService.createProduct(memberId, shopId, command))
			.isInstanceOf(BaseException.class)
			.satisfies(ex -> {
				BaseException be = (BaseException) ex;
				assertThat(be.getErrorCode()).isEqualTo(ProductErrorCode.SHOP_NOT_FOUND);
			});

		then(productRepository).shouldHaveNoInteractions();
		then(productIndexProducer).shouldHaveNoInteractions();
	}

	@Test
	void 상품_생성시_상점_소유자가_아니면_SHOP_FORBIDDEN_BaseException() {
		// given
		UUID memberId = uuid();
		UUID shopId = uuid();

		ProductCommand command = new ProductCommand(
			"name",
			"desc",
			BigDecimal.valueOf(1000),
			10,
			ProductStatus.ON_SALE,
			anyCategory(),
			"thumb.png"
		);

		UUID otherOwner = uuid();
		given(shopOwnershipClient.getOwnerMemberId(shopId)).willReturn(otherOwner);

		// when & then
		assertThatThrownBy(() -> productService.createProduct(memberId, shopId, command))
			.isInstanceOf(BaseException.class)
			.satisfies(ex -> {
				BaseException be = (BaseException) ex;
				assertThat(be.getErrorCode()).isEqualTo(ProductErrorCode.SHOP_FORBIDDEN);
			});

		then(productRepository).shouldHaveNoInteractions();
		then(productIndexProducer).shouldHaveNoInteractions();
	}

	@Test
	void 상품_생성시_상점서비스_장애면_SHOP_SERVICE_UNAVAILABLE_BaseException() {
		// given
		UUID memberId = uuid();
		UUID shopId = uuid();

		ProductCommand command = new ProductCommand(
			"name",
			"desc",
			BigDecimal.valueOf(1000),
			10,
			ProductStatus.ON_SALE,
			anyCategory(),
			"thumb.png"
		);

		given(shopOwnershipClient.getOwnerMemberId(shopId)).willThrow(feignInternalServerError());

		// when & then
		assertThatThrownBy(() -> productService.createProduct(memberId, shopId, command))
			.isInstanceOf(BaseException.class)
			.satisfies(ex -> {
				BaseException be = (BaseException) ex;
				assertThat(be.getErrorCode()).isEqualTo(ProductErrorCode.SHOP_SERVICE_UNAVAILABLE);
			});

		then(productRepository).shouldHaveNoInteractions();
		then(productIndexProducer).shouldHaveNoInteractions();
	}

	@Test
	void 상품_수정_성공시_소유권검증후_저장하고_업데이트이벤트를_발행한다() {
		// given
		UUID memberId = uuid();

		Product existing = ProductTestFactory.onSale();
		UUID productId = existing.getId();
		UUID shopId = existing.getShopId();

		ProductUpdateCommand patch = new ProductUpdateCommand(
			"newName",
			"newDesc",
			BigDecimal.valueOf(2000),
			20,
			anyCategory(),
			"newThumb.png"
		);

		given(productRepository.findById(productId)).willReturn(Optional.of(existing));
		given(shopOwnershipClient.getOwnerMemberId(shopId)).willReturn(memberId);
		given(productRepository.save(existing)).willReturn(existing);

		// when
		ProductInfo result = productService.updateProduct(memberId, productId, patch);

		// then
		assertThat(result).isNotNull();
		then(shopOwnershipClient).should().getOwnerMemberId(shopId);
		then(productRepository).should().save(existing);
		then(productIndexProducer).should().sendProductUpdateEvent(existing);
	}

	@Test
	void 상품_수정시_상품이_없으면_ProductNotFoundException() {
		// given
		UUID memberId = uuid();
		UUID productId = uuid();

		given(productRepository.findById(productId)).willReturn(Optional.empty());

		ProductUpdateCommand patch = new ProductUpdateCommand(
			"newName",
			null,
			null,
			null,
			null,
			null
		);

		// when & then
		assertThatThrownBy(() -> productService.updateProduct(memberId, productId, patch))
			.isInstanceOf(ProductNotFoundException.class);

		then(shopOwnershipClient).shouldHaveNoInteractions();
		then(productRepository).should(never()).save(any());
		then(productIndexProducer).shouldHaveNoInteractions();
	}

	@Test
	void 상품_상태_변경시_저장하고_색인_업데이트_이벤트를_발행한다() {
		// given
		UUID memberId = uuid();

		Product existing = ProductTestFactory.onSale();
		UUID productId = existing.getId();
		UUID shopId = existing.getShopId();

		given(productRepository.findById(productId)).willReturn(Optional.of(existing));
		given(shopOwnershipClient.getOwnerMemberId(shopId)).willReturn(memberId);
		given(productRepository.save(existing)).willReturn(existing);

		// when
		ProductInfo result = productService.updateStatus(memberId, productId, ProductStatus.HIDDEN);

		// then
		assertThat(result).isNotNull();
		then(productRepository).should().save(existing);
		then(productIndexProducer).should().sendProductUpdateEvent(existing);
	}

	@Test
	void 상품_판매중단시_저장하고_색인_업데이트_이벤트를_발행한다() {
		// given
		UUID memberId = uuid();

		Product existing = ProductTestFactory.onSale();
		UUID productId = existing.getId();
		UUID shopId = existing.getShopId();

		given(productRepository.findById(productId)).willReturn(Optional.of(existing));
		given(shopOwnershipClient.getOwnerMemberId(shopId)).willReturn(memberId);
		given(productRepository.save(existing)).willReturn(existing);

		// when
		productService.discontinueProduct(memberId, productId);

		// then
		then(productRepository).should().save(existing);
		then(productIndexProducer).should().sendProductUpdateEvent(existing);
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
	}

	@Test
	void 판매중_상품_단건조회시_없으면_OnSaleProductNotFoundException() {
		// given
		UUID productId = uuid();

		given(productRepository.findByIdAndStatus(productId, ProductStatus.ON_SALE))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.getOnSaleProduct(productId))
			.isInstanceOf(OnSaleProductNotFoundException.class);
	}

	@Test
	void 상품ID목록으로_shopId조회시_null또는빈리스트면_빈맵을_반환한다() {
		// when & then
		assertThat(productService.getShopIdsByProductIds(null)).isEmpty();
		assertThat(productService.getShopIdsByProductIds(List.of())).isEmpty();

		then(productRepository).shouldHaveNoInteractions();
	}

	@Test
	void 상품ID목록으로_shopId조회시_하나라도_없으면_ProductNotFoundException() {
		// given
		UUID p1 = uuid();
		UUID p2 = uuid();

		Product onlyOne = ProductTestFactory.onSale();
		given(productRepository.findAllByIdIn(List.of(p1, p2))).willReturn(List.of(onlyOne));

		// when & then
		assertThatThrownBy(() -> productService.getShopIdsByProductIds(List.of(p1, p2)))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	void 상품ID목록으로_shopId조회시_중복ID는_distinct_처리된다() {
		// given
		Product p = ProductTestFactory.onSale();
		UUID pid = p.getId();

		given(productRepository.findAllByIdIn(List.of(pid))).willReturn(List.of(p));

		// when
		Map<UUID, UUID> result = productService.getShopIdsByProductIds(List.of(pid, pid, pid));

		// then
		assertThat(result).containsEntry(pid, p.getShopId());
		then(productRepository).should().findAllByIdIn(List.of(pid));
	}

	@Test
	void 판매중_상품_목록조회는_findByStatus로_조회한다() {
		// given
		Page<Product> page = new PageImpl<>(
			List.of(ProductTestFactory.onSale()),
			DEFAULT_PAGE,
			1
		);
		given(productRepository.findByStatus(ProductStatus.ON_SALE, DEFAULT_PAGE)).willReturn(page);

		// when
		Page<ProductInfo> result = productService.getOnSaleProducts(DEFAULT_PAGE);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		then(productRepository).should().findByStatus(ProductStatus.ON_SALE, DEFAULT_PAGE);
	}

	@Test
	void 특정상점_상품조회는_소유권검증후_findByShopId로_조회한다() {
		// given
		UUID memberId = uuid();
		UUID shopId = uuid();

		given(shopOwnershipClient.getOwnerMemberId(shopId)).willReturn(memberId);

		Page<Product> page = new PageImpl<>(
			List.of(ProductTestFactory.onSale()),
			DEFAULT_PAGE,
			1
		);
		given(productRepository.findByShopId(shopId, DEFAULT_PAGE)).willReturn(page);

		// when
		Page<ProductInfo> result = productService.getProductsByShop(memberId, shopId, DEFAULT_PAGE);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		then(shopOwnershipClient).should().getOwnerMemberId(shopId);
		then(productRepository).should().findByShopId(shopId, DEFAULT_PAGE);
	}

	@Test
	void 판매중단된_상품_상태변경시_PRODUCT_STATUS_CHANGE_NOT_ALLOWED_BaseException() {
		// given
		UUID memberId = uuid();

		Product discontinued = ProductTestFactory.discontinued();
		UUID productId = discontinued.getId();
		UUID shopId = discontinued.getShopId();

		given(productRepository.findById(productId)).willReturn(Optional.of(discontinued));
		given(shopOwnershipClient.getOwnerMemberId(shopId)).willReturn(memberId);

		// when & then
		assertThatThrownBy(() ->
			productService.updateStatus(memberId, productId, ProductStatus.HIDDEN)
		)
			.isInstanceOf(BaseException.class)
			.satisfies(ex -> {
				BaseException be = (BaseException) ex;
				assertThat(be.getErrorCode())
					.isEqualTo(ProductErrorCode.PRODUCT_STATUS_CHANGE_NOT_ALLOWED);
			});

		then(productRepository).should(never()).save(any());
		then(productIndexProducer).shouldHaveNoInteractions();
	}

	@Test
	void 특정상점_상품조회시_소유자가_아니면_SHOP_FORBIDDEN_BaseException() {
		// given
		UUID memberId = uuid();
		UUID shopId = uuid();

		UUID otherOwner = uuid();
		given(shopOwnershipClient.getOwnerMemberId(shopId)).willReturn(otherOwner);

		// when & then
		assertThatThrownBy(() -> productService.getProductsByShop(memberId, shopId, PageRequest.of(0, 10)))
			.isInstanceOf(BaseException.class)
			.satisfies(ex -> {
				BaseException be = (BaseException) ex;
				assertThat(be.getErrorCode()).isEqualTo(ProductErrorCode.SHOP_FORBIDDEN);
			});

		then(productRepository).shouldHaveNoInteractions();
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

	private static FeignException.NotFound feignNotFound() {
		return new FeignException.NotFound("NOT_FOUND", request(), null, null);
	}

	private static FeignException.InternalServerError feignInternalServerError() {
		return new FeignException.InternalServerError("INTERNAL_SERVER_ERROR", request(), null, null);
	}

	private static Request request() {
		return Request.create(Request.HttpMethod.GET, "/test", Map.of(), null, new RequestTemplate());
	}
}
