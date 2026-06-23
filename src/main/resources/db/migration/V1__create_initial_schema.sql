CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    product_name VARCHAR(255) NOT NULL,
    purchase_price NUMERIC(18,2) NOT NULL,
    selling_price NUMERIC(18,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stocks (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    quantity INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_stock_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE
);

CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    transaction_no VARCHAR(20) NOT NULL UNIQUE,
    sale_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    created_by BIGINT,

    CONSTRAINT fk_sale_user
        FOREIGN KEY (created_by)
        REFERENCES users(id)
);

CREATE TABLE sale_details (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    quantity INTEGER NOT NULL,
    purchase_price NUMERIC(18,2) NOT NULL,
    selling_price NUMERIC(18,2) NOT NULL,

    subtotal NUMERIC(18,2) NOT NULL,
    profit NUMERIC(18,2) NOT NULL,

    CONSTRAINT fk_detail_sale
        FOREIGN KEY (sale_id)
        REFERENCES sales(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_detail_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
);