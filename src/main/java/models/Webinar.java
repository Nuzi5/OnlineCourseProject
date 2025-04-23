package models;

import java.time.LocalDateTime;

public class Webinar {
    private int id;
    private int courseId;
    private int course;
    private String title;
    private String description;
    private final LocalDateTime scheduledAt;
    private final int teacherId;
    private final boolean wasConducted;

    public Webinar(int courseId, int course, String title, String description,
                   LocalDateTime scheduledAt, int teacherId, boolean wasConducted) {
        this.courseId = courseId;
        this.course = course;
        this.title = title;
        this.description = description;
        this.scheduledAt = scheduledAt;
        this.teacherId = teacherId;
        this.wasConducted = wasConducted;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public boolean isWasConducted() {
        return wasConducted;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
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