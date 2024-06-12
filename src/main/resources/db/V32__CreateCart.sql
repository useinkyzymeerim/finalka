DROP TABLE IF EXISTS cart CASCADE;

CREATE SEQUENCE cart_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE cart (
                      id BIGINT NOT NULL DEFAULT nextval('cart_seq'),
                      total_price,
                      user_id BIGINT UNIQUE,
                      PRIMARY KEY (id),
                      CONSTRAINT FKg5uhi8vpsuy0lgloxk2h4w5o6 FOREIGN KEY (user_id) REFERENCES users
);