package com.node5.catalogservice.product.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.product.application.ProductImageService;
import com.node5.catalogservice.product.application.dto.PresignedUrlInfo;
import com.node5.catalogservice.product.presentation.dto.ConfirmImageRequest;
import com.node5.catalogservice.product.presentation.dto.ConfirmImageResponse;
import com.node5.catalogservice.product.presentation.dto.PresignedUrlRequest;
import com.node5.catalogservice.product.presentation.dto.PresignedUrlResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/seller")
@RequiredArgsConstructor
public class ProductImageController {

	private final ProductImageService productImageService;

	@Tag(name = "Seller Products", description = "판매자 상품 관리 API")
	@PostMapping("/products/images/presigned-url")
	@Operation(summary = "상품 이미지 업로드 Presigned URL 발급", description = "이미지 업로드용 Presigned URL과 객체 key를 발급합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Presigned URL 발급 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다.")
	})
	public ResponseEntity<PresignedUrlResponse> createPresignedUrl(
		@Valid @RequestBody PresignedUrlRequest request
	) {
		PresignedUrlInfo info = productImageService.createUploadUrl(request.contentType());
		return ResponseEntity.ok(new PresignedUrlResponse(info.url(), info.key()));
	}

	@PostMapping("/products/images/confirm")
	public ResponseEntity<ConfirmImageResponse> confirmPresignedUrl(
		@Valid @RequestBody ConfirmImageRequest request
	) {
		String productKey = productImageService.confirm(request.tempKey());
		return ResponseEntity.ok(new ConfirmImageResponse(productKey));
	}
}
