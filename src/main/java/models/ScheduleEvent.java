package models;

import java.time.LocalDateTime;

public class ScheduleEvent {
    private int id;
    private int courseId;
    private String title;
    private final String eventType;
    private final LocalDateTime eventTime;
    private final int createdBy;

    public ScheduleEvent(int id, int courseId, String title, String eventType,
                         LocalDateTime eventTime, int createdBy) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.createdBy = createdBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getEventType() { return eventType; }
    public LocalDateTime getEventTime() { return eventTime; }
    public int getCreatedBy() { return createdBy; }

}