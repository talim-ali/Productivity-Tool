 import dao.TaskDAO;
import model.Task;

public class TestAddTask {

    public static void main(String[] args) {

        TaskDAO taskDAO = new TaskDAO();

        Task task = new Task(
                0,
                "Test New Task",
                "Testing add task functionality",
                "Medium",
                "2026-10-15",
                "Pending"
        );

        taskDAO.addTask(task);

        System.out.println("Add task test completed.");
    }
}