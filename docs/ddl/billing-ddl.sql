CREATE SCHEMA IF NOT EXISTS billing;

-- DROP TABLE IF EXISTS billing."payment";
-- DROP TABLE IF EXISTS billing."payment_failure";
-- DROP TABLE IF EXISTS billing."wallet";
-- DROP TABLE IF EXISTS billing."wallet_deposit_log";
-- DROP TABLE IF EXISTS billing."wallet_withdraw_log";

CREATE TABLE billing."wallet" (
    id uuid NOT NULL,
    member_id uuid NOT NULL,
    balance BIGINT NOT NULL DEFAULT 0,
    created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL,
    deleted_at timestamp NULL,
    CONSTRAINT pk_wallet PRIMARY KEY (id),
    CONSTRAINT uq_wallet_member_id UNIQUE (member_id)
);

CREATE TABLE billing."wallet_deposit_log" (
    id uuid NOT NULL,
    member_id uuid NOT NULL,
    settlement_id uuid NOT NULL,
    amount BIGINT NOT NULL,
    created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL,
    CONSTRAINT pk_wallet_deposit_log PRIMARY KEY (id),
    CONSTRAINT uq_wallet_deposit_settlement UNIQUE (settlement_id)
);

CREATE TABLE billing."wallet_withdraw_log" (
    id uuid NOT NULL,
    member_id uuid NOT NULL,
    order_id uuid NOT NULL,
    amount BIGINT NOT NULL,
    state varchar(20) NOT NULL,
    created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL,
    CONSTRAINT pk_wallet_withdraw_log PRIMARY KEY (id),
    CONSTRAINT uq_wallet_withdraw_order UNIQUE (order_id)
);

CREATE TABLE billing."payment" (
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

CREATE TABLE billing."payment_failure" (
    id uuid NOT NULL,
    member_id uuid NOT NULL,
    payment_key varchar(200) NOT NULL,
    order_id varchar(100) NOT NULL,
    error_code varchar(50),
    error_message text,
    amount BIGINT,
    created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL,
    CONSTRAINT pk_payment_failure PRIMARY KEY (id),
    CONSTRAINT uq_payment_failure_key UNIQUE (payment_key)
);
