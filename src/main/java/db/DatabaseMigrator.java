package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseMigrator {
    public static void applyMigrations() {
        try (Connection connection = DatabaseSetup.getConnection()) {
            applyMigrations(connection);
        } catch (SQLException e) {
            System.err.println("Ошибка при применении миграций: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void applyMigrations(Connection connection) throws SQLException {
        try {
            addFinalScoreToCertificates(connection);
            addEnrollmentIdToTestResults(connection);
            resetAndSetUsersAutoIncrement(connection);
        } catch (SQLException e) {
            throw new SQLException("Ошибка при применении миграций", e);
        }
    }

    private static void addFinalScoreToCertificates(Connection connection) throws SQLException {
        if (!columnExists(connection, "certificates", "final_score")) {
            executeUpdate(connection, "ALTER TABLE certificates ADD COLUMN final_score INT NOT NULL DEFAULT 0");
        }
    }

    private static void addEnrollmentIdToTestResults(Connection connection) throws SQLException {
        if (!columnExists(connection, "test_results", "enrollment_id")) {
            executeUpdate(connection, "ALTER TABLE test_results ADD COLUMN enrollment_id INT");
        }

        if (!foreignKeyExists(connection, "test_results", "fk_test_results_enrollment")) {
            executeUpdate(connection, "ALTER TABLE test_results ADD CONSTRAINT fk_test_results_enrollment " +
                    "FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE");
        }
    }

    private static boolean columnExists(Connection connection, String table, String column) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, table);
            stmt.setString(2, column);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt("cnt") > 0;
        }
    }

    private static boolean foreignKeyExists(Connection connection, String table, String fkName) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS " +
                "WHERE TABLE_NAME = ? AND CONSTRAINT_NAME = ? AND CONSTRAINT_TYPE = 'FOREIGN KEY'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, table);
            stmt.setString(2, fkName);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt("cnt") > 0;
        }
    }

    private static void executeUpdate(Connection connection, String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    private static int getCurrentMaxUserId(Connection connection) throws SQLException {
        String sql = "SELECT MAX(id) AS max_id FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("max_id") : 0;
        }
    }

    public static void resetAndSetUsersAutoIncrement(Connection connection) throws SQLException {
        try {
            List<Integer> existingIds = new ArrayList<>();
            String selectSql = "SELECT id FROM users ORDER BY id";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {
                while (rs.next()) {
                    existingIds.add(rs.getInt("id"));
                }
            }

            if (existingIds.isEmpty()) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("ALTER TABLE users AUTO_INCREMENT = 6");
                    System.out.println("Таблица users пуста. AUTO_INCREMENT установлен в 6");
                }
                return;
            }

            int maxId = Collections.max(existingIds);
            int nextId = Math.max(maxId + 1, 6);

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("ALTER TABLE users AUTO_INCREMENT = " + nextId);
                System.out.println("AUTO_INCREMENT для users установлен в " + nextId);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при сбросе AUTO_INCREMENT для таблицы users: " + e.getMessage());
            throw e;
        }
    }
}