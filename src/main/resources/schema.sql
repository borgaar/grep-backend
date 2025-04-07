DROP TABLE IF EXISTS listings;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    phone         VARCHAR(20) PRIMARY KEY NOT NULL,
    password_hash TEXT                    NOT NULL,
    first_name    TEXT                    NOT NULL,
    last_name     TEXT                    NOT NULL
);

CREATE TABLE listings
(
    id          CHAR(36) PRIMARY KEY NOT NULL,
    author      VARCHAR(20)          NOT NULL REFERENCES users (phone) ON DELETE CASCADE,
    category    TEXT                 NOT NULL, -- add PK to categories
    title       TEXT                 NOT NULL,
    description TEXT                 NOT NULL,
    price       INT                  NOT NULL,
    lat         DOUBLE               NOT NULL,
    lon         DOUBLE               NOT NULL
);

-- Password is 123
INSERT INTO users (phone, password_hash, first_name, last_name)
SELECT '99999999', '$2a$10$BbjEkDurDk31upVmMjM.A.JFIT57TQFQr6m./a9tk9ftjAu2rNMay', 'Brotherman', 'Testern';

INSERT INTO listings (id, author, category, title, description, price, lat, lon)
SELECT '084846ae-3ddc-4ae8-8d8c-1ed7c4ab1922',
       '99999999',
       'Ting',
       'Testsalg',
       'En veldig betydelig beskrivelse.',
       42,
       71.00,
       71.00;