CREATE DATABASE IF NOT EXISTS tech_challenge;

CREATE USER IF NOT EXISTS application_user IDENTIFIED BY 'SENHA123';

USE tech_challenge;

GRANT ALL PRIVILEGES ON tech_challenge.* TO 'application_user'@'%';