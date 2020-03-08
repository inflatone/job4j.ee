DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS users;

DROP TABLE IF EXISTS image;
DROP TABLE IF EXISTS car;

DROP TABLE IF EXISTS vendor;
DROP TABLE IF EXISTS body;
DROP TABLE IF EXISTS engine;
DROP TABLE IF EXISTS transmission;

DROP SEQUENCE IF EXISTS common_seq;

CREATE SEQUENCE common_seq START WITH 100000;

CREATE TABLE body
(
    id   INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    type VARCHAR NOT NULL
);

CREATE TABLE engine
(
    id   INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    type VARCHAR NOT NULL
);

CREATE TABLE transmission
(
    id   INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    type VARCHAR NOT NULL
);

CREATE TABLE vendor
(
    id        INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    name      VARCHAR NOT NULL,
    country   VARCHAR NOT NULL,
    logo_link VARCHAR
);

CREATE UNIQUE INDEX body_unique_type_idx ON body (type);

CREATE UNIQUE INDEX engine_unique_type_idx ON engine (type);

CREATE UNIQUE INDEX transmission_unique_type_idx ON transmission (type);

CREATE UNIQUE INDEX vendor_unique_name_idx ON vendor (name);

CREATE TABLE image
(
    id           INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    file_name    VARCHAR(255) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    data         OID          NOT NULL
);

CREATE TABLE car
(
    id              INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    vendor_id       INTEGER NOT NULL REFERENCES vendor (id),
    model           VARCHAR NOT NULL,
    year            INTEGER NOT NULL,
    mileage         INTEGER NOT NULL    DEFAULT 0,
    transmission_id INTEGER NOT NULL REFERENCES transmission (id),
    engine_id       INTEGER NOT NULL REFERENCES engine (id),
    body_id         INTEGER NOT NULL REFERENCES body (id)
);

CREATE TABLE users
(
    id         INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    name       VARCHAR   NOT NULL,
    login      VARCHAR   NOT NULL,
    password   VARCHAR   NOT NULL,
    registered TIMESTAMP NOT NULL  DEFAULT now(),
    role       VARCHAR   NOT NULL,
    enabled    BOOLEAN   NOT NULL  DEFAULT true,
    image_id   INTEGER REFERENCES image (id)
);

CREATE UNIQUE INDEX users_unique_login_idx ON users (login);

CREATE TABLE post
(
    id        INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    title     VARCHAR   NOT NULL,
    message   VARCHAR,
    posted    TIMESTAMP NOT NULL  DEFAULT now(),
    price     INTEGER,
    completed BOOLEAN   NOT NULL  DEFAULT false,
    car_id    INTEGER   REFERENCES car (id) ON DELETE SET NULL,
    image_id  INTEGER REFERENCES image (id),
    user_id   INTEGER   NOT NULL REFERENCES users (id) ON DELETE CASCADE
);