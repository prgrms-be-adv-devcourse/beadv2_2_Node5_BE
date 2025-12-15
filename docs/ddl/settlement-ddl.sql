CREATE SCHEMA IF NOT EXISTS settlement;

CREATE TABLE settlement.settlement_result (
	fee_amount numeric(38, 2) NOT NULL,
	fee_rate numeric(38, 2) NOT NULL,
	payout_amount numeric(38, 2) NOT NULL,
	sales_amount numeric(38, 2) NOT NULL,
	target_end_date date NOT NULL,
	target_start_date date NOT NULL,
	batch_id int8 NOT NULL,
	payout_at timestamp(6) NULL,
	settled_at timestamp(6) NOT NULL,
	id uuid NOT NULL,
	shop_id uuid NOT NULL,
	status varchar(20) NOT NULL,
	error_msg varchar(255) NULL,
	CONSTRAINT settlement_result_pkey PRIMARY KEY (id),
	CONSTRAINT uk_settlement_source_order_product UNIQUE (order_id, product_id),
	CONSTRAINT settlement_result_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'PAID'::character varying, 'FAILED'::character varying])::text[])))
);

CREATE TABLE settlement.settlement_source (
	item_amount numeric(38, 2) NOT NULL,
	paid_at timestamp(6) NOT NULL,
	id uuid NOT NULL,
	order_id uuid NOT NULL,
	product_id uuid NOT NULL,
	shop_id uuid NOT NULL,
	status varchar(255) NOT NULL,
	CONSTRAINT settlement_source_pkey PRIMARY KEY (id),
	CONSTRAINT settlement_source_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'COMPLETED'::character varying])::text[])))
);
