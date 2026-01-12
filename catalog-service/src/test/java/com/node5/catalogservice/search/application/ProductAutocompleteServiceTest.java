package com.node5.catalogservice.search.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.node5.catalogservice.search.application.port.ProductSearchPort;

@ExtendWith(MockitoExtension.class)
class ProductAutocompleteServiceTest {

	@Mock
	private ProductSearchPort productSearchPort;

	@InjectMocks
	private ProductAutocompleteService productAutocompleteService;

	@Test
	void 키워드가_최소길이_미만이면_빈결과를_반환한다() {
		// when
		List<String> result =
			productAutocompleteService.autocomplete("케");

		// then
		assertThat(result).isEmpty();
		verifyNoInteractions(productSearchPort);
	}

	@Test
	void 공백이_포함된_키워드도_정상_처리한다() {
		// given
		when(productSearchPort.autocomplete(any()))
			.thenReturn(List.of("무선 로봇 청소기"));

		// when
		List<String> result = productAutocompleteService.autocomplete("무선 로");

		// then
		assertThat(result).containsExactly("무선 로봇 청소기");
	}

	@Test
	void prefix_자동완성_결과를_중복없이_반환한다() {
		// given
		when(productSearchPort.autocomplete(any()))
			.thenReturn(List.of(
				"케이크",
				"카페라떼",
				"케이크" // 중복
			));

		// when
		List<String> result =
			productAutocompleteService.autocomplete("케이");

		// then
		assertThat(result)
			.containsExactly("케이크", "카페라떼");
	}
}
