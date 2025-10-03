-- Seed Data for Testing --

-- Insert test users with BCrypt hashed password 'password123'
INSERT INTO users (email, encrypted_password, created_at, updated_at, role, first_name, last_name, sign_in_count)
VALUES 
  ('admin@example.com', '$2a$10$iZI/PRp8r/z/tZHepLJQge.nr9emyLG8gkOvKf5AZwUX1A4lOgGve', NOW(), NOW(), 'admin', 'Admin', 'User', 0),
  ('user@example.com', '$2a$10$iZI/PRp8r/z/tZHepLJQge.nr9emyLG8gkOvKf5AZwUX1A4lOgGve', NOW(), NOW(), 'user', 'Test', 'User', 0);

-- Insert addresses from LocationMaster (California - Eligible)
INSERT INTO addresses (street, unit_number, city, state, zip, county, created_at, updated_at)
VALUES 
  ('212 encounter bay', NULL, 'Alameda', 'California', '90255', 'Alameda', NOW(), NOW()),
  ('978 stannage avenu', NULL, 'Albany', 'California', '91106', 'Alameda', NOW(), NOW()),
  ('433 Camden', NULL, 'San Ramon', 'California', '90210', 'Contra Costa', NOW(), NOW()),
  ('1920 Hinckley', NULL, 'Albany', 'California', '94706', 'Alameda', NOW(), NOW());

-- Insert Florida address (Eligible)
INSERT INTO addresses (street, unit_number, city, state, zip, county, created_at, updated_at)
VALUES 
  ('123 Test', NULL, 'Delray Beach', 'Florida', '90255', 'Palm Beach', NOW(), NOW());

-- Insert New York addresses (Not Eligible)
INSERT INTO addresses (street, unit_number, city, state, zip, county, created_at, updated_at)
VALUES 
  ('400 E', NULL, 'Newburgh', 'NY', '12550', 'Orange', NOW(), NOW()),
  ('123 Test', NULL, 'Newburgh', 'NY', '12550', 'Orange', NOW(), NOW()),
  ('834 67th', NULL, 'Brooklyn', 'NY', '11220', 'Kings', NOW(), NOW()),
  ('11 Brooks Hill', NULL, 'Lansing', 'NY', '14882', 'Tompkins', NOW(), NOW()),
  ('2477 Norte Vista', NULL, 'WallKill', 'NY', '12589', 'Ulster', NOW(), NOW());

-- Insert Pennsylvania addresses (Not Eligible)
INSERT INTO addresses (street, unit_number, city, state, zip, county, created_at, updated_at)
VALUES 
  ('23 Green', NULL, 'Clairton', 'PA', '15025', 'Allegheny', NOW(), NOW()),
  ('760 Sproul', NULL, 'Springfield', 'PA', '19064', 'Delaware', NOW(), NOW()),
  ('320 Harrison', NULL, 'Lewisburg', 'PA', '17837', 'Union', NOW(), NOW());

-- Insert loan applications with SSNs from DecisionMaster applicant.json
INSERT INTO loan_applications (first_name, last_name, date_of_birth, ssn, email, phone, income, income_type, requested_loan_amount, address_id, status, created_at, updated_at)
VALUES 
  ('John', 'Smith', '1985-03-15', '123456781', 'john.smith@email.com', '555-0101', 75000.00, 'salary', 250000.00, 1, 'pending', NOW(), NOW()),
  ('Jane', 'Doe', '1990-07-22', '123456782', 'jane.doe@email.com', '555-0102', 85000.00, 'salary', 300000.00, 2, 'approved', NOW(), NOW()),
  ('Bob', 'Johnson', '1982-11-10', '123456783', 'bob.johnson@email.com', '555-0103', 65000.00, 'hourly', 200000.00, 3, 'pending', NOW(), NOW()),
  ('Alice', 'Williams', '1988-05-18', '123456784', 'alice.w@email.com', '555-0104', 92000.00, 'salary', 350000.00, 4, 'approved', NOW(), NOW()),
  ('Charlie', 'Brown', '1975-09-25', '123456785', 'charlie.b@email.com', '555-0105', 58000.00, 'hourly', 180000.00, 5, 'rejected', NOW(), NOW()),
  ('Diana', 'Miller', '1993-01-30', '123456786', 'diana.m@email.com', '555-0106', 78000.00, 'salary', 275000.00, 6, 'pending', NOW(), NOW()),
  ('Edward', 'Davis', '1980-12-05', '123456787', 'edward.d@email.com', '555-0107', 105000.00, 'salary', 400000.00, 7, 'approved', NOW(), NOW()),
  ('Fiona', 'Garcia', '1987-06-14', '123456788', 'fiona.g@email.com', '555-0108', 72000.00, 'salary', 240000.00, 8, 'pending', NOW(), NOW()),
  ('George', 'Martinez', '1991-04-20', '123456789', 'george.m@email.com', '555-0109', 88000.00, 'salary', 320000.00, 9, 'approved', NOW(), NOW());