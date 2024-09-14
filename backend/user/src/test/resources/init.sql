CREATE TABLE user (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    picture VARCHAR(255),
    phone VARCHAR(15),
    role VARCHAR(255),
    status VARCHAR(255),
    created_time DATETIME,
    updated_time DATETIME,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
