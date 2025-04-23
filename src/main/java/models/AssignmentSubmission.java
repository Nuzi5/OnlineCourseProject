package models;

import java.time.LocalDateTime;

public class AssignmentSubmission {
    private int id;
    private final int assignmentId;
    private final int studentId;
    private final String answer;
    private LocalDateTime submittedAt;
    private final int score;
    private final Object o;

    public AssignmentSubmission(int id, int assignmentId, int studentId,
                                String answer, LocalDateTime submittedAt,
                                int score, Object o) {
        this.id = id;
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.answer = answer;
        this.submittedAt = submittedAt;
        this.score = score;
        this.o = o;
    }

    public String getAnswer() {
        return answer;
    }

    public int getScore() {
        return score;
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

    public int getAssignmentId() {
        return assignmentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}