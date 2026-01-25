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

@Tag(name = "Product Images", description = "상품 이미지 관리 API")
@RestController
@RequestMapping("${api.v1}/seller")
@RequiredArgsConstructor
public class ProductImageController {

	private final ProductImageService productImageService;

	@PostMapping("/products/images/presigned-url")
	@Operation(summary = "상품 이미지 업로드 Presigned URL 발급", description = "이미지 업로드용 Presigned URL과 객체 key를 발급합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Presigned URL 발급 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다."),
		@ApiResponse(responseCode = "415", description = "지원하지 않는 이미지 타입입니다.")
	})
	public ResponseEntity<PresignedUrlResponse> createPresignedUrl(
		@Valid @RequestBody PresignedUrlRequest request
	) {
		PresignedUrlInfo info = productImageService.createUploadUrl(request.contentType());
		return ResponseEntity.ok(new PresignedUrlResponse(info.url(), info.tempKey()));
	}

	@PostMapping("/products/images/confirm")
	@Operation(summary = "상품 이미지 업로드 확정", description = "임시 객체 key를 확정된 상품 이미지 key로 변경합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 이미지 확정 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다."),
		@ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없습니다."),
		@ApiResponse(responseCode = "413", description = "이미지 파일 크기가 허용 범위를 초과했습니다."),
		@ApiResponse(responseCode = "415", description = "지원하지 않는 이미지 타입입니다.")
	})
	public ResponseEntity<ConfirmImageResponse> confirmPresignedUrl(
		@Valid @RequestBody ConfirmImageRequest request
	) {
		String productKey = productImageService.confirm(request.tempKey());
		return ResponseEntity.ok(new ConfirmImageResponse(productKey));
	}
}
