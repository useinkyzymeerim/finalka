DROP TABLE IF EXISTS users_roles CASCADE;

CREATE TABLE users_roles (
                             roles_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             PRIMARY KEY (roles_id, user_id),
                             CONSTRAINT FK15d410tj6juko0sq9k4km60xq FOREIGN KEY (roles_id) REFERENCES role,
                             CONSTRAINT FK2o0jvgh89lemvvo17cbqvdxaa FOREIGN KEY (user_id) REFERENCES users
);