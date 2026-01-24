CREATE SCHEMA IF NOT EXISTS catalog;

CREATE TABLE catalog.product (
    id uuid NOT NULL,
    shop_id uuid NOT NULL,
    name varchar(100) NOT NULL,
    description text NULL,
    price numeric(15, 2) NOT NULL,
    status varchar(30) NOT NULL,
    category varchar(255) NOT NULL,
    thumbnail_key varchar(255) NULL,
    created_at timestamp(6) NOT NULL,
    modified_at timestamp(6) NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

CREATE TABLE catalog.cart (
    id uuid NOT NULL,
    member_id uuid NOT NULL,
    created_at timestamp(6) NOT NULL,
    modified_at timestamp(6) NOT NULL,
    CONSTRAINT pk_cart PRIMARY KEY (id),
    CONSTRAINT uq_cart_member UNIQUE (member_id)
);

CREATE TABLE catalog.cart_item (
    id uuid NOT NULL,
    cart_id uuid NOT NULL,
    product_id uuid NOT NULL,
    quantity int4 NOT NULL,
    created_at timestamp(6) NOT NULL,
    modified_at timestamp(6) NOT NULL,
    CONSTRAINT pk_cart_item PRIMARY KEY (id),
    CONSTRAINT uq_cart_item_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES catalog.cart(id),
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES catalog.product(id),
    CONSTRAINT ck_cart_item_quantity CHECK (quantity > 0)
);

CREATE INDEX ix_cart_item_cart ON catalog.cart_item (cart_id);

CREATE TABLE catalog.stock (
    product_id uuid NOT NULL,
    quantity int4 NOT NULL,
    created_at timestamp(6) DEFAULT now() NOT NULL,
    modified_at timestamp(6) DEFAULT now() NOT NULL,
    CONSTRAINT pk_stock PRIMARY KEY (product_id),
    CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES catalog.product(id),
    CONSTRAINT ck_stock_quantity CHECK (quantity >= 0)
);

CREATE TABLE catalog.stock_reservation (
    id uuid NOT NULL,
    order_id uuid NOT NULL,
    product_id uuid NOT NULL,
    quantity int4 NOT NULL,
    status varchar(20) NOT NULL,
    created_at timestamp(6) DEFAULT now() NOT NULL,
    modified_at timestamp(6) DEFAULT now() NOT NULL,
    CONSTRAINT pk_stock_reservation PRIMARY KEY (id),
    CONSTRAINT fk_stock_reservation_product FOREIGN KEY (product_id) REFERENCES catalog.product(id),
    CONSTRAINT ck_stock_reservation_quantity CHECK (quantity > 0),
    CONSTRAINT ck_stock_reservation_status CHECK (status IN ('HELD','COMMITTED','RELEASED')),
    CONSTRAINT uq_stock_reservation UNIQUE (order_id, product_id)
);

CREATE TABLE catalog.product_idempotency (
    idempotency_key varchar(80) NOT NULL,
    product_id uuid NULL,
    status varchar(20) NOT NULL,
    created_at timestamp(6) NOT NULL DEFAULT now(),
    modified_at timestamp(6) NOT NULL DEFAULT now(),
    CONSTRAINT pk_product_idempotency PRIMARY KEY (idempotency_key),
    CONSTRAINT ck_product_idempotency_status CHECK (status IN ('PROCESSING', 'COMPLETED', 'FAILED'))
);

CREATE TABLE catalog.processed_event (
    event_type VARCHAR(50) NOT NULL,
    event_id uuid NOT NULL,
    created_at timestamp(6) NOT NULL DEFAULT now(),
    CONSTRAINT pk_processed_event PRIMARY KEY (event_type, event_id)
);

CREATE INDEX ix_stock_reservation_product_status
    ON catalog.stock_reservation (product_id, status);

CREATE INDEX ix_stock_reservation_order
    ON catalog.stock_reservation (order_id);
