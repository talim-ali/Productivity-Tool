package model;

public class BreakSession extends PomodoroSession {

    private String breakType;

    public BreakSession(int sessionId, int taskId, int duration,
                        String sessionType, String sessionDate,
                        String breakType) {

        super(sessionId, taskId, duration, sessionType, sessionDate);

        this.breakType = breakType;
    }

    public String getBreakType() {
        return breakType;
    }

    public void setBreakType(String breakType) {
        this.breakType = breakType;
    }

    @Override
    public void startSession() {
        System.out.println("Break session started: " + breakType);
    }
}
