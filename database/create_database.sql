-- Run once as the MySQL root user (MySQL 8.0.16 or newer).
-- It creates an empty database and an application user.
-- Tables and sample data are created automatically by Flyway when the backend starts.

CREATE DATABASE IF NOT EXISTS hotel_booking;

-- Replace the password below with your own, and put the same one in backend/.env
CREATE USER IF NOT EXISTS 'hotel_app'@'localhost' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON hotel_booking.* TO 'hotel_app'@'localhost';
FLUSH PRIVILEGES;
