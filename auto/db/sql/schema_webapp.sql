CREATE TABLE users
(
    id         INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    name       VARCHAR   NOT NULL,
    login      VARCHAR   NOT NULL,
    password   VARCHAR   NOT NULL,
    registered TIMESTAMP NOT NULL  DEFAULT now(),
    role       VARCHAR   NOT NULL,
    enabled    BOOLEAN   NOT NULL  DEFAULT true
);

CREATE UNIQUE INDEX users_unique_login_idx ON users (login);

CREATE TABLE post
(
    id        INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    title     VARCHAR   NOT NULL,
    message   VARCHAR   NOT NULL,
    posted    TIMESTAMP NOT NULL  DEFAULT now(),
    price     INTEGER,
    car_id    INTEGER   REFERENCES car (id) ON DELETE SET NULL,
    user_id   INTEGER   NOT NULL REFERENCES users (id) ON DELETE CASCADE
);