INSERT INTO employee (employee_id, name, pto_balance) VALUES ('EMP-992', 'John Doe', 15) ON CONFLICT DO NOTHING;
INSERT INTO employee (employee_id, name, pto_balance)
VALUES
('EMP-101', 'Employee 101', 12),
('EMP-102', 'Employee 102', 8),
('EMP-103', 'Employee 103', 20),
('EMP-21', 'Employee 9912', 15);