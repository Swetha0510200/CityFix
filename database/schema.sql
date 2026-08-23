-- ============================================================================
-- CityFix – Smart Civic Issue Reporting System
-- Database Schema Script (MySQL 8.x)
-- ============================================================================

CREATE DATABASE IF NOT EXISTS `cityfix` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `cityfix`;

-- 1. Users Table (Citizens)
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `phone` VARCHAR(20) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'CITIZEN',
    `created_at` DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Complaints Table
CREATE TABLE IF NOT EXISTS `complaints` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `complaint_id` VARCHAR(30) NOT NULL UNIQUE,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `category` VARCHAR(50) NOT NULL,
    `description` TEXT NOT NULL,
    `location` VARCHAR(255) NOT NULL,
    `latitude` DOUBLE NULL,
    `longitude` DOUBLE NULL,
    `image_filename` VARCHAR(255) NULL,
    `priority` VARCHAR(20) NOT NULL DEFAULT 'Medium',
    `status` VARCHAR(30) NOT NULL DEFAULT 'Pending',
    `admin_remarks` TEXT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NULL,
    CONSTRAINT `fk_complaints_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Admins Table
CREATE TABLE IF NOT EXISTS `admins` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `created_at` DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
