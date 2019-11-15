INSERT INTO users(id, name, login, password, role)
VALUES (1, 'User', 'user', 'password', 'USER'),
       (2, 'Admin', 'dealer', 'dealer', 'ADMIN');

INSERT INTO car (id, vendor_id, model, year, mileage, transmission_id, engine_id, body_id)
VALUES (100, 50, 'MAZDA6', 2015, 0, 10, 20, 30),
       (101, 51, 'M5 RWD', 2018, 150, 11, 21, 31),
       (102, 50, 'MAZDA3', 2018, 0, 10, 21, 30);

INSERT INTO post(id, car_id, title, message, price, user_id)
VALUES (150, 100, 'cell car asap', 'mazda 6 description', 300000, 1),
       (151, 101, 'nice car', 'bmw m5 description', null, 1),
       (152, 102, 'beauty babe', 'mazda 3 description', 550000, 2);