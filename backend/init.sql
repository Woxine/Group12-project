CREATE DATABASE IF NOT EXISTS scooter_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE scooter_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS feedbacks;
DROP TABLE IF EXISTS discount_verification_submissions;
DROP TABLE IF EXISTS billing_settings_logs;
DROP TABLE IF EXISTS billing_settings;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS scooters;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS location_points;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  role VARCHAR(20) DEFAULT 'CUSTOMER',
  is_student TINYINT(1) DEFAULT 0,
  age INT DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email)
);

CREATE TABLE location_points (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  lat DOUBLE DEFAULT NULL,
  lng DOUBLE DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE scooters (
  id BIGINT NOT NULL AUTO_INCREMENT,
  status VARCHAR(255) NOT NULL,
  location_lat DOUBLE DEFAULT NULL,
  location_lng DOUBLE DEFAULT NULL,
  hour_rate DECIMAL(38,2) NOT NULL,
  visible TINYINT(1) NOT NULL DEFAULT 1,
  location_point_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_scooters_location_point (location_point_id),
  CONSTRAINT fk_scooters_location_point
    FOREIGN KEY (location_point_id) REFERENCES location_points (id)
);

CREATE TABLE bookings (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  scooter_id BIGINT NOT NULL,
  start_time DATETIME(6) NOT NULL,
  end_time DATETIME(6) DEFAULT NULL,
  duration_hours DOUBLE DEFAULT NULL,
  total_price DECIMAL(38,2) DEFAULT NULL,
  original_price DECIMAL(38,2) DEFAULT NULL,
  discount_amount DECIMAL(38,2) DEFAULT NULL,
  discount_multiplier DECIMAL(10,4) DEFAULT NULL,
  discount_type VARCHAR(32) DEFAULT NULL,
  status VARCHAR(255) NOT NULL,
  start_lat DOUBLE DEFAULT NULL,
  start_lng DOUBLE DEFAULT NULL,
  end_lat DOUBLE DEFAULT NULL,
  end_lng DOUBLE DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_bookings_user (user_id),
  KEY idx_bookings_scooter (scooter_id),
  CONSTRAINT fk_bookings_user
    FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_bookings_scooter
    FOREIGN KEY (scooter_id) REFERENCES scooters (id)
);

CREATE TABLE billing_settings (
  id BIGINT NOT NULL,
  long_rent_threshold_hours DECIMAL(10,2) NOT NULL,
  extra_long_rent_threshold_hours DECIMAL(10,2) NOT NULL,
  long_rent_multiplier DECIMAL(10,4) NOT NULL,
  extra_long_rent_multiplier DECIMAL(10,4) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE billing_settings_logs (
  id BIGINT NOT NULL AUTO_INCREMENT,
  old_long_rent_multiplier DECIMAL(10,4) NOT NULL,
  new_long_rent_multiplier DECIMAL(10,4) NOT NULL,
  old_extra_long_rent_multiplier DECIMAL(10,4) NOT NULL,
  new_extra_long_rent_multiplier DECIMAL(10,4) NOT NULL,
  operator_user_id BIGINT DEFAULT NULL,
  created_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_billing_logs_created_at (created_at),
  CONSTRAINT fk_billing_logs_operator
    FOREIGN KEY (operator_user_id) REFERENCES users (id)
);

CREATE TABLE discount_verification_submissions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  type VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL,
  storage_path VARCHAR(500) NOT NULL,
  original_filename VARCHAR(255) NOT NULL,
  mime_type VARCHAR(128) NOT NULL,
  size_bytes BIGINT NOT NULL,
  submitted_at DATETIME(6) NOT NULL,
  reviewed_at DATETIME(6) DEFAULT NULL,
  reviewer_user_id BIGINT DEFAULT NULL,
  reject_reason VARCHAR(500) DEFAULT NULL,
  version INT NOT NULL,
  PRIMARY KEY (id),
  KEY idx_discount_verify_user_type (user_id, type),
  KEY idx_discount_verify_status_type (status, type),
  CONSTRAINT fk_discount_verify_user
    FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_discount_verify_reviewer
    FOREIGN KEY (reviewer_user_id) REFERENCES users (id)
);

CREATE TABLE payments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  booking_id BIGINT NOT NULL,
  amount DECIMAL(38,2) NOT NULL,
  payment_method VARCHAR(255) DEFAULT NULL,
  timestamp DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_payments_booking (booking_id),
  CONSTRAINT fk_payments_booking
    FOREIGN KEY (booking_id) REFERENCES bookings (id)
);

CREATE TABLE feedbacks (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT DEFAULT NULL,
  scooter_id BIGINT DEFAULT NULL,
  content TEXT NOT NULL,
  priority VARCHAR(20) DEFAULT 'LOW',
  resolved BIT(1) DEFAULT b'0',
  escalated BIT(1) DEFAULT b'0',
  escalated_to VARCHAR(100) DEFAULT NULL,
  escalated_at DATETIME(6) DEFAULT NULL,
  processed_by_user_id BIGINT DEFAULT NULL,
  process_note TEXT DEFAULT NULL,
  escalation_status VARCHAR(32) DEFAULT 'PENDING',
  PRIMARY KEY (id),
  KEY idx_feedbacks_user (user_id),
  KEY idx_feedbacks_scooter (scooter_id),
  KEY idx_feedbacks_priority_escalated (priority, escalated),
  KEY idx_feedbacks_processed_by_user (processed_by_user_id),
  CONSTRAINT fk_feedbacks_user
    FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_feedbacks_scooter
    FOREIGN KEY (scooter_id) REFERENCES scooters (id),
  CONSTRAINT fk_feedbacks_processed_by_user
    FOREIGN KEY (processed_by_user_id) REFERENCES users (id)
);

CREATE TABLE audit_logs (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT DEFAULT NULL,
  action VARCHAR(255) DEFAULT NULL,
  entity_name VARCHAR(255) DEFAULT NULL,
  entity_id VARCHAR(255) DEFAULT NULL,
  request_params TEXT,
  timestamp DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (id)
);

INSERT INTO location_points (id, name, lat, lng) VALUES
  (1, 'Library Plaza', 53.806700, -1.555000),
  (2, 'Engineering Building', 53.808300, -1.553000),
  (3, 'Student Union', 53.807200, -1.557300);

INSERT INTO scooters (id, status, location_lat, location_lng, hour_rate, visible, location_point_id) VALUES
  (1, 'AVAILABLE', 53.806700, -1.555000, 3.50, 1, 1),
  (2, 'AVAILABLE', 53.808300, -1.553000, 3.50, 1, 2),
  (3, 'MAINTENANCE', 53.807200, -1.557300, 3.50, 1, 3),
  (4, 'AVAILABLE', 53.806900, -1.554800, 4.00, 1, 1),
  (5, 'RENTED', 53.808100, -1.553200, 4.00, 1, 2);

INSERT INTO billing_settings (
  id,
  long_rent_threshold_hours,
  extra_long_rent_threshold_hours,
  long_rent_multiplier,
  extra_long_rent_multiplier,
  updated_at
) VALUES (1, 24.00, 72.00, 0.8500, 0.7500, NOW(6));

-- No default users are inserted here because the backend expects BCrypt-hashed passwords.
-- Register users through the application or insert a BCrypt hash generated by PasswordEncoderConfig.
