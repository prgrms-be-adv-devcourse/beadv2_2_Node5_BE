package com.node5.catalogservice.search.application;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.search.application.dto.ProductSearchCommand;
import com.node5.catalogservice.search.application.dto.ProductSearchResponse;
import com.node5.catalogservice.search.application.port.ProductSearchPort;
import com.node5.catalogservice.search.application.query.QueryNormalizer;
import com.node5.catalogservice.search.domain.ProductDocument;
import com.node5.catalogservice.search.domain.ProductSearchSort;
import com.node5.catalogservice.search.exception.SearchErrorCode;
import com.node5.common.exception.BaseException;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceTest {

	private static final PageRequest DEFAULT_PAGE = PageRequest.of(0, 10);

	@Mock
	private ProductSearchPort productSearchPort;

	@Mock
	private QueryNormalizer queryNormalizer;

	@InjectMocks
	private ProductSearchService productSearchService;

	@Test
	void 가격범위가_완성되지_않으면_PRICE_RANGE_INCOMPLETE_BaseException() {
		// given
		ProductSearchCommand cmd = searchCommand("테스트", null, null, 1000, null, null);

		// when
		BaseException ex = catchThrowableOfType(
			() -> productSearchService.search(cmd, DEFAULT_PAGE),
			BaseException.class
		);

		// then
		assertThat(ex.getErrorCode()).isEqualTo(SearchErrorCode.PRICE_RANGE_INCOMPLETE);
		then(queryNormalizer).shouldHaveNoInteractions();
		then(productSearchPort).shouldHaveNoInteractions();
	}

	@Test
	void 가격범위에서_최소값이_최대값보다_크면_INVALID_PRICE_RANGE_BaseException() {
		// given
		ProductSearchCommand cmd = searchCommand("테스트", null, null, 5000, 1000, null);

		// when
		BaseException ex = catchThrowableOfType(
			() -> productSearchService.search(cmd, DEFAULT_PAGE),
			BaseException.class
		);

		// then
		assertThat(ex.getErrorCode()).isEqualTo(SearchErrorCode.INVALID_PRICE_RANGE);
		then(queryNormalizer).shouldHaveNoInteractions();
		then(productSearchPort).shouldHaveNoInteractions();
	}

	@Test
	void 검색_요청시_키워드를_정규화한_커맨드로_port를_호출한다() {
		// given
		UUID shopId = uuid();

		ProductSearchCommand cmd = searchCommand(
			"  Te-ST_/키워드  ",
			shopId,
			ProductCategory.SERVICE_SUBSCRIPTION,
			1000,
			5000,
			ProductSearchSort.LATEST
		);

		given(queryNormalizer.normalize(cmd.keyword())).willReturn("te st 키워드");
		given(productSearchPort.search(any(ProductSearchCommand.class), any()))
			.willReturn(Page.empty(DEFAULT_PAGE));

		// when
		productSearchService.search(cmd, DEFAULT_PAGE);

		// then
		ArgumentCaptor<ProductSearchCommand> captor = ArgumentCaptor.forClass(ProductSearchCommand.class);
		then(productSearchPort).should().search(captor.capture(), eq(DEFAULT_PAGE));

		ProductSearchCommand sent = captor.getValue();
		assertThat(sent.keyword()).isEqualTo("te st 키워드");
		assertThat(sent.shopId()).isEqualTo(shopId);
		assertThat(sent.category()).isEqualTo(ProductCategory.SERVICE_SUBSCRIPTION);
		assertThat(sent.minPrice()).isEqualTo(1000);
		assertThat(sent.maxPrice()).isEqualTo(5000);
		assertThat(sent.sort()).isEqualTo(ProductSearchSort.LATEST);

		then(queryNormalizer).should().normalize(cmd.keyword());
	}

	@Test
	void 검색_성공시_ProductDocument를_ProductSearchResponse로_매핑한다() {
		// given
		LocalDateTime base = LocalDateTime.of(2025, 1, 1, 0, 0);

		ProductSearchCommand cmd = searchCommand("테스트", null, null, null, null, null);

		given(queryNormalizer.normalize(cmd.keyword())).willReturn("테스트");

		ProductDocument d1 = new ProductDocument(
			"1", "1", "테스트 상품", "테스트 상품", "TEST",
			"product/test-thumb-1.png", 1000L, "ON_SALE", base.plusDays(1)
		);
		ProductDocument d2 = new ProductDocument(
			"2", "2", "사과 상품", "사과 상품", "FOOD_BEVERAGE",
			"product/apple-thumb.png", 2000L, "ON_SALE", base.plusDays(2)
		);

		Page<ProductDocument> docs = new PageImpl<>(List.of(d1, d2), DEFAULT_PAGE, 2);
		given(productSearchPort.search(any(ProductSearchCommand.class), any()))
			.willReturn(docs);

		// when
		Page<ProductSearchResponse> result = productSearchService.search(cmd, DEFAULT_PAGE);

		// then
		assertThat(result.getContent()).hasSize(2);

		ProductSearchResponse r1 = result.getContent().get(0);
		assertThat(r1.productId()).isEqualTo("1");
		assertThat(r1.shopId()).isEqualTo("1");
		assertThat(r1.name()).isEqualTo("테스트 상품");
		assertThat(r1.category()).isEqualTo("TEST");
		assertThat(r1.thumbnailKey()).isEqualTo("product/test-thumb-1.png");
		assertThat(r1.price()).isEqualTo(1000L);
		assertThat(r1.status()).isEqualTo("ON_SALE");
		assertThat(r1.createdAt()).isEqualTo(base.plusDays(1));

		ProductSearchResponse r2 = result.getContent().get(1);
		assertThat(r2.productId()).isEqualTo("2");
		assertThat(r2.shopId()).isEqualTo("2");
		assertThat(r2.name()).isEqualTo("사과 상품");
		assertThat(r2.category()).isEqualTo("FOOD_BEVERAGE");
		assertThat(r2.thumbnailKey()).isEqualTo("product/apple-thumb.png");
		assertThat(r2.price()).isEqualTo(2000L);
		assertThat(r2.status()).isEqualTo("ON_SALE");
		assertThat(r2.createdAt()).isEqualTo(base.plusDays(2));
	}

	private static UUID uuid() {
		return UUID.randomUUID();
	}

	private ProductSearchCommand searchCommand(
		String keyword,
		UUID shopId,
		ProductCategory category,
		Integer minPrice,
		Integer maxPrice,
		ProductSearchSort sort
	) {
		return new ProductSearchCommand(keyword, shopId, category, minPrice, maxPrice, sort);
	}
}
