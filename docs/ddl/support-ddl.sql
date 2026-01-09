CREATE SCHEMA IF NOT EXISTS support;

CREATE TABLE support."review_summary" (
	id uuid NOT NULL,
	product_id uuid NOT NULL,
    rating int NOT NULL,
    summary text NOT NULL,
	created_at timestamp NOT NULL,
	modified_at timestamp NOT NULL,
	CONSTRAINT pk_review_summary PRIMARY KEY (id),
    CONSTRAINT uk_review_summary_product_rating UNIQUE (product_id, rating)
);
