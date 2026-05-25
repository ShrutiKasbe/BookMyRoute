CREATE TABLE IF NOT EXISTS route_reviews (
    review_id BIGINT NOT NULL AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    route_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment VARCHAR(1000),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (review_id),
    CONSTRAINT uk_route_review_booking UNIQUE (booking_id),
    CONSTRAINT fk_review_booking FOREIGN KEY (booking_id) REFERENCES bookings (id),
    CONSTRAINT fk_review_route FOREIGN KEY (route_id) REFERENCES routes (id),
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT chk_route_review_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE INDEX idx_route_reviews_route_created ON route_reviews (route_id, created_at);
CREATE INDEX idx_route_reviews_user ON route_reviews (user_id);
