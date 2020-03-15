CREATE SEQUENCE common_seq START WITH 100000;

CREATE TABLE users
(
    id         INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    login      VARCHAR   NOT NULL,
    password   VARCHAR   NOT NULL,
    role       VARCHAR   NOT NULL,
    registered TIMESTAMP NOT NULL  DEFAULT now()
);

CREATE UNIQUE INDEX users_unique_login_idx ON users (login);

INSERT INTO users (login, password, role)
VALUES ('user', 'password', 'USER'),
       ('admin', 'admin', 'ADMIN');