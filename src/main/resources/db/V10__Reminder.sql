DROP TABLE IF EXISTS reminder CASCADE;

CREATE SEQUENCE reminder_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE reminder (
                          id BIGINT NOT NULL DEFAULT nextval('reminder_seq'),
                          reminder_time TIMESTAMP(6),
                          user_id BIGINT,
                          email VARCHAR(255),
                          message VARCHAR(255),
                          PRIMARY KEY (id)
);