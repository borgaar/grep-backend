DROP TABLE IF EXISTS bookmarks;
DROP TABLE IF EXISTS listings;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    phone         VARCHAR(20) PRIMARY KEY NOT NULL,
    password_hash TEXT                    NOT NULL,
    first_name    TEXT                    NOT NULL,
    last_name     TEXT                    NOT NULL,
    role          VARCHAR(20)             NOT NULL DEFAULT 'user'
);

CREATE TABLE categories
(
    name VARCHAR(255) NOT NULL PRIMARY KEY
);

CREATE TABLE listings
(
    id          CHAR(36) PRIMARY KEY NOT NULL,
    author      VARCHAR(20)          NOT NULL REFERENCES users (phone) ON DELETE CASCADE,
    category    VARCHAR(255)         NOT NULL REFERENCES categories (name) ON UPDATE CASCADE,
    title       TEXT                 NOT NULL,
    description TEXT                 NOT NULL,
    price       INT                  NOT NULL,
    created_at  TIMESTAMP            NOT NULL DEFAULT NOW(),
    lat         DOUBLE               NOT NULL,
    lon         DOUBLE               NOT NULL,
    reserved_by VARCHAR(20)          NULL REFERENCES users (phone) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE messages
(
    id           CHAR(36)    NOT NULL PRIMARY KEY,
    sender_id    VARCHAR(20) NOT NULL REFERENCES users (phone) ON UPDATE CASCADE,
    recipient_id VARCHAR(20) NOT NULL REFERENCES users (phone) ON UPDATE CASCADE,
    content      TEXT        NOT NULL,
    timestamp    TIMESTAMP   NOT NULL DEFAULT NOW(),
    type  ENUM('text', 'reserved', 'marked-sold') NOT NULL DEFAULT('text')
);

CREATE TABLE bookmarks
(
    user_id    VARCHAR(20) NOT NULL REFERENCES users (phone) ON UPDATE CASCADE,
    listing_id CHAR(36)    NOT NULL REFERENCES listings (id) ON UPDATE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, listing_id)
);

-- Password is 123
INSERT INTO users (phone, password_hash, first_name, last_name, role)
    VALUE ('99999999', '$2a$10$BbjEkDurDk31upVmMjM.A.JFIT57TQFQr6m./a9tk9ftjAu2rNMay', 'Brotherman', 'Testern', 'admin');

-- Password is 456
INSERT INTO users (phone, password_hash, first_name, last_name)
    VALUES ('12345678', '$2a$10$yL3lpULoLJwKexGJNa1Rte1a7SryalnNN2oQwD0PjcGkWOqg5Q84e', 'Brur', 'Testingston'),
           ('00000000', '000', 'Roger', 'Rogersen');

INSERT INTO categories (name)
    VALUE ('Ting og tang');

INSERT INTO listings (id, author, category, title, description, price, lat, lon)
    VALUE ('084846ae-3ddc-4ae8-8d8c-1ed7c4ab1922',
           '99999999',
           'Ting og tang',
           'Testsalg',
           'En veldig betydelig beskrivelse.',
           42,
           71.00,
           71.00);

INSERT INTO listings (id, author, category, title, description, price, lat, lon)
    VALUE ('084846ae-3ddc-4ae8-8d8c-1ed7c4ab1925',
           '12345678',
           'Ting og tang',
           'Ost',
           'Mmmm, veldig ost.',
           500,
           71.00,
           71.00);

INSERT INTO bookmarks (user_id, listing_id)
    VALUE (99999999, '084846ae-3ddc-4ae8-8d8c-1ed7c4ab1922');

INSERT INTO messages (id, sender_id, recipient_id, content, timestamp)
    VALUES (UUID(), '99999999', '12345678', 'halla', '1999-02-02 14:00'),
           (UUID(), '99999999', '12345678', 'halla igjen', '1999-02-02 15:00'),
           (UUID(), '12345678', '99999999', '...', '1999-02-02 16:00'),
           (UUID(), '99999999', '12345678', '.....', '1999-02-02 18:00'),
           (UUID(), '00000000', '99999999', 'roger', '1999-02-02 17:00');
