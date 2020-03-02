DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS seq;

CREATE SEQUENCE seq START WITH 100000;

CREATE table users
(
    id       INTEGER PRIMARY KEY DEFAULT nextval('seq'),
    login    VARCHAR                           NOT NULL,
    name     VARCHAR                           NOT NULL,
    password VARCHAR                           NOT NULL,
    created  TIMESTAMP           DEFAULT now() NOT NULL
);

CREATE UNIQUE INDEX users_unique_login_idx ON users (login);