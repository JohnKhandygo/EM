CREATE USER 'em_admin' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON test.* TO 'em_admin'@'%' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON test.* TO 'em_admin'@'localhost' IDENTIFIED BY '1234';
FLUSH PRIVILEGES;
SET sql_log_bin=0;