package model;

public class PomodoroSession {

    private int sessionId;
    private int taskId;
    private int duration;
    private String sessionType;
    private String sessionDate;
    public PomodoroSession(int sessionId, int taskId, int duration,
                       String sessionType, String sessionDate) {

    this.sessionId = sessionId;
    this.taskId = taskId;
    this.duration = duration;
    this.sessionType = sessionType;
    this.sessionDate = sessionDate;
}
public int getSessionId() {
    return sessionId;
}

public void setSessionId(int sessionId) {
    this.sessionId = sessionId;
}

public int getTaskId() {
    return taskId;
}

public void setTaskId(int taskId) {
    this.taskId = taskId;
}

public int getDuration() {
    return duration;
}

public void setDuration(int duration) {
    this.duration = duration;
}

public String getSessionType() {
    return sessionType;
}

public void setSessionType(String sessionType) {
    this.sessionType = sessionType;
}

public String getSessionDate() {
    return sessionDate;
}

public void setSessionDate(String sessionDate) {
    this.sessionDate = sessionDate;
}
public void startSession() {
    System.out.println("Starting a Pomodoro session...");
}

}