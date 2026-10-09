package model;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private List<Task> tasks;

    public TaskManager() {
        tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public Task findTaskById(int id) {

        for (Task task : tasks) {

            if (task.getTaskId() == id) {
                return task;
            }
        }

        return null;
    }
}