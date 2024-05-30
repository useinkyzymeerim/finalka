INSERT INTO users (id, email, name, surname, username, password, phone_number, last_authentication)
VALUES
    (1, 'user1@example.com', 'Иван', 'Иванов', 'ivan', 'Java-F23&', '+123456789', CURRENT_TIMESTAMP),
    (2, 'user2@example.com', 'Петр', 'Петров', 'petr', 'Java-F23&', '+987654321', CURRENT_TIMESTAMP),
    (3, 'chef1@example.com', 'Анна', 'Кулинарова', 'anna_chef', 'Java-F23&', '+111111111', CURRENT_TIMESTAMP),
    (4, 'chef2@example.com', 'Михаил', 'Поваров', 'mikhail_chef', 'Java-F23&', '+222222222', CURRENT_TIMESTAMP),
    (5, 'admin@example.com', 'Admin', 'Adminov', 'admin', 'Java-F23&', '+333333333', CURRENT_TIMESTAMP);