package com.node5.catalogservice.product.application;

import java.util.Set;

public final class ImageUploadPolicy {

	public static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
		"image/png",
		"image/jpeg"
	);

	private ImageUploadPolicy() {}
}
