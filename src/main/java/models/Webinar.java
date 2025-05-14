package models;

import java.time.LocalDateTime;

public class Webinar {
    private final int id;
    private final String title;
    private final LocalDateTime scheduledAt;
    private final String courseTitle;

    public Webinar(int id, String title, LocalDateTime scheduledAt, String courseTitle) {
        this.id = id;
        this.title = title;
        this.scheduledAt = scheduledAt;
        this.courseTitle = courseTitle;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public String getCourseTitle() { return courseTitle; }
}