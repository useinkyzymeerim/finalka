DROP TABLE IF EXISTS users CASCADE;

CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE users (
                       id BIGINT NOT NULL DEFAULT nextval('users_seq'),
                       last_authentication TIMESTAMP(6),
                       email VARCHAR(255),
                       name VARCHAR(255),
                       password VARCHAR(255),
                       phone_number VARCHAR(255),
                       surname VARCHAR(255),
                       username VARCHAR(255),
                       PRIMARY KEY (id)
);