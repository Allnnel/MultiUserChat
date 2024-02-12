DROP TABLE service.users CASCADE;
DROP TABLE service.chat CASCADE;
DROP TABLE service.message CASCADE;
DROP SCHEMA IF EXISTS service;
CREATE SCHEMA IF NOT EXISTS service;
CREATE TABLE IF NOT EXISTS service.users (
    id SERIAL PRIMARY KEY,
    email TEXT NOT NULL,
    password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS service.chat (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  ownerID INTEGER REFERENCES service.users(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS service.message (
  id SERIAL PRIMARY KEY,
  authorID INTEGER REFERENCES service.users(id) NOT NULL,
  roomID INTEGER REFERENCES service.chat(id) NOT NULL,
  textMessage TEXT NOT NULL,
  time TIMESTAMP NOT NULL
);