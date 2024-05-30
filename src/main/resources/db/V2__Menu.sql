DROP TABLE IF EXISTS menu CASCADE;

CREATE SEQUENCE menu_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE menu (
                      id BIGINT NOT NULL DEFAULT nextval('menu_seq'),
                      created_at TIMESTAMP(6),
                      deleted_at TIMESTAMP(6),
                      last_updated_at TIMESTAMP(6),
                      created_by VARCHAR(255),
                      deleted_by VARCHAR(255),
                      last_updated_by VARCHAR(255),
                      name_of_menu VARCHAR(255),
                      PRIMARY KEY (id)
);