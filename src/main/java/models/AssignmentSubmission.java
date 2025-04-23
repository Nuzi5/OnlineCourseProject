package models;

import java.time.LocalDateTime;

public class AssignmentSubmission {
    private int id;
    private int assignmentId;
    private int studentId;
    private String answer;
    private LocalDateTime submittedAt;
    private int score;
    private String feedback;

    public AssignmentSubmission(int id, int assignmentId, int studentId,
                                String answer, LocalDateTime submittedAt,
                                int score, String feedback) {
        this.id = id;
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.answer = answer;
        this.submittedAt = submittedAt;
        this.score = score;
        this.feedback = feedback;
    }

    public String getAnswer() {
        return answer;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}