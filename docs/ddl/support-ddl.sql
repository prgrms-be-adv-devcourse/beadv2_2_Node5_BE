CREATE SCHEMA IF NOT EXISTS support;
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE support."review_summary" (
	id uuid NOT NULL,
	product_id uuid NOT NULL,
    summary text NOT NULL,
    summary_end_date date NOT NULL,
	created_at timestamp NOT NULL,
	modified_at timestamp NOT NULL,
	CONSTRAINT pk_review_summary PRIMARY KEY (id),
    CONSTRAINT uk_review_summary_product_id UNIQUE (product_id)
);

CREATE TABLE support."review_static" (
    id uuid NOT NULL,
    product_id uuid NOT NULL,
    review_count int NOT NULL,
    rating_count_1 int NOT NULL,
    rating_count_2 int NOT NULL,
    rating_count_3 int NOT NULL,
    rating_count_4 int NOT NULL,
    rating_count_5 int NOT NULL,
    created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL,
    CONSTRAINT pk_review PRIMARY KEY (id),
    CONSTRAINT uk_review_product UNIQUE (product_id)
);

CREATE TABLE support."review_detail" (
    id uuid NOT NULL,
    product_id uuid NOT NULL,
    member_id uuid NOT NULL,
    nickname varchar(50) NOT NULL,
    order_id uuid NOT NULL,
    rating int NOT NULL,
    body text NULL,
    like_count int NOT NULL,
    embedding vector(1536),
    created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL,
    deleted_at timestamp NULL,
    CONSTRAINT pk_review_detail PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_review_active_purchase
    ON support.review_detail (member_id, order_id, product_id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_review_detail_embedding
    ON support.review_detail
    USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100)
    WHERE deleted_at IS NULL;

CREATE TABLE support."review_like_history" (
    id uuid NOT NULL,
    member_id uuid NOT NULL,
    review_id uuid NOT NULL,
    created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL,
    CONSTRAINT pk_review_like PRIMARY KEY (id),
    CONSTRAINT uk_review_like_member_review UNIQUE (member_id, review_id)
);

CREATE TABLE support."product_embedding" (
    id uuid NOT NULL,
    product_id uuid NOT NULL,
    content text NOT NULL,
    status varchar(20) NOT NULL,
    embedding vector(1536) NOT NULL,
    created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL,
    CONSTRAINT pk_product_embedding PRIMARY KEY (id),
    CONSTRAINT uk_product_embedding_product UNIQUE (product_id)
);

CREATE INDEX idx_product_embedding_embedding
    ON support."product_embedding"
    USING ivfflat (embedding vector_cosine_ops);
