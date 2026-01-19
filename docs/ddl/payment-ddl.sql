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
