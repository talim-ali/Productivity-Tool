CREATE DATABASE IF NOT EXISTS productivity_tool;

USE productivity_tool;

CREATE TABLE IF NOT EXISTS tasks (
    task_id INT PRIMARY KEY AUTO_INCREMENT,
    task_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    priority VARCHAR(20),
    deadline DATE,
    status VARCHAR(20) DEFAULT 'Pending'
);

CREATE TABLE IF NOT EXISTS pomodoro_sessions (
    session_id INT PRIMARY KEY AUTO_INCREMENT,
    task_id INT NOT NULL,
    duration INT NOT NULL,
    session_type VARCHAR(20) NOT NULL,
    session_date DATE NOT NULL,
    FOREIGN KEY (task_id) REFERENCES tasks(task_id)
);