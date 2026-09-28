-- NextNest database schema
-- Rebuilt from the queries used in the Java code.
-- Run in MySQL Workbench, or: mysql -u root -p < database/schema.sql
-- Note: the code uses both `users` and `Users`. MySQL on Windows ignores the
-- difference; on Linux/macOS set lower_case_table_names=1.

CREATE DATABASE IF NOT EXISTS nextnest CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE nextnest;

CREATE TABLE IF NOT EXISTS users (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    mobile_number       VARCHAR(20)  NOT NULL UNIQUE,
    gender              VARCHAR(20),
    email               VARCHAR(150),
    address             VARCHAR(255),
    nid                 VARCHAR(30),
    password            VARCHAR(255) NOT NULL,
    profile_image_path  VARCHAR(255),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Posts (
    post_id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT NOT NULL,
    title          VARCHAR(200) NOT NULL,
    description    TEXT,
    price          DECIMAL(12,2),
    house_no       VARCHAR(50),
    phone_number   VARCHAR(20),
    email          VARCHAR(150),
    location       VARCHAR(255),
    sale_or_rent   VARCHAR(20),
    negotiable     VARCHAR(20),
    image_path     VARCHAR(255),
    template_path  VARCHAR(255),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Reactions (
    reaction_id    INT AUTO_INCREMENT PRIMARY KEY,
    post_id        INT NOT NULL,
    user_id        INT NOT NULL,
    reaction_type  ENUM('like','dislike') NOT NULL,
    UNIQUE KEY one_reaction_per_user (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES Posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Comments (
    comment_id    INT AUTO_INCREMENT PRIMARY KEY,
    post_id       INT NOT NULL,
    user_id       INT NOT NULL,
    comment_text  TEXT NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES Posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
