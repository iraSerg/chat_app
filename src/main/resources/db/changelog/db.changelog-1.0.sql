--liquibase formatted sql

--changeset irina:1
create schema if not exists user_chat_app;
CREATE TABLE user_chat_app.users (
                                     username VARCHAR(64) PRIMARY KEY,
                                     name VARCHAR(64) UNIQUE NOT NULL CHECK ( length(trim(name))>0 ),
                                     email VARCHAR(64) UNIQUE NOT NULL CHECK ( length(trim(username))>0 ),
                                     password VARCHAR(64) CHECK ( length(trim(password))>0 ),
                                     image VARCHAR(128)

);


CREATE TABLE user_chat_app.roles (
                                     role_id SERIAL PRIMARY KEY,
                                     name VARCHAR(64) NOT NULL
);

CREATE TABLE user_chat_app.users_roles (
                                           user_id TEXT REFERENCES user_chat_app.users(username) ON DELETE CASCADE,
                                           role_id INT REFERENCES user_chat_app.roles(role_id) ON DELETE CASCADE,
                                           PRIMARY KEY (user_id, role_id)
)
--changeset irina:2
INSERT INTO user_chat_app.roles  VALUES (1,'ROLE_ADMIN');
INSERT INTO user_chat_app.roles  VALUES (2,'ROLE_USER');
INSERT INTO user_chat_app.users VALUES ('admin',  'Irina','chat@gmail.com', '$2a$10$f9/t9Zuk9.u9vT2yLz6L4uKkG5Vd5n7j0p8x7x8z8n7y7D7z7v');
INSERT INTO user_chat_app.users_roles VALUES ('admin', 1);