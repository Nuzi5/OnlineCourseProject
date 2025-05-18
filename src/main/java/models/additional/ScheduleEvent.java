package models.additional;

import java.time.LocalDateTime;

public class ScheduleEvent {
    private final int id;
    private final int courseId;
    private final String title;
    private final String eventType;
    private final LocalDateTime eventTime;
    private final int createdBy;

    public ScheduleEvent(int id, int courseId, String title,
                         String eventType, LocalDateTime eventTime, int createdBy) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.createdBy = createdBy;
    }

    public int getId() { return id; }
    public int getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getEventType() { return eventType; }
    public LocalDateTime getEventTime() { return eventTime; }
    public int getCreatedBy() { return createdBy; }
}