import exception.TaskValidationException;

public class TestException {

    public static void validateTask(String taskName)
            throws TaskValidationException {

        if (taskName == null || taskName.trim().isEmpty()) {
            throw new TaskValidationException(
                    "Task name cannot be empty!"
            );
        }

        System.out.println("Task is valid.");
    }

    public static void main(String[] args) {

        try {

            validateTask("");

        } catch (TaskValidationException e) {

            System.out.println("Error: " + e.getMessage());
        }
    }
}