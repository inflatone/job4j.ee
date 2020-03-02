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

INSERT INTO users(id, name, login, password, role, registered)
VALUES (1, 'User', 'user', 'password', 'USER', '2020-01-01 10:00:00'),
       (2, 'Admin', 'dealer', 'dealer', 'ADMIN', '2015-01-05 10:00:00');

INSERT INTO car (id, vendor_id, model, year, mileage, transmission_id, engine_id, body_id)
VALUES (100, 50, 'MAZDA6', 2015, 0, 10, 20, 30),
       (101, 51, 'M5 RWD', 2018, 150, 11, 21, 31),
       (102, 50, 'MAZDA3', 2018, 0, 10, 21, 30);

INSERT INTO post(id, car_id, title, message, price, user_id)
VALUES (150, 100, 'cell car asap', 'mazda 6 description', 300000, 1),
       (151, 101, 'nice car', 'bmw m5 description', null, 1),
       (152, 102, 'beauty babe', 'mazda 3 description', 550000, 2);