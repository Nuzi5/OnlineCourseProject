package models;

import java.time.LocalDateTime;

public class TestResult {
    private final int id;
    private final int studentId;
    public final int testId;
    private final int score;
    public final int passingScore;
    public final LocalDateTime completedAt;

    public TestResult(int id, int studentId, int testId, int score,
                      int passingScore, LocalDateTime completedAt) {
        this.id = id;
        this.studentId = studentId;
        this.testId = testId;
        this.score = score;
        this.passingScore = passingScore;
        this.completedAt = completedAt;
    }
    public int getId() { 
        return id; 
    }
    public int getScore() { 
        return score; 
    }

    public int getStudentId() {
        return studentId;
    }
}
    