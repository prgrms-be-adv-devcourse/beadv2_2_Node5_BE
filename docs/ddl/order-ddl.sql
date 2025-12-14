CREATE SCHEMA IF NOT EXISTS "order";

CREATE TABLE "order"."order" (
	total_amount numeric(38, 2) NULL,
	closed_at timestamp(6) NULL,
	created_at timestamp(6) NULL,
	modified_at timestamp(6) NULL,
	paid_at timestamp(6) NULL,
	id uuid NOT NULL,
	member_id uuid NOT NULL,
	subscription_id uuid NULL,
	order_num varchar(20) NOT NULL,
	order_type varchar(20) NOT NULL,
	status varchar(30) NOT NULL,
	recipient_name varchar(50) NOT NULL,
	recipient_address varchar(255) NOT NULL
);


CREATE TABLE "order".order_item (
	quantity int4 NOT NULL,
	total_price numeric(38, 2) NOT NULL,
	unit_price numeric(38, 2) NOT NULL,
	created_at timestamp(6) NULL,
	modified_at timestamp(6) NULL,
	id uuid NOT NULL,
	order_id uuid NOT NULL,
	product_id uuid NOT NULL,
	img_url varchar(255) NULL,
	"name" varchar(255) NOT NULL,
	CONSTRAINT order_item_pkey PRIMARY KEY (id)
);
