package com.node5.catalogservice.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 업로드용 Presigned URL 생성 요청")
public record PresignedUrlRequest(
	@Schema(description = "업로드할 파일의 Content-Type", example = "image/png")
	String contentType
) {
}
