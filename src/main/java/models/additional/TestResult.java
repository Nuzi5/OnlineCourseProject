package models.additional;

import java.time.LocalDateTime;

public class TestResult {
    private final int id;
    private final int testId;
    private final int studentId;
    private final String studentName;
    private final int score;
    private final int passingScore;
    private final LocalDateTime completedAt;

    public TestResult(int id, int testId, int studentId, String studentName,
                      int score, int passingScore, LocalDateTime completedAt) {
        this.id = id;
        this.testId = testId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.score = score;
        this.passingScore = passingScore;
        this.completedAt = completedAt;
    }

    public int getId() { return id; }
    public int getTestId() { return testId; }
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public int getScore() { return score; }
    public int getPassingScore() { return passingScore; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    public boolean isPassed() {
        return score >= passingScore;
    }

    public double getPercentage() {
        return (double) score / passingScore * 100;
    }
}
    