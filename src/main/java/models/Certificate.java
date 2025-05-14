package models;

import java.time.LocalDate;

public class Certificate {
    private final int id;
    private final int studentId;
    private final int courseId;
    private final LocalDate issueDate;
    private final int finalScore;
    private final String studentName;

    public Certificate(int id, int studentId, int courseId,
                       LocalDate issueDate, int finalScore, String studentName) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.issueDate = issueDate;
        this.finalScore = finalScore;
        this.studentName = studentName;
    }


    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public int getCourseId() { return courseId; }
    public LocalDate getIssueDate() { return issueDate; }
    public int getFinalScore() { return finalScore; }
    public String getStudentName() { return studentName; }
}