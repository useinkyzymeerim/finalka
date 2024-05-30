DROP TABLE IF EXISTS menu_recipe CASCADE;

CREATE TABLE menu_recipe (
                             menu_id BIGINT NOT NULL,
                             recipe_id BIGINT NOT NULL,
                             CONSTRAINT FK95yoskw6d1y8kwdr78hovrd0l FOREIGN KEY (recipe_id) REFERENCES recipes,
                             CONSTRAINT FKl0ta3l204rrpbc868rs1bbp3a FOREIGN KEY (menu_id) REFERENCES menu
);