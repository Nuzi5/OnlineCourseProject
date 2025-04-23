package dao;

import models.ScheduleEvent;
import db.DatabaseSetup;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
    public boolean createEvent(ScheduleEvent event) {
        String sql = "INSERT INTO schedule_events (course_id, title, event_type, event_time, created_by) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, event.getCourseId());
            pstmt.setString(2, event.getTitle());
            pstmt.setString(3, event.getEventType());
            pstmt.setTimestamp(4, Timestamp.valueOf(event.getEventTime()));
            pstmt.setInt(5, event.getCreatedBy());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        event.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error creating schedule event: " + e.getMessage());
            return false;
        }
    }

    public List<ScheduleEvent> getAllUpcomingEvents() {
        List<ScheduleEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM schedule_events WHERE event_time >= NOW() ORDER BY event_time";

        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ScheduleEvent event = new ScheduleEvent(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getString("event_type"),
                        rs.getTimestamp("event_time").toLocalDateTime(),
                        rs.getInt("created_by")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            System.err.println("Error getting upcoming events: " + e.getMessage());
        }
        return events;
    }
}
