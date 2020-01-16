CREATE TABLE scan_source
(
    id       INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    code     VARCHAR NOT NULL,
    url      VARCHAR NOT NULL,
    icon_url VARCHAR NOT NULL
);

INSERT INTO scan_source(code, url, icon_url)
VALUES ('hh_ru', 'https://hh.ru/', 'https://hh.ru/favicon.ico'),
       ('habr_com', 'https://career.habr.com/', 'https://career.habr.com/images/favicons/favicon-16.png'),
       ('sql_ru', 'https://www.sql.ru/', 'https://www.sql.ru/favicon.ico');

CREATE TABLE task
(
    id             INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    active         BOOLEAN   NOT NULL  DEFAULT true,
    amount         INTEGER   NOT NULL  DEFAULT 0,
    keyword        VARCHAR   NOT NULL,
    city           VARCHAR,
    scan_limit     TIMESTAMP NOT NULL,
    next_launch    TIMESTAMP,
    repeat_rule    VARCHAR   NOT NULL,
    scan_source_id INTEGER   NOT NULL REFERENCES scan_source (id),
    user_id        INTEGER   NOT NULL REFERENCES users (id) ON DELETE CASCADE
);