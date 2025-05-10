package dao;

import models.User;
import models.Administrator;
import models.Teacher;
import models.Student;
import models.CourseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import db.DatabaseSetup;

public class UserDAO {

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (id, username, password, email, full_name, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getFullName());
            stmt.setString(6, user.getRole());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка создания пользователя: " + e.getMessage());
            return false;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? createUserFromResultSet(rs) : null;

        } catch (SQLException e) {
            throw new RuntimeException("Error getting user", e);
        }
    }

    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next() ? createUserFromResultSet(rs) : null;

        } catch (SQLException e) {
            throw new RuntimeException("Authentication error", e);
        }
    }


    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, full_name = ?, role = ? WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getRole());
            pstmt.setInt(6, user.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении пользователя: " + e.getMessage());
            return false;
        }
    }


    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении пользователя: " + e.getMessage());
            return false;
        }
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException("Error getting users", e);
        }
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        String fullName = rs.getString("full_name");
        String role = rs.getString("role");

        Connection connection = DatabaseSetup.getConnection();

        return switch (role) {
            case "ADMIN" -> new Administrator(id, username, password, email, fullName, connection);
            case "TEACHER" -> new Teacher(id, username, password, email, fullName, connection);
            case "STUDENT" -> new Student(id, username, password, email, fullName, connection);
            case "MANAGER" -> new CourseManager(id, username, password, email, fullName, connection);
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }

    public int getNextUserId() throws SQLException {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM users";
        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 1;
        }
    }
}