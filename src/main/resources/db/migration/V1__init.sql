CREATE TABLE events (
                        id UUID PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        date TIMESTAMP NOT NULL
);

CREATE TABLE seats (
                       id UUID PRIMARY KEY,
                       event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
                       row VARCHAR(10),
                       number VARCHAR(10),
                       status VARCHAR(10) CHECK (status IN ('FREE', 'BOOKED', 'PAID')) DEFAULT 'FREE',
                       price NUMERIC(10,2) NOT NULL
);

CREATE TABLE bookings (
                          id UUID PRIMARY KEY,
                          user_id UUID NOT NULL,
                          seat_id UUID NOT NULL REFERENCES seats(id),
                          status VARCHAR(20) CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED')),
                          expires_at TIMESTAMP,
                          created_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_booking_seat ON bookings(seat_id);