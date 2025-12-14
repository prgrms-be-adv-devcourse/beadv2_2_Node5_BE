CREATE SCHEMA IF NOT EXISTS shop;

-- DROP TABLE IF EXISTS shop."shop";

CREATE TABLE shop."shop" (
	id uuid NOT NULL,
	member_id uuid NOT NULL,
    shop_email varchar(100) NOT NULL,
    shop_name varchar(50) NOT NULL,
    shop_phone_number varchar(20) NOT NULL,
    shop_registration_number varchar(100) NOT NULL,
    shop_address varchar(100) NOT NULL,
	created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL,
    deleted_at timestamp NULL,
	CONSTRAINT pk_shop PRIMARY KEY (id)
);
