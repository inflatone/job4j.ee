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