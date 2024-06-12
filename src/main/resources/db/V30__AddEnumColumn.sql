CREATE TYPE category AS ENUM ('ЗАВТРАКИ', 'СУПЫ', 'ГОРЯЧИЕ БЛЮДА', 'ДЕСЕРТЫ', 'ЗАКУСКИ', 'НАПИТКИ');

ALTER TABLE Recipes
    ADD COLUMN categories VARCHAR(255);

UPDATE Recipes
SET categories = 'BREAKFAST'
WHERE categories IS NULL;

UPDATE Recipes
SET categories = 'DESSERT'
WHERE categories IS NULL;

UPDATE Recipes
SET categories = 'SOUPS'
WHERE categories IS NULL;

UPDATE Recipes
SET categories = 'HOT_DISHES'
WHERE categories IS NULL;

UPDATE Recipes
SET categories = 'SNACKS'
WHERE categories IS NULL;

UPDATE Recipes
SET categories = 'DRINKS'
WHERE categories IS NULL;

ALTER TABLE Recipes
    ALTER COLUMN categories SET NOT NULL;