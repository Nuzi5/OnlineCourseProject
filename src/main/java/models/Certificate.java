package models;

import java.time.LocalDateTime;

public class Certificate {
    private int id;
    private int userId;
    private int courseId;
    private LocalDateTime issueDate;
    private String certificateNumber;

    public Certificate(int id, int userId, int courseId, LocalDateTime issueDate, String certificateNumber) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.issueDate = issueDate;
        this.certificateNumber = certificateNumber;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getCourseId() {
        return courseId;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }
}