import dao.TaskDAO;
import model.Task;

import java.util.List;

public class TestTaskDAO {

    public static void main(String[] args) {

        TaskDAO taskDAO = new TaskDAO();

        List<Task> tasks = taskDAO.getAllTasks();

        System.out.println("----- TASKS FROM DATABASE -----");

        for (Task task : tasks) {

            System.out.println("ID: " + task.getTaskId());
            System.out.println("Task: " + task.getTaskName());
            System.out.println("Description: " + task.getDescription());
            System.out.println("Priority: " + task.getPriority());
            System.out.println("Deadline: " + task.getDeadline());
            System.out.println("Status: " + task.getStatus());

            System.out.println("------------------------------");
        }
    }
}