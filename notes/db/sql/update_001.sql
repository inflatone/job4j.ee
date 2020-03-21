CREATE SEQUENCE seq START WITH 100000;

CREATE TABLE items
(
    id          INTEGER PRIMARY KEY DEFAULT nextval('seq'),
    description VARCHAR                           NOT NULL,
    created     TIMESTAMP           DEFAULT now() NOT NULL,
    done        BOOLEAN             DEFAULT false NOT NULL
);