CREATE TABLE vacancy
(
    id          INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    task_id     INTEGER   NOT NULL REFERENCES task (id) ON DELETE CASCADE,
    highlighted BOOLEAN   NOT NULL  DEFAULT false,
    title       VARCHAR   NOT NULL,
    description VARCHAR   NOT NULL,
    url         VARCHAR   NOT NULL,
    date        TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX vacancy_unique_title_task_idx ON vacancy (title, task_id);

CREATE TABLE launch_log
(
    id           INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    task_id      INTEGER     NOT NULL REFERENCES task (id) ON DELETE CASCADE,
    status       VARCHAR(10) NOT NULL,
    date_time    TIMESTAMP   NOT NULL,
    found_amount INTEGER     NOT NULL,
    added_amount INTEGER     NOT NULL
);