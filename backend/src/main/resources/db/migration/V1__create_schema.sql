-- Hotels shown in the portal
CREATE TABLE hotel (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(120)  NOT NULL,
    location    VARCHAR(80)   NOT NULL,
    address     VARCHAR(200)  NOT NULL,
    description VARCHAR(1000) NOT NULL
);

CREATE INDEX idx_hotel_location ON hotel (location);

-- One row per amenity, so amenities stay searchable and normalised
CREATE TABLE hotel_amenity (
    hotel_id BIGINT      NOT NULL,
    amenity  VARCHAR(60) NOT NULL,
    PRIMARY KEY (hotel_id, amenity),
    CONSTRAINT fk_amenity_hotel FOREIGN KEY (hotel_id) REFERENCES hotel (id)
);

-- Room category of a hotel. The assignment needs one category per hotel,
-- enforced here with a unique hotel_id (can be relaxed later for more categories).
CREATE TABLE room_type (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    hotel_id        BIGINT        NOT NULL,
    name            VARCHAR(80)   NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    total_rooms     INT           NOT NULL,
    max_guests      INT           NOT NULL,
    CONSTRAINT uq_room_type_hotel UNIQUE (hotel_id),
    CONSTRAINT fk_room_type_hotel FOREIGN KEY (hotel_id) REFERENCES hotel (id),
    CONSTRAINT chk_room_price CHECK (price_per_night > 0),
    CONSTRAINT chk_room_total CHECK (total_rooms > 0)
);

-- How many rooms are still free on each night.
-- A booking takes one room on every night from check-in up to (not including) check-out.
CREATE TABLE room_inventory (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_type_id    BIGINT NOT NULL,
    stay_date       DATE   NOT NULL,
    available_rooms INT    NOT NULL,
    CONSTRAINT uq_inventory_room_date UNIQUE (room_type_id, stay_date),
    CONSTRAINT fk_inventory_room_type FOREIGN KEY (room_type_id) REFERENCES room_type (id),
    CONSTRAINT chk_inventory_not_negative CHECK (available_rooms >= 0)
);

CREATE TABLE booking (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_reference VARCHAR(20)   NOT NULL,
    room_type_id      BIGINT        NOT NULL,
    guest_name        VARCHAR(100)  NOT NULL,
    guest_email       VARCHAR(150)  NOT NULL,
    check_in          DATE          NOT NULL,
    check_out         DATE          NOT NULL,
    nights            INT           NOT NULL,
    price_per_night   DECIMAL(10,2) NOT NULL,
    total_amount      DECIMAL(12,2) NOT NULL,
    status            VARCHAR(20)   NOT NULL,
    created_at        DATETIME      NOT NULL,
    cancelled_at      DATETIME      NULL,
    CONSTRAINT uq_booking_reference UNIQUE (booking_reference),
    CONSTRAINT fk_booking_room_type FOREIGN KEY (room_type_id) REFERENCES room_type (id),
    CONSTRAINT chk_booking_dates CHECK (check_out > check_in),
    CONSTRAINT chk_booking_status CHECK (status IN ('CONFIRMED', 'CANCELLED'))
);

CREATE INDEX idx_booking_room_dates ON booking (room_type_id, check_in, check_out);
