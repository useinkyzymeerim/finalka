DROP TABLE IF EXISTS purchase_products CASCADE;

CREATE TABLE purchase_products (
                                   product_id BIGINT NOT NULL,
                                   purchase_id BIGINT NOT NULL,
                                   CONSTRAINT FKd82k86c2e721ymg3bw91xdnqc FOREIGN KEY (product_id) REFERENCES product_of_shop,
                                   CONSTRAINT FKquuf4xmoqfcnww0m8dl69wef0 FOREIGN KEY (purchase_id) REFERENCES purchase
);