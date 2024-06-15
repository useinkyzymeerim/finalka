CREATE SEQUENCE favorite_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE favorite (
                          id BIGINT NOT NULL,
                          added_at TIMESTAMP(6),
                          menu_id BIGINT,
                          user_id BIGINT,
                          PRIMARY KEY (id),
                          CONSTRAINT FK_favorite_menu FOREIGN KEY (menu_id) REFERENCES menu(id),
                          CONSTRAINT FK_favorite_user FOREIGN KEY (user_id) REFERENCES users(id)
);