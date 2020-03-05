DROP TABLE IF EXISTS images;

CREATE table images
(
    id           INTEGER PRIMARY KEY DEFAULT nextval('seq'),
    name         VARCHAR    NOT NULL,
    content_type VARCHAR    NOT NULL,
    file_size    BIGINT     NOT NULL,
    oid          OID UNIQUE NOT NULL
);

ALTER TABLE users
    ADD COLUMN image_id INTEGER REFERENCES images (id);