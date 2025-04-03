DROP TABLE IF EXISTS listings;
DROP TABLE IF EXISTS users;
SET sql_mode = 'STRICT_TRANS_TABLES';

CREATE TABLE users
(
    phone         VARCHAR(20)  PRIMARY KEY,
    password_hash TEXT        ,
    first_name    TEXT        ,
    last_name     TEXT
);

CREATE TABLE listings
(
    id CHAR(36) PRIMARY KEY,
    author VARCHAR(20) REFERENCES users (phone),
    category TEXT, -- add PK to categories
    title TEXT,
    description TEXT,
    price INT,
    lat DOUBLE,
    lon DOUBLE
);

-- Password is 123
INSERT INTO users (phone, password_hash, first_name, last_name)
SELECT '99999999', '$2a$10$BbjEkDurDk31upVmMjM.A.JFIT57TQFQr6m./a9tk9ftjAu2rNMay', 'Brotherman', 'Testern';

INSERT INTO listings (id, author, category, title, description, price, lat, lon)
SELECT '084846ae-3ddc-4ae8-8d8c-1ed7c4ab1922', '99999999', 'Ting', 'Testsalg', 'En veldig betydelig beskrivelse.', 42, 71.00, 71.00;