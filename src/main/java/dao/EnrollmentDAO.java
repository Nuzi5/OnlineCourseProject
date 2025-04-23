package dao;

import models.Course;
import models.Student;
import models.User;
import db.DatabaseSetup;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
////    public boolean enrollStudent(int studentId, int courseId) {
//        String sql = "INSERT INTO enrollments (user_id, course_id) VALUES (?, ?)";
//
//        try (Connection conn = DatabaseSetup.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, studentId);
//            pstmt.setInt(2, courseId);
//
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.err.println("Error enrolling student: " + e.getMessage());
//            return false;
//        }
//    }

    public boolean isCourseCompleted(int studentId, int courseId) {
        String sql = "SELECT completed_at FROM enrollments " +
                "WHERE user_id = ? AND course_id = ? AND completed_at IS NOT NULL";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Возвращает true если есть запись с completed_at
            }
        } catch (SQLException e) {
            System.err.println("Error checking course completion status: " + e.getMessage());
            return false;
        }
    }

////    public boolean unenrollStudent(int studentId, int courseId) {
//        String sql = "UPDATE enrollments SET is_active = FALSE WHERE user_id = ? AND course_id = ?";
//
//        try (Connection conn = DatabaseSetup.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, studentId);
//            pstmt.setInt(2, courseId);
//
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.err.println("Error unenrolling student: " + e.getMessage());
//            return false;
//        }
//    }

    public List<Course> getStudentCourses(int studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c " +
                "JOIN enrollments e ON c.id = e.course_id " +
                "WHERE e.user_id = ? AND e.is_active = TRUE";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("created_by"),
                        rs.getBoolean("is_active")
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Error getting student courses: " + e.getMessage());
        }
        return courses;
    }

    public List<User> getCourseStudents(int courseId) {
        List<User> students = new ArrayList<>();
        String sql = "SELECT u.* FROM users u " +
                "JOIN enrollments e ON u.id = e.user_id " +
                "WHERE e.course_id = ? AND e.is_active = TRUE AND u.role = 'STUDENT'";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User student = new Student(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("full_name")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error getting course students: " + e.getMessage());
        }
        return students;
    }

////    public boolean markCourseCompleted(int studentId, int courseId) {
//        String sql = "UPDATE enrollments SET completed_at = CURRENT_TIMESTAMP " +
//                "WHERE user_id = ? AND course_id = ?";
//
//        try (Connection conn = DatabaseSetup.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, studentId);
//            pstmt.setInt(2, courseId);
//
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.err.println("Error marking course as completed: " + e.getMessage());
//            return false;
//        }
//    }

////    public boolean isStudentEnrolled(int studentId, int courseId) {
//        String sql = "SELECT 1 FROM enrollments " +
//                "WHERE user_id = ? AND course_id = ? AND is_active = TRUE";
//
//        try (Connection conn = DatabaseSetup.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, studentId);
//            pstmt.setInt(2, courseId);
//
//            try (ResultSet rs = pstmt.executeQuery()) {
//                return rs.next();
//            }
//        } catch (SQLException e) {
//            System.err.println("Error checking enrollment: " + e.getMessage());
//            return false;
//        }
//    }

    public LocalDateTime getLastActivityDate(int studentId, int courseId) {
        String sql = "SELECT MAX(activity_date) FROM course_activity WHERE user_id = ? AND course_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp(1);
                    return timestamp != null ? timestamp.toLocalDateTime() : null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting last activity date: " + e.getMessage());
        }
        return null;
    }

    public int getActiveStudentsCount(int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ? AND is_active = TRUE";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting active students: " + e.getMessage());
        }
        return 0;
    }

    public int getCourseProgress(int studentId, int courseId) {
        String countSql = "SELECT COUNT(*) FROM course_materials WHERE course_id = ?";
        String completedSql = "SELECT COUNT(*) FROM student_progress " +
                "WHERE student_id = ? AND course_id = ? AND is_completed = TRUE";

        try (Connection conn = DatabaseSetup.getConnection()) {
            int totalItems = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(countSql)) {
                pstmt.setInt(1, courseId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        totalItems = rs.getInt(1);
                    }
                }
            }

            if (totalItems == 0) {
                return 0;
            }
            int completedItems = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(completedSql)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, courseId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        completedItems = rs.getInt(1);
                    }
                }
            }
            return (int) Math.round((completedItems * 100.0) / totalItems);

        } catch (SQLException e) {
            System.err.println("Error getting course progress: " + e.getMessage());
            return -1;
        }
    }
}
