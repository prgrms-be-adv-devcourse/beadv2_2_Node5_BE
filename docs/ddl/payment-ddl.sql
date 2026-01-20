CREATE SCHEMA IF NOT EXISTS payment;

CREATE TABLE payment."payment" (
                                   id uuid NOT NULL,
                                   member_id uuid NOT NULL,
                                   payment_key varchar(200) UNIQUE,
                                   order_id varchar(100),
                                   amount BIGINT NOT NULL,
                                   method varchar(50),
                                   status varchar(20) NOT NULL,
                                   requested_at timestamp NULL,
                                   approved_at timestamp NULL,
                                   fail_reason text NULL,
                                   created_at timestamp NOT NULL,
                                   modified_at timestamp NOT NULL,
                                   CONSTRAINT pk_payment PRIMARY KEY (id)
);

CREATE TABLE payment."payment_outbox" (
                                id UUID NOT NULL,
                                aggregate_type VARCHAR(50) NOT NULL,
                                aggregate_id UUID NOT NULL,
                                event_type VARCHAR(100) NOT NULL,
                                payload TEXT NOT NULL,
                                status VARCHAR(20) NOT NULL,
                                retry_count INT NOT NULL DEFAULT 0,
                                last_error_message TEXT NULL,
                                created_at TIMESTAMP NOT NULL,
                                modified_at TIMESTAMP NOT NULL,
                                published_at TIMESTAMP NULL,
                                CONSTRAINT pk_payment_outbox PRIMARY KEY (id)
);

CREATE INDEX idx_outbox_status_created_at
    ON payment.payment_outbox (status, created_at)
    WHERE status IN ('READY', 'FAILED'); -- 부분 인덱스(Partial Index)로 효율 극대화