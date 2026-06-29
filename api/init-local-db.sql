-- Execute this script once to set up local MySQL databases
-- Run: mysql -u root -p < init-local-db.sql

CREATE USER IF NOT EXISTS 'nidus'@'localhost' IDENTIFIED BY 'nidus123';
CREATE DATABASE IF NOT EXISTS nidus;
GRANT ALL PRIVILEGES ON nidus.* TO 'nidus'@'localhost';

CREATE USER IF NOT EXISTS 'nidus_test'@'localhost' IDENTIFIED BY 'nidus_test123';
CREATE DATABASE IF NOT EXISTS nidus_test;
GRANT ALL PRIVILEGES ON nidus_test.* TO 'nidus_test'@'localhost';

FLUSH PRIVILEGES;
