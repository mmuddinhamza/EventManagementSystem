CREATE DATABASE eventdb;

USE eventdb;

CREATE TABLE event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    time VARCHAR(10) NOT NULL,
    duration VARCHAR(10) NOT NULL,
    date DATE NOT NULL
);
