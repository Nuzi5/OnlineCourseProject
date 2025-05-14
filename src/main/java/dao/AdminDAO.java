package dao;

import models.*;

import java.sql.*;
import java.util.*;

public class AdminDAO {
    private final Connection connection;

    public AdminDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean createCourse(String title, String description, int createdBy, boolean isActive) throws SQLException {
        String sql = "INSERT INTO courses (title, description, created_by, is_active) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setInt(3, createdBy);
            stmt.setBoolean(4, isActive);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateCourse(int courseId, String title, String description) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE courses SET ");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            sql.append("title = ?, ");
            params.add(title);
        }
        if (description != null && !description.isEmpty()) {
            sql.append("description = ?, ");
            params.add(description);
        }

        if (params.isEmpty()) return false;

        sql.delete(sql.length()-2, sql.length());
        sql.append(" WHERE id = ?");
        params.add(courseId);

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i+1, params.get(i));
            }
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean toggleCourseStatus(int courseId) throws SQLException {
        String sql = "UPDATE courses SET is_active = NOT is_active WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT id, title, description, created_by, is_active FROM courses";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("created_by"),
                        rs.getBoolean("is_active")
                ));
            }
        }
        return courses;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, email, full_name, role, is_active FROM users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                user.setActive(rs.getBoolean("is_active"));
                users.add(user);
            }
        }
        return users;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String fullName = rs.getString("full_name");
        String role = rs.getString("role");

        return switch (role) {
            case "ADMIN" -> new Administrator(id, username, "", email, fullName, connection);
            case "TEACHER" -> new Teacher(id, username, "", email, fullName, connection);
            case "STUDENT" -> new Student(id, username, "", email, fullName, connection);
            case "MANAGER" -> new CourseManager(id, username, "", email, fullName, connection);
            default -> throw new SQLException("Unknown role: " + role);
        };
    }

    public boolean createUser(String username, String password, String email,
                              String fullName, String role, boolean isActive) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, full_name, role, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, fullName);
            stmt.setString(5, role);
            stmt.setBoolean(6, isActive);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateUserRole(int userId, String newRole) throws SQLException {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newRole);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean toggleUserStatus(int userId) throws SQLException {
        String sql = "UPDATE users SET is_active = NOT is_active WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateUser(int userId, String username, String fullName,
                              String email, String role, boolean isActive) throws SQLException {
        String sql = "UPDATE users SET username = ?, full_name = ?, email = ?, role = ?, is_active = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, fullName);
            stmt.setString(3, email);
            stmt.setString(4, role);
            stmt.setBoolean(5, isActive);
            stmt.setInt(6, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public ResultSet getPlatformStats() throws SQLException {
        String sql = "SELECT (SELECT COUNT(*) FROM users) as user_count, " +
                "(SELECT COUNT(*) FROM courses) as course_count, " +
                "(SELECT COUNT(*) FROM enrollments) as enrollment_count, " +
                "(SELECT COUNT(*) FROM test_results) as test_count";
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sql);
    }

    public ResultSet getActivityLogs(int limit) throws SQLException {
        String sql = "SELECT * FROM activity_logs ORDER BY action_time DESC" +
                (limit > 0 ? " LIMIT " + limit : "");
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sql);
    }

    public ResultSet getActiveUsers() throws SQLException {
        String sql = "SELECT id, username, full_name, last_login FROM users " +
                "WHERE is_active = true ORDER BY last_login DESC LIMIT 20";
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sql);
    }

    public boolean userExists(int userId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isTeacher(int userId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE id = ? AND role = 'TEACHER'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}