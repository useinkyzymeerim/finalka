DROP TABLE IF EXISTS review CASCADE;

CREATE TABLE review (
                        id BIGSERIAL NOT NULL,
                        rating INTEGER NOT NULL,
                        created_at TIMESTAMP(6),
                        updated_at TIMESTAMP(6),
                        recipe_id BIGINT,
                        user_id BIGINT,
                        comment VARCHAR(255),
                        PRIMARY KEY (id),
                        CONSTRAINT FK4tkew8gxy1pe0xi0xhd9gs37k FOREIGN KEY (recipe_id) REFERENCES recipes,
                        CONSTRAINT FK6cpw2nlklblpvc7hyt7ko6v3e FOREIGN KEY (user_id) REFERENCES users
);