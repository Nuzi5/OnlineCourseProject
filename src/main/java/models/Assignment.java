package models;

import java.time.LocalDateTime;
public class Assignment {
    private final int id;
    private final int courseId;
    private final String title;
    private final String description;
    private final LocalDateTime deadline;
    private final int maxScore;
    private final Integer score;  // Может быть null
    private final LocalDateTime submittedAt;  // Может быть null

    public Assignment(int id, int courseId, String title,
                      String description, LocalDateTime deadline,
                      int maxScore) {
        this(id, courseId, title, description, deadline, maxScore, null, null);
    }

    public Assignment(int id, int courseId, String title,
                      String description, LocalDateTime deadline,
                      int maxScore, Integer score, LocalDateTime submittedAt) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.maxScore = maxScore;
        this.score = score;
        this.submittedAt = submittedAt;
    }

    public int getId() { return id; }
    public int getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getDeadline() { return deadline; }
    public int getMaxScore() { return maxScore; }
    public Integer getScore() { return score; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
}