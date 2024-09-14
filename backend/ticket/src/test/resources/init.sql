CREATE TABLE ticket (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    tour_id VARCHAR(36),
    payment_id VARCHAR(36),
    status VARCHAR(255),
    start_date DATE,
    end_date DATE,
    is_rescheduled BOOLEAN,
    is_reviewed BOOLEAN,
    created_time DATETIME,
    updated_time DATETIME,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE ticket_detail (
    id VARCHAR(36) PRIMARY KEY,
    ticket_id VARCHAR(36),
    name VARCHAR(255),
    phone VARCHAR(15),
    created_time DATETIME,
    updated_time DATETIME,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_ticket FOREIGN KEY (ticket_id) REFERENCES ticket(id) ON DELETE CASCADE
);
