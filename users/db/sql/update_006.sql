INSERT INTO country (name) VALUES ('Russian Federation'), ('Ukraine'), ('Australia');

INSERT INTO city (name, country_id)
VALUES ('Moscow', (SELECT id FROM country WHERE name='Russian Federation')),
       ('Saint Petersburg', (SELECT id FROM country WHERE name='Russian Federation')),
       ('Kiev', (SELECT id FROM country WHERE name='Ukraine')),
       ('Sydney', (SELECT id FROM country WHERE name='Australia'));

UPDATE users SET city_id = (SELECT c.id FROM city c WHERE name='Moscow')