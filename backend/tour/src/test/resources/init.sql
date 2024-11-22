DROP TABLE IF EXISTS tour;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS facility;
CREATE TABLE tour (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255),
    detail TEXT,
    quota INT,
    prices INT,
    location VARCHAR(255),
    image VARCHAR(255),
    status VARCHAR(255),
    created_time DATETIME,
    updated_time DATETIME,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE review (
    id VARCHAR(36) PRIMARY KEY,
    tour_id VARCHAR(36),
    user_id VARCHAR(36),
    review TEXT,
    rating INT,
    created_time DATETIME,
    updated_time DATETIME,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_tour_review FOREIGN KEY (tour_id) REFERENCES tour(id) ON DELETE CASCADE
);

CREATE TABLE facility (
    id VARCHAR(36) PRIMARY KEY,
    tour_id VARCHAR(36),
    name VARCHAR(255),
    created_time DATETIME,
    updated_time DATETIME,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_tour_facility FOREIGN KEY (tour_id) REFERENCES tour(id) ON DELETE CASCADE
);
