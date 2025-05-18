package dao;

import models.*;
import models.additional.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ManagerDAO {
    private final Connection connection;

    public ManagerDAO(Connection connection) {
        this.connection = connection;}

    public List<ScheduleEvent> getScheduleEvents(int courseId) throws SQLException {
        List<ScheduleEvent> events = new ArrayList<>();
        String sql = "SELECT id, title, event_type, event_time FROM schedule_events WHERE course_id = ? ORDER BY event_time";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                events.add(new ScheduleEvent(
                        rs.getInt("id"),
                        courseId,
                        rs.getString("title"),
                        rs.getString("event_type"),
                        rs.getTimestamp("event_time").toLocalDateTime(),
                        0
                ));
            }}
        return events;
    }

    public boolean addScheduleEvent(int courseId, String title, String eventType,
                                    LocalDateTime eventTime, int createdBy) throws SQLException {
        String sql = "INSERT INTO schedule_events (course_id, title, event_type, event_time, created_by) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setString(2, title);
            stmt.setString(3, eventType);
            stmt.setTimestamp(4, Timestamp.valueOf(eventTime));
            stmt.setInt(5, createdBy);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteScheduleEvent(int eventId) throws SQLException {
        String sql = "DELETE FROM schedule_events WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<CourseStats> getCourseStatistics() throws SQLException {
        List<CourseStats> stats = new ArrayList<>();
        String sql = "SELECT c.id, c.title, COUNT(e.user_id) as students_count " +
                "FROM courses c LEFT JOIN enrollments e ON c.id = e.course_id " +
                "GROUP BY c.id, c.title";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stats.add(new CourseStats(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("students_count")
                ));
            }}
        return stats;
    }

    public List<StudentProgress> getStudentProgress(int courseId) throws SQLException {
        List<StudentProgress> progressList = new ArrayList<>();
        String sql = "SELECT s.id, s.full_name, AVG(ts.score) as avg_score " +
                "FROM test_results ts " +
                "JOIN users s ON ts.student_id = s.id " +
                "WHERE ts.test_id IN (SELECT id FROM tests WHERE course_id = ?) " +
                "GROUP BY s.id, s.full_name";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                progressList.add(new StudentProgress(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getDouble("avg_score")
                ));
            }}
        return progressList;
    }

    public List<Certificate> getCourseCertificates(int courseId) throws SQLException {
        List<Certificate> certificates = new ArrayList<>();
        String sql = "SELECT c.id, u.full_name, c.issue_date, c.final_score " +
                "FROM certificates c " +
                "JOIN users u ON c.student_id = u.id " +
                "WHERE c.course_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                certificates.add(new Certificate(
                        rs.getInt("id"),
                        0, // student_id временно 0
                        courseId,
                        rs.getDate("issue_date").toLocalDate(),
                        rs.getInt("final_score"),
                        rs.getString("full_name")
                ));
            }}
        return certificates;
    }

    public boolean issueCertificate(int studentId, int courseId, int finalScore) throws SQLException {
        String sql = "INSERT INTO certificates (student_id, course_id, final_score, issue_date) " +
                "VALUES (?, ?, ?, CURRENT_DATE)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.setInt(3, finalScore);
            return stmt.executeUpdate() > 0;
        }
    }

    public CourseReport generateCourseReport(int courseId) throws SQLException {
        String sql = "SELECT c.title, " +
                "COUNT(DISTINCT e.student_id) as student_count, " +
                "AVG(tr.score) as avg_score " +
                "FROM courses c " +
                "LEFT JOIN enrollments e ON c.id = e.course_id " +
                "LEFT JOIN test_results tr ON tr.test_id IN (SELECT id FROM tests WHERE course_id = c.id) " +
                "WHERE c.id = ? " +
                "GROUP BY c.title";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new CourseReport(
                        rs.getString("title"),
                        rs.getInt("student_count"),
                        rs.getDouble("avg_score")
                );
            }}
        return null;
    }

    public PlatformReport generatePlatformReport() throws SQLException {
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM users) as user_count, " +
                "(SELECT COUNT(*) FROM courses) as course_count, " +
                "(SELECT COUNT(*) FROM enrollments) as enrollment_count, " +
                "(SELECT COUNT(*) FROM certificates) as certificate_count";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return new PlatformReport(
                        rs.getInt("user_count"),
                        rs.getInt("course_count"),
                        rs.getInt("enrollment_count"),
                        rs.getInt("certificate_count")
                );
            }}
        return null;
    }

    public List<CourseWithTeacher> getAllCoursesWithTeachers() throws SQLException {
        List<CourseWithTeacher> courses = new ArrayList<>();
        String sql = "SELECT c.id, c.title, c.description, c.is_active, u.full_name AS teacher_name " +
                "FROM courses c " +
                "LEFT JOIN course_teachers ct ON c.id = ct.course_id " +
                "LEFT JOIN users u ON ct.teacher_id = u.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(new CourseWithTeacher(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("teacher_name"),
                        rs.getBoolean("is_active")
                ));
            }
        }
        return courses;
    }
}