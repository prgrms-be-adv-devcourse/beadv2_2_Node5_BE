package com.node5.catalogservice.product.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.product.application.ProductImageService;
import com.node5.catalogservice.product.application.dto.PresignedUrlInfo;
import com.node5.catalogservice.product.presentation.dto.PresignedUrlRequest;
import com.node5.catalogservice.product.presentation.dto.PresignedUrlResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/seller")
@RequiredArgsConstructor
public class ProductImageController {

	private final ProductImageService productImageService;

	@PostMapping("/products/images/presigned-url")
	public ResponseEntity<PresignedUrlResponse> createPresignedUrl(@Valid @RequestBody PresignedUrlRequest request) {
		PresignedUrlInfo info = productImageService.createUploadUrl(request.contentType());
		return ResponseEntity.ok(new PresignedUrlResponse(info.url(), info.key()));
	}
}
