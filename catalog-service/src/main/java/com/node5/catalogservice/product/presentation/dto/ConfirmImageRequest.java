package com.node5.catalogservice.product.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmImageRequest(
	@NotBlank
	String tempKey
) {
}
