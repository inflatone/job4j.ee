DELETE FROM post;
DELETE FROM users;

DELETE FROM car;

DELETE FROM transmission;
DELETE FROM engine;
DELETE FROM body;
DELETE FROM vendor;

ALTER SEQUENCE common_seq RESTART WITH 100000;

INSERT INTO transmission (id, type)
VALUES (10, 'automatic'),
       (11, 'manual');

INSERT INTO engine (id, type)
VALUES (20, 'Diesel'),
       (21, 'Electric');

INSERT INTO body (id, type)
VALUES (30, 'Sedan'),
       (31, 'Coupe');

INSERT INTO vendor(id, name, country)
VALUES (50, 'Mazda', 'Japan'),
       (51, 'BMW', 'Germany');