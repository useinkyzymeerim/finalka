DO $$
DECLARE
max_id BIGINT;
BEGIN
SELECT MAX(id) INTO max_id FROM users;
PERFORM setval('users_seq', max_id + 1, false);
END $$;

DO $$
DECLARE
max_id BIGINT;
BEGIN
SELECT MAX(id) INTO max_id FROM menu;
PERFORM setval('menu_seq', max_id + 1, false);
END $$;

DO $$
DECLARE
max_id BIGINT;
BEGIN
SELECT MAX(id) INTO max_id FROM recipes;
PERFORM setval('recipes_seq', max_id + 1, false);
END $$;

DO $$
DECLARE
max_id BIGINT;
BEGIN
SELECT MAX(id) INTO max_id FROM products_for_recipes;
PERFORM setval('product_for_recipes_seq', max_id + 1, false);
END $$;

DO $$
DECLARE
max_id BIGINT;
BEGIN
SELECT MAX(id) INTO max_id FROM recipes_with_products;
PERFORM setval('recipes_with_products_seq', max_id + 1, false);
END $$;

DO $$
DECLARE
max_id BIGINT;
BEGIN
SELECT MAX(id) INTO max_id FROM product_of_shop;
PERFORM setval('product_of_shop_seq', max_id + 1, false);
END $$;
