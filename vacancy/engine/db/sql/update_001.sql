CREATE SEQUENCE common_seq START WITH 100000;

CREATE TABLE log
(
    date   TIMESTAMP NOT NULL UNIQUE,
    amount INTEGER   NOT NULL
);

CREATE TABLE vacancy_data
(
    id          INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    title       VARCHAR   NOT NULL,
    description VARCHAR   NOT NULL,
    link        VARCHAR   NOT NULL,
    date        TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX vacancy_data_unique_title_idx ON vacancy_data (title);