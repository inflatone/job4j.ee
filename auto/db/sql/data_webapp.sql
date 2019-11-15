INSERT INTO users(id, name, login, password, role)
VALUES (1, 'User', 'user', 'password', 'USER'),
       (2, 'Admin', 'dealer', 'dealer', 'ADMIN');

INSERT INTO car (id, vendor_id, model, year, mileage, transmission_id, engine_id, body_id)
VALUES (100, 50, 'MAZDA6', 2015, 0, 10, 20, 33),
       (101, 51, 'M5 RWD', 2018, 150, 11, 22, 33),
       (102, 52, 'Civic LX', 2019, 0, 11, 20, 34),
       (103, 50, 'MAZDA3', 2018, 0, 10, 23, 30),
       (104, 53, 'Taurus', 2014, 0, 11, 25, 31);

INSERT INTO post(id, car_id, title, message, price, user_id)
VALUES (150, 100, 'cell car asap', 'mazda description', 300000, 1),
       (151, 101, 'nice car', 'bmw m5 description', null, 1),
       (152, 102, 'beauty babe', 'civic description', 550000, 2),
       (153, 103, 'new auto', 'mazda 3 desc', 400000, 2),
       (154, 104, 'hell one', 'taurus nice', null, 2);

