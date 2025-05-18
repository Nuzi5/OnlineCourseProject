package models.additional;

import java.time.LocalDateTime;

public class AssignmentSubmission {
    private final int id;
    private final int assignmentId;
    private final int studentId;
    private final String answer;
    private final LocalDateTime submittedAt;
    private final Integer score;
    private final String studentName;

    public AssignmentSubmission(int id, int assignmentId, int studentId,
                                String answer, LocalDateTime submittedAt,
                                Integer score, String studentName) {
        this.id = id;
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.answer = answer;
        this.submittedAt = submittedAt;
        this.score = score;
        this.studentName = studentName;
    }

    public int getId() { return id; }
    public int getAssignmentId() { return assignmentId; }
    public int getStudentId() { return studentId; }
    public String getAnswer() { return answer; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public Integer getScore() { return score; }
    public String getStudentName() { return studentName; }
}