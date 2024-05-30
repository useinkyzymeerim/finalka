DROP TABLE IF EXISTS recipes CASCADE;

CREATE SEQUENCE recipes_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE recipes (
                         id BIGINT NOT NULL DEFAULT nextval('recipes_seq'),
                         cooking_time INTEGER,
                         quantity_of_product INTEGER,
                         created_at TIMESTAMP(6),
                         deleted_at TIMESTAMP(6),
                         last_updated_at TIMESTAMP(6),
                         user_id BIGINT,
                         created_by VARCHAR(255),
                         deleted_by VARCHAR(255),
                         description VARCHAR(255),
                         last_updated_by VARCHAR(255),
                         link_of_video VARCHAR(255),
                         name_of_food VARCHAR(255),
                         image_base64 OID,
                         PRIMARY KEY (id),
                         CONSTRAINT FKlc3x6yty3xsupx80hqbj9ayos FOREIGN KEY (user_id) REFERENCES users
);