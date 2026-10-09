
import dao.TaskDAO;
import model.Task;

public class TestUpdateTask {

    public static void main(String[] args) {

        TaskDAO taskDAO = new TaskDAO();

        // Task ID 5 already exists in the database
        Task task = new Task(
                5,
                "Test New Task",
                "Testing update task functionality",
                "High",
                "2026-10-20",
                "Completed"
        );

        taskDAO.updateTask(task);

        System.out.println("Update task test completed.");
    }
}