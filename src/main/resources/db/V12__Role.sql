DROP TABLE IF EXISTS role CASCADE;

CREATE SEQUENCE role_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE role (
                      id BIGINT NOT NULL DEFAULT nextval('role_seq'),
                      name VARCHAR(255),
                      PRIMARY KEY (id)
);