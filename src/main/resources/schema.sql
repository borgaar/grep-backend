DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    phone         VARCHAR(20) NOT NULL PRIMARY KEY,
    password_hash TEXT        NOT NULL,
    first_name    TEXT        NOT NULL,
    last_name     TEXT        NOT NULL
)