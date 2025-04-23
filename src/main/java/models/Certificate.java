package models;

import java.time.LocalDateTime;

public class Certificate {
    private final int id;
    private final int userId;
    private final int courseId;
    private final LocalDateTime issueDate;
    private final String certificateNumber;

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