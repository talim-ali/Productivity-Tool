import model.PomodoroSession;
import model.WorkSession;
import model.BreakSession;

public class TestOOP {

    public static void main(String[] args) {

        // Polymorphism
        PomodoroSession session1 = new WorkSession(
                1,
                1,
                25,
                "Work",
                "2026-10-08",
                "Study DBMS"
        );

        PomodoroSession session2 = new BreakSession(
                2,
                1,
                5,
                "Break",
                "2026-10-08",
                "Short Break"
        );

        session1.startSession();
        session2.startSession();
    }
}