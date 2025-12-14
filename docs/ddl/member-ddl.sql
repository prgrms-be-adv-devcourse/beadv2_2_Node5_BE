CREATE SCHEMA IF NOT EXISTS member;

-- DROP TABLE IF EXISTS member."member";
-- DROP TABLE IF EXISTS member."o_auth";
-- DROP TABLE IF EXISTS member."inquiry";

CREATE TABLE member."member" (
	id uuid NOT NULL,
	name varchar(20) NOT NULL,
	email varchar(100) NOT NULL,
	phone_number varchar(20) NOT NULL,
	address varchar(100) NOT NULL,
	"roles" varchar(20) DEFAULT 'USER'::character varying NOT NULL,
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

CREATE TABLE member."inquiry" (
	id uuid NOT NULL,
	member_id uuid NOT NULL,
    message text NOT NULL,
    inquiry_category varchar(100) NOT NULL,
	created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL,
	CONSTRAINT pk_inquiry PRIMARY KEY (id)
);
