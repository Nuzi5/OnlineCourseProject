package models;

public class StudentProgress {
    private final int studentId;
    private final String studentName;
    private final double averageScore;

    public StudentProgress(int studentId, String studentName, double averageScore) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.averageScore = averageScore;
    }

    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public double getAverageScore() { return averageScore; }
}
