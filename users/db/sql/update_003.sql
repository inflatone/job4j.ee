ALTER TABLE users
    ADD COLUMN role VARCHAR;

UPDATE users SET role='USER';

INSERT INTO users (login, password, name, role) VALUES ('root', 'root', 'root', 'ADMIN');