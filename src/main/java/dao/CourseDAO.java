package dao;

import models.Course;
import db.DatabaseSetup;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    public boolean createCourse(Course course) {
        String sql = "INSERT INTO courses (title, description, created_by, is_active) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, course.getTitle());
            pstmt.setString(2, course.getDescription());
            pstmt.setInt(3, course.getCreatedBy());
            pstmt.setBoolean(4, course.isActive());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        course.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Ошибка при создании курса: " + e.getMessage());
            return false;
        }
    }

    public boolean deactivateCourse(int courseId) {
        String sql = "UPDATE courses SET is_active = false WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при деактивации курса: " + e.getMessage());
            return false;
        }
    }

    public Course getCourseById(int id) {
        String sql = "SELECT * FROM courses WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createCourseFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении курса: " + e.getMessage());
        }

        return null;
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET title = ?, description = ?, is_active = ? WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getTitle());
            pstmt.setString(2, course.getDescription());
            pstmt.setBoolean(3, course.isActive());
            pstmt.setInt(4, course.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении курса: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCourse(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении курса: " + e.getMessage());
            return false;
        }
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";

        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Course course = createCourseFromResultSet(rs);
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка курсов: " + e.getMessage());
        }

        return courses;
    }

    public List<Course> getActiveCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE is_active = true";

        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Course course = createCourseFromResultSet(rs);
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении активных курсов: " + e.getMessage());
        }

        return courses;
    }

    public List<Course> getCoursesByTeacher(int creatorId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE created_by = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, creatorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = createCourseFromResultSet(rs);
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении курсов по создателю: " + e.getMessage());
        }

        return courses;
    }

    private Course createCourseFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        int createdBy = rs.getInt("created_by");
        boolean isActive = rs.getBoolean("is_active");

        return new Course(id, title, description, createdBy, isActive);
    }
}


