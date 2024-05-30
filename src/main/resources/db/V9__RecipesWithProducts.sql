DROP TABLE IF EXISTS recipes_with_products CASCADE;

CREATE SEQUENCE recipes_with_products_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE recipes_with_products (
                                       id BIGINT NOT NULL DEFAULT nextval('recipes_with_products_seq'),
                                       quantity_of_product INTEGER,
                                       product_id BIGINT,
                                       recipe_id BIGINT,
                                       units_enum VARCHAR(255) CHECK (units_enum IN ('gram','milliliter','litre','teaspoon','spoon','Pieces')),
                                       PRIMARY KEY (id),
                                       CONSTRAINT FKgym9y05hyhjs824rhwr6srml3 FOREIGN KEY (product_id) REFERENCES products,
                                       CONSTRAINT FKqjhlk3bc6grhj4cnlyf2nqh2m FOREIGN KEY (recipe_id) REFERENCES recipes
);