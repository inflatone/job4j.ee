CREATE TABLE country
(
    id   INTEGER PRIMARY KEY DEFAULT nextval('seq'),
    name VARCHAR NOT NULL
);

CREATE table city
(
    id         INTEGER PRIMARY KEY DEFAULT nextval('seq'),
    name       VARCHAR NOT NULL,
    country_id INTEGER NOT NULL REFERENCES country (id)
);

CREATE UNIQUE INDEX cities_unique_name_country_idx ON city (name, country_id);

CREATE UNIQUE INDEX country_unique_name_idx ON country (name);

ALTER TABLE users
    ADD COLUMN city_id INTEGER REFERENCES city (id);