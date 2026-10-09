package dao;

import model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DBConnection;

public class TaskDAO {

    // Add a new task to the database
    public void addTask(Task task) {

        String sql = "INSERT INTO tasks " +
                     "(task_name, description, priority, deadline, status) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getTaskName());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getPriority());
            statement.setString(4, task.getDeadline());
            statement.setString(5, task.getStatus());

            statement.executeUpdate();

            System.out.println("Task added successfully.");

        } catch (SQLException e) {
            System.out.println("Error adding task: " + e.getMessage());
        }
    }


    // Get all tasks from the database
    public List<Task> getAllTasks() {

        List<Task> tasks = new ArrayList<>();

        String sql = "SELECT * FROM tasks";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Task task = new Task(
                    resultSet.getInt("task_id"),
                    resultSet.getString("task_name"),
                    resultSet.getString("description"),
                    resultSet.getString("priority"),
                    resultSet.getString("deadline"),
                    resultSet.getString("status")
                );

                tasks.add(task);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving tasks: " + e.getMessage());
        }

        return tasks;
    }


    // Update an existing task
    public void updateTask(Task task) {

        String sql = "UPDATE tasks SET " +
                     "task_name = ?, " +
                     "description = ?, " +
                     "priority = ?, " +
                     "deadline = ?, " +
                     "status = ? " +
                     "WHERE task_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getTaskName());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getPriority());
            statement.setString(4, task.getDeadline());
            statement.setString(5, task.getStatus());
            statement.setInt(6, task.getTaskId());

            statement.executeUpdate();

            System.out.println("Task updated successfully.");

        } catch (SQLException e) {
            System.out.println("Error updating task: " + e.getMessage());
        }
    }


    // Delete a task from the database
    public void deleteTask(int taskId) {

        String sql = "DELETE FROM tasks WHERE task_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, taskId);

            statement.executeUpdate();

            System.out.println("Task deleted successfully.");

        } catch (SQLException e) {
            System.out.println("Error deleting task: " + e.getMessage());
        }
    }


    // Get one task using its ID
    public Task getTaskById(int taskId) {

        String sql = "SELECT * FROM tasks WHERE task_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, taskId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                return new Task(
                    resultSet.getInt("task_id"),
                    resultSet.getString("task_name"),
                    resultSet.getString("description"),
                    resultSet.getString("priority"),
                    resultSet.getString("deadline"),
                    resultSet.getString("status")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error finding task: " + e.getMessage());
        }

        return null;
    }
}