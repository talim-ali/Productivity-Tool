package model;

public class WorkSession extends PomodoroSession {

    private String focusGoal;

    public WorkSession(int sessionId, int taskId, int duration,
                       String sessionType, String sessionDate,
                       String focusGoal) {

        super(sessionId, taskId, duration, sessionType, sessionDate);

        this.focusGoal = focusGoal;
    }

    public String getFocusGoal() {
        return focusGoal;
    }

    public void setFocusGoal(String focusGoal) {
        this.focusGoal = focusGoal;
    }

   @Override
    public void startSession() {
    System.out.println("Work session started: " + focusGoal);
}
}