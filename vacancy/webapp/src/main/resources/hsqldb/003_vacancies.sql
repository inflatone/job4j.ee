CREATE TABLE vacancy
(
    id          INTEGER GENERATED BY DEFAULT AS SEQUENCE common_seq PRIMARY KEY,
    task_id     INTEGER               NOT NULL REFERENCES task (id) ON DELETE CASCADE,
    highlighted BOOLEAN DEFAULT false NOT NULL,
    title       VARCHAR(255)          NOT NULL,
    description TEXT               NOT NULL,
    url         VARCHAR(255)          NOT NULL,
    date        TIMESTAMP             NOT NULL
);

CREATE UNIQUE INDEX vacancy_unique_title_task_idx ON vacancy (title, task_id);

CREATE TABLE launch_log
(
    id           INTEGER GENERATED BY DEFAULT AS SEQUENCE common_seq PRIMARY KEY,
    task_id      INTEGER     NOT NULL REFERENCES task (id) ON DELETE CASCADE,
    status       VARCHAR(10) NOT NULL,
    date_time    TIMESTAMP   NOT NULL,
    found_amount INTEGER     NOT NULL,
    added_amount INTEGER     NOT NULL
);