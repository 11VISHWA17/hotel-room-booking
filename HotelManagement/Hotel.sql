CREATE DATABASE hotel;

USE hotel;

CREATE TABLE rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_type VARCHAR(50),
    is_booked BOOLEAN DEFAULT FALSE
);

CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100),
    room_id INT,
    check_in DATE,
    check_out DATE,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

-- Sample Data
INSERT INTO rooms (room_type, is_booked) VALUES ('Single', FALSE), ('Double', FALSE), ('Suite', FALSE);
