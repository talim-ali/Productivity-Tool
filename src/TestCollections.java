import model.Task;
import model.TaskManager;

public class TestCollections {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        Task task1 = new Task(
                1,
                "Study DBMS",
                "Study normalization",
                "High",
                "2026-10-08",
                "Pending"
        );

        Task task2 = new Task(
                2,
                "Practice DSA",
                "Practice arrays",
                "Medium",
                "2026-10-09",
                "Pending"
        );

        manager.addTask(task1);
        manager.addTask(task2);

        System.out.println("Total tasks: " + manager.getTaskCount());

        Task found = manager.findTaskById(2);

        if (found != null) {
            System.out.println("Found task: " + found.getTaskName());
        }
    }
}