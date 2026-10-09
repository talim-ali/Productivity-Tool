# GUVI Evaluation Rubric Map

**Project:** Time Management and Productivity Enhancement Tool

## 1. OOP Implementation — 10 Marks

### Inheritance
Files:
- `src/model/PomodoroSession.java`
- `src/model/WorkSession.java`
- `src/model/BreakSession.java`

### Polymorphism
Files:
- `src/model/WorkSession.java`
- `src/model/BreakSession.java`
- `src/TestOOP.java`

Look for overridden `startSession()` methods.

### Exception Handling
Files:
- `src/exception/TaskValidationException.java`
- `src/Main.java`
- `src/TestException.java`

Look for task validation and custom exception handling.

### Interfaces
File:
- `src/Main.java`

Look for the `TimerListener` interface and its implementation.

## 2. Collections and Generics — 6 Marks

File:
- `src/model/TaskManager.java`

Demonstration:
- `src/TestCollections.java`

Look for `List<Task>` and `ArrayList<Task>`.

## 3. Multithreading and Synchronization — 4 Marks

File:
- `src/Main.java`

Look for `PomodoroEngine`, `Runnable`, `Thread`,
synchronized methods or blocks, and `Thread.sleep()`.

## 4. Classes for Database Operations — 7 Marks

File:
- `src/dao/TaskDAO.java`

Demonstrations:
- `src/TestTaskDAO.java`
- `src/TestAddTask.java`
- `src/TestUpdateTask.java`

Look for methods that add, retrieve, update, and delete tasks.

## 5. Database Connectivity (JDBC) — 3 Marks

File:
- `src/util/DBConnection.java`

Demonstration:
- `src/TestConnection.java`

Look for JDBC connection configuration and `DriverManager`.

## 6. Implement JDBC for Database Connectivity — 3 Marks

Files:
- `src/dao/TaskDAO.java`
- `src/util/DBConnection.java`

Look for JDBC connections, `PreparedStatement`, and SQL operations.

## Database Setup

The application uses a locally configured MySQL database.
Refer to `README.md` for setup instructions.