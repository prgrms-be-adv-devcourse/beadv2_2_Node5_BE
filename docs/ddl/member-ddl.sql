CREATE SCHEMA IF NOT EXISTS member;

-- DROP TABLE IF EXISTS member."member";
-- DROP TABLE IF EXISTS member."o_auth";
-- DROP TABLE IF EXISTS member."inquiry";

CREATE TABLE member."member" (
	id uuid NOT NULL,
	name varchar(20) NOT NULL,
	nickname varchar(20) NOT NULL,
	email varchar(100) NOT NULL,
	phone_number varchar(20) NOT NULL,
	address varchar(100) NOT NULL,
	roles jsonb NOT NULL,
	status varchar(20) NOT NULL,
	created_at timestamp NOT NULL,
	modified_at timestamp NOT NULL,
	deleted_at timestamp NULL,
	CONSTRAINT pk_member PRIMARY KEY (id)
);

CREATE TABLE member."o_auth" (
	id uuid NOT NULL,
	member_id uuid NOT NULL,
	provider varchar(20) NOT NULL,
	provider_id varchar(100) NOT NULL,
	created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL,
	CONSTRAINT pk_o_auth PRIMARY KEY (id)
);

CREATE TABLE member.role (
    name varchar(20) PRIMARY KEY
);

CREATE TABLE member.endpoint (
    id uuid NOT NULL,
    role varchar(20) NOT NULL,
    http_method varchar(10) NOT NULL,
    path_pattern varchar(200) NOT NULL,
    CONSTRAINT pk_endpoint PRIMARY KEY (id),
    CONSTRAINT uq_endpoint UNIQUE (role, http_method, path_pattern)
);

CREATE TABLE member."inquiry" (
	id uuid NOT NULL,
	member_id uuid NOT NULL,
    title varchar(100) NOT NULL,
    message text NOT NULL,
    inquiry_category varchar(100) NOT NULL,
    status           varchar(20) default 'RECEIVED'::character varying not null,
	created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL,
	CONSTRAINT pk_inquiry PRIMARY KEY (id)
);

CREATE TABLE member."inquiry_answer" (
	id uuid NOT NULL,
    inquiry_id uuid NOT NULL,
	answered_admin_id uuid NOT NULL,
    message text NOT NULL,
	created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL,
	CONSTRAINT pk_inquiry_answer PRIMARY KEY (id),
    CONSTRAINT uq_inquiry_answer UNIQUE (inquiry_id)
);

CREATE TABLE member."shop" (
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
