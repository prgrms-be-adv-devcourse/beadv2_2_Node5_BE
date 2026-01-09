CREATE SCHEMA IF NOT EXISTS catalog;

CREATE TABLE catalog.cart (
    id uuid NOT NULL,
    member_id uuid NOT NULL,
    created_at timestamp(6) NOT NULL,
    modified_at timestamp(6) NOT NULL,
    CONSTRAINT pk_cart PRIMARY KEY (id),
    CONSTRAINT uq_cart_member UNIQUE (member_id)
);

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

CREATE TABLE catalog.cart_item (
    id uuid NOT NULL,
    cart_id uuid NOT NULL,
    product_id uuid NOT NULL,
    quantity int4 NOT NULL,
    created_at timestamp(6) NOT NULL,
    modified_at timestamp(6) NOT NULL,

    CONSTRAINT pk_cart_item PRIMARY KEY (id),

    CONSTRAINT uq_cart_item_cart_product UNIQUE (cart_id, product_id),

    CONSTRAINT fk_cart_item_cart
        FOREIGN KEY (cart_id) REFERENCES catalog.cart(id),

    CONSTRAINT fk_cart_item_product
        FOREIGN KEY (product_id) REFERENCES catalog.product(id)
);
