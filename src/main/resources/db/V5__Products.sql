DROP TABLE IF EXISTS products CASCADE;

CREATE SEQUENCE products_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE products (
                          id BIGINT NOT NULL DEFAULT nextval('products_seq'),
                          quantity INTEGER,
                          created_at TIMESTAMP(6),
                          deleted_at TIMESTAMP(6),
                          last_updated_at TIMESTAMP(6),
                          created_by VARCHAR(255),
                          deleted_by VARCHAR(255),
                          last_updated_by VARCHAR(255),
                          product_name VARCHAR(255),
                          units_enum VARCHAR(255) CHECK (units_enum IN ('gram','milliliter','litre','teaspoon','spoon','Pieces')),
                          PRIMARY KEY (id)
);