CREATE TABLE support_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NULL,
    ticket_ref VARCHAR(30) NOT NULL UNIQUE,
    category VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    subject VARCHAR(120) NOT NULL,
    message TEXT NOT NULL,
    booking_ref VARCHAR(25) NULL,
    contact_name VARCHAR(100) NOT NULL,
    contact_email VARCHAR(150) NOT NULL,
    contact_phone VARCHAR(15) NULL,
    admin_reply TEXT NULL,
    replied_at DATETIME NULL,
    reply_email_sent BIT NULL,
    reply_email_message VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_support_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_support_requests_user_id ON support_requests(user_id);
CREATE INDEX idx_support_requests_status ON support_requests(status);
CREATE INDEX idx_support_requests_booking_ref ON support_requests(booking_ref);
