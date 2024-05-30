DROP TABLE IF EXISTS product_of_shop CASCADE;

CREATE SEQUENCE product_of_shop_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE product_of_shop (
                                 id BIGINT NOT NULL DEFAULT nextval('product_of_shop_seq'),
                                 deleted BOOLEAN NOT NULL,
                                 in_stock BOOLEAN NOT NULL,
                                 price FLOAT(53),
                                 quantity INTEGER,
                                 quantity_in_stock INTEGER,
                                 cart_id BIGINT,
                                 deletion_time TIMESTAMP(6),
                                 user_id BIGINT,
                                 product_name VARCHAR(255),
                                 type VARCHAR(255),
                                 units2enum VARCHAR(255) CHECK (units2enum IN ('kilogram','Pieces')),
                                 PRIMARY KEY (id),
                                 CONSTRAINT FKspiuyyy7xqo5cppkm1tooitxu FOREIGN KEY (cart_id) REFERENCES cart,
                                 CONSTRAINT FK2dgrgdr2pfrbndjmji8qc2obu FOREIGN KEY (user_id) REFERENCES users
);