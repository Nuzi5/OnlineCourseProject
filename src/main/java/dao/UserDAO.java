package dao;

import models.*;
import db.DatabaseSetup;
import java.sql.*;
import java.util.*;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public int createUser(String username, String password, String email,
                          String fullName, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, full_name, role) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, fullName);
            stmt.setString(5, role);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return -1;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            throw new SQLException("Не удалось получить ID пользователя");
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
}