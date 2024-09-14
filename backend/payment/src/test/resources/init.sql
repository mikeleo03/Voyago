CREATE TABLE payment (
    id VARCHAR(36) PRIMARY KEY,
    nominal INT NOT NULL,
    picture VARCHAR(255),
    payment_date DATETIME,
    status VARCHAR(255),
    created_time DATETIME,
    updated_time DATETIME,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);