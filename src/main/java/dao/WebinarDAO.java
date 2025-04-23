package dao;

import models.Webinar;
import db.DatabaseSetup;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WebinarDAO {

    public boolean createWebinar(Webinar webinar) {
        String sql = "INSERT INTO webinars (course_id, title, description, scheduled_at, teacher_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, webinar.getCourseId());
            pstmt.setString(2, webinar.getTitle());
            pstmt.setString(3, webinar.getDescription());
            pstmt.setTimestamp(4, Timestamp.valueOf(webinar.getScheduledAt()));
            pstmt.setInt(5, webinar.getTeacherId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        webinar.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error creating webinar: " + e.getMessage());
            return false;
        }
    }

    public boolean markAsConducted(int webinarId) {
        String sql = "UPDATE webinars SET was_conducted = true WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, webinarId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error marking webinar as conducted: " + e.getMessage());
            return false;
        }
    }

    public List<Webinar> getCourseWebinars(int courseId) {
        List<Webinar> webinars = new ArrayList<>();
        String sql = "SELECT * FROM webinars WHERE course_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Webinar webinar = new Webinar(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("scheduled_at").toLocalDateTime(),
                        rs.getInt("teacher_id"),
                        rs.getBoolean("was_conducted")
                );
                webinars.add(webinar);
            }
        } catch (SQLException e) {
            System.err.println("Error getting webinars: " + e.getMessage());
        }
        return webinars;
    }

    public List<Webinar> getTeacherWebinars(int teacherId, boolean b) {
        List<Webinar> webinars = new ArrayList<>();
        String sql = "SELECT * FROM webinars WHERE teacher_id = ? ORDER BY scheduled_at";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Webinar webinar = new Webinar(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("scheduled_at").toLocalDateTime(),
                        rs.getInt("teacher_id"),
                        rs.getBoolean("was_conducted")
                );
                webinars.add(webinar);
            }
        } catch (SQLException e) {
            System.err.println("Error getting teacher webinars: " + e.getMessage());
        }
        return webinars;
    }

    public List<Webinar> getTeacherWebinars(int teacherId) {
        List<Webinar> webinars = new ArrayList<>();
        String sql = "SELECT * FROM webinars WHERE teacher_id = ? ORDER BY scheduled_at";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Webinar webinar = new Webinar(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("scheduled_at").toLocalDateTime(),
                        rs.getInt("teacher_id"),
                        rs.getBoolean("was_conducted")
                );
                webinars.add(webinar);
            }
        } catch (SQLException e) {
            System.err.println("Error getting teacher webinars: " + e.getMessage());
        }
        return webinars;
    }
}
