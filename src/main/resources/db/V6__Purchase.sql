DROP TABLE IF EXISTS purchase CASCADE;

CREATE SEQUENCE purchase_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE purchase (
                          id BIGINT NOT NULL DEFAULT nextval('purchase_seq'),
                          total_price FLOAT(53) NOT NULL,
                          cart_id BIGINT,
                          purchase_date TIMESTAMP(6),
                          user_id BIGINT,
                          PRIMARY KEY (id),
                          CONSTRAINT FK4y4piwhfpfja5of6woxhjr77q FOREIGN KEY (cart_id) REFERENCES cart,
                          CONSTRAINT FKoj7ky1v8cf4ibkk0s7alikp52 FOREIGN KEY (user_id) REFERENCES users
);