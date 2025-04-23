package models;

import java.time.LocalDateTime;

public class Assignment {
    private int id;
    private int courseId;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private int maxScore;

    public Assignment(int id, int courseId, String title,
                      String description, LocalDateTime deadline, int maxScore) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.maxScore = maxScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
