package com.node5.catalogservice.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 업로드용 Presigned URL 응답")
public record PresignedUrlResponse(
	@Schema(
		description = "업로드에 사용할 Presigned URL",
		example = "https://s3.ap-northeast-2.amazonaws.com/bucket/uploads/uuid?X-Amz-Algorithm=AWS4-HMAC-SHA256..."
	)
	String url,

	@Schema(description = "업로드 후 저장될 객체 키", example = "product/550e8400-e29b-41d4-a716-446655440000")
	String key
) {
}
