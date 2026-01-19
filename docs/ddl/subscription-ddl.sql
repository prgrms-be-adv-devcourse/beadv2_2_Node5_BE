CREATE SCHEMA IF NOT EXISTS subscription;

CREATE TABLE subscription.subscription (
                                           id UUID NOT NULL,
                                           member_id UUID NOT NULL,
                                           shop_id UUID NOT NULL,

                                           product_id UUID NOT NULL,
                                           product_name VARCHAR(100),
                                           thumbnail_url VARCHAR,
                                           price_per_item NUMERIC(38, 2) NOT NULL,
                                           quantity INTEGER NOT NULL DEFAULT 1,
                                           total_price NUMERIC(38, 2),

                                           subscription_status VARCHAR(20) NOT NULL,
                                           next_run_date DATE NOT NULL,
                                           last_processed_run_date DATE,
                                           delivery_address VARCHAR(100),

                                           created_at TIMESTAMP NOT NULL DEFAULT now(),
                                           modified_at TIMESTAMP NOT NULL DEFAULT now(),
                                           deleted_at TIMESTAMP,

                                           PRIMARY KEY (id)
);

CREATE INDEX idx_subscription_member_id ON subscription.subscription(member_id);
CREATE INDEX idx_subscription_shop_id ON subscription.subscription(shop_id);
CREATE INDEX idx_subscription_product_id ON subscription.subscription(product_id);
CREATE INDEX idx_subscription_next_run_date ON subscription.subscription(next_run_date);

COMMENT ON TABLE subscription.subscription IS '구독 테이블';

COMMENT ON COLUMN subscription.subscription.id IS '구독 ID';
COMMENT ON COLUMN subscription.subscription.member_id IS '회원 ID';
COMMENT ON COLUMN subscription.subscription.shop_id IS '상점 ID';
COMMENT ON COLUMN subscription.subscription.product_id IS '상품 ID';
COMMENT ON COLUMN subscription.subscription.product_name IS '상품명';
COMMENT ON COLUMN subscription.subscription.thumbnail_url IS '상품 이미지 url';
COMMENT ON COLUMN subscription.subscription.price_per_item IS '단가';
COMMENT ON COLUMN subscription.subscription.quantity IS '수량';
COMMENT ON COLUMN subscription.subscription.total_price IS '총 금액';
COMMENT ON COLUMN subscription.subscription.subscription_status IS '구독 상태';
COMMENT ON COLUMN subscription.subscription.next_run_date IS '다음 결제일';
COMMENT ON COLUMN subscription.subscription.last_processed_run_date IS '배치 처리 기준일';
COMMENT ON COLUMN subscription.subscription.delivery_address IS '배송지';
COMMENT ON COLUMN subscription.subscription.created_at IS '생성일';
COMMENT ON COLUMN subscription.subscription.modified_at IS '수정일';
COMMENT ON COLUMN subscription.subscription.deleted_at IS '삭제일';

CREATE TABLE subscription.subscription_recurrence_rule (
                                                           id UUID NOT NULL,
                                                           subscription_id UUID NOT NULL,

                                                           recurrence_type VARCHAR(20) NOT NULL,
                                                           day_of_week INTEGER,
                                                           day_of_month INTEGER,

                                                           created_at TIMESTAMP NOT NULL DEFAULT now(),
                                                           modified_at TIMESTAMP NOT NULL DEFAULT now(),

                                                           PRIMARY KEY (id),

                                                           FOREIGN KEY (subscription_id) REFERENCES subscription.subscription(id)
);

CREATE INDEX idx_recurrence_subscription_id
    ON subscription.subscription_recurrence_rule(subscription_id);

CREATE TABLE subscription.kafka_consumer_failures (
    id BIGSERIAL PRIMARY KEY,
    topic VARCHAR(255) NOT NULL,
    partition INTEGER NOT NULL,
    record_offset BIGINT NOT NULL,
    record_key TEXT,
    payload TEXT,
    exception_class VARCHAR(255),
    exception_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_kafka_consumer_failures_created_at
    ON subscription.kafka_consumer_failures(created_at);
