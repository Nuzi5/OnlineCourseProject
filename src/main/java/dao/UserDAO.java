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
                          String fullName, String role, boolean isActive) throws SQLException {
        int maxId = getMaxUserIdFromDB();
        int newId = maxId + 1;

        String sql = "INSERT INTO users (id, username, password, email, full_name, role, is_active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newId);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, email);
            stmt.setString(5, fullName);
            stmt.setString(6, role.toUpperCase());
            stmt.setBoolean(7, isActive);

            stmt.executeUpdate();

            updateAutoIncrement(newId + 1);

            return newId;
        }
    }

    private int getMaxUserIdFromDB() throws SQLException {
        String sql = "SELECT MAX(id) FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 5; // Базовый offset = 5
        }
    }

    private void updateAutoIncrement(int newValue) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE users AUTO_INCREMENT = " + newValue);
        }
    }

    private void disableAutoIncrement() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET @@SESSION.sql_mode='NO_AUTO_VALUE_ON_ZERO'");
        }
    }

    private void enableAutoIncrement() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET @@SESSION.sql_mode=''");
        }
    }

    private void updateAutoIncrementAfterInsert(int insertedId) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE users AUTO_INCREMENT = " + (insertedId + 1));
        }
    }

    private boolean isIdExists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private int countUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public void resetIdSequence() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Устанавливаем AUTO_INCREMENT = 6
            stmt.execute("ALTER TABLE users AUTO_INCREMENT = 6");
            System.out.println("ID последовательность сброшена. AUTO_INCREMENT установлен в 6");
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