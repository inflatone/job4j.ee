ALTER TABLE users
    ADD COLUMN enabled BOOLEAN DEFAULT true;

UPDATE users SET enabled=true;