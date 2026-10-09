# Time Management and Productivity Enhancement Tool

## Project Overview
A Java Swing desktop application designed to help users organize tasks and improve productivity.

## Features
- Add, view, update, and delete tasks
- Manage task priorities and deadlines
- Pomodoro work and break sessions
- Input validation and exception handling
- MySQL database integration through JDBC
- Productivity-focused graphical user interface

## Technologies Used
- Java
- Java Swing
- Object-Oriented Programming
- JDBC
- MySQL
- MySQL Connector/J

## Project Structure
- `src/model/` — Task and session model classes
- `src/dao/` — Database operations
- `src/util/` — Database connection
- `src/exception/` — Custom exception handling
- `src/Main.java` — Main desktop application
- `lib/` — MySQL JDBC connector

## Database Setup
The application uses a local MySQL database named `productivity_tool`.

Before running the application:
1. Install and start MySQL Server.
2. Create the database and required tables.
3. Configure your local database connection.
4. Ensure MySQL Connector/J is available.

The database credentials are stored in a local configuration file that should not be uploaded to GitHub.

## Evaluation Rubric
See `RUBRIC-MAP.md` for guidance on where the project demonstrates:
- Inheritance and polymorphism
- Exception handling and interfaces
- Collections and generics
- Multithreading and synchronization
- Database operation classes
- JDBC connectivity and implementation

## Current Limitation
The application currently uses a locally configured MySQL database. Standalone packaging is a planned future enhancement.