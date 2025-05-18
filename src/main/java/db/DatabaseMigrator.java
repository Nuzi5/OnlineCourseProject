package db;

import java.sql.*;

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
        try (Statement stmt = connection.createStatement()) {
            addFinalScoreToCertificates(connection);
            addEnrollmentIdToTestResults(connection);
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
}