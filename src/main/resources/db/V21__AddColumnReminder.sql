ALTER TABLE reminder (
    ADD hour INTEGER,
    ADD minute INTEGER,
    ADD created_by VARCHAR (255),
    ADD created_at TIMESTAMP (6),
    ADD last_updated_by VARCHAR (255),
    ADD last_updated_at TIMESTAMP (6),
    ADD deleted_by VARCHAR (255),
    ADD deleted_at TIMESTAMP (6);
);