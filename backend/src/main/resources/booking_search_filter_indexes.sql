-- Optional performance indexes for the My Bookings search/filter API.
-- Run this once on MySQL databases that already contain the BookMyRoute schema.

CREATE INDEX idx_bookings_user_status_booked_at
    ON bookings (user_id, status, booked_at);

CREATE INDEX idx_bookings_user_booked_at
    ON bookings (user_id, booked_at);
