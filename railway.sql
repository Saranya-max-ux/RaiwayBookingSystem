CREATE DATABASE IF NOT EXISTS railway;
USE railway;

DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS trains;

CREATE TABLE IF NOT EXISTS trains (
    train_id INT PRIMARY KEY,
    train_name VARCHAR(100),
    total_seats INT
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    passenger_name VARCHAR(100),
    train_id INT,
    FOREIGN KEY (train_id) REFERENCES trains(train_id)
);

-- Sample trains with seat counts
INSERT INTO trains VALUES 
(1, 'Express A', 30), 
(2, 'Express B', 30), 
(3, 'Superfast C', 30);
