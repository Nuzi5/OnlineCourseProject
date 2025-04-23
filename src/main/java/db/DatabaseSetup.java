package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    private static final String URL = "jdbc:mysql://localhost:3306/education_platform";
    private static final String USER = "root";
    private static final String PASSWORD = "Nurzat211125";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initDatabase() {
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = getConnection();
            stmt = conn.createStatement();

            createUserTable(stmt);
            createCourseTable(stmt);

            createEnrollmentTable(stmt);
            createAssignmentTable(stmt);
            createTestTables(stmt);
            createWebinarTable(stmt);
            createCertificateTable(stmt);

            createCourseMaterialsTable(stmt);
            createScheduleEventsTable(stmt);

            System.out.println("База данных успешно инициализирована.");

        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка: MySQL драйвер не найден.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Ошибка при работе с базой данных:");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии соединения:");
                e.printStackTrace();
            }
        }
    }

    private static void createUserTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "email VARCHAR(100) UNIQUE NOT NULL," +
                "full_name VARCHAR(100) NOT NULL," +
                "role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'TEACHER', 'STUDENT', 'MANAGER'))," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        executeStatement(stmt, sql, "Таблица users");
    }

    private static void createCourseTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS courses (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "title VARCHAR(100) NOT NULL," +
                "description TEXT," +
                "created_by INT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "is_active BOOLEAN DEFAULT TRUE," +
                "FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL" +
                ")";
        executeStatement(stmt, sql, "Таблица courses");
    }

    private static void createTestTables(Statement stmt) throws SQLException {
        String testsSql = "CREATE TABLE IF NOT EXISTS tests (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "course_id INT NOT NULL," +
                "title VARCHAR(100) NOT NULL," +
                "description TEXT," +
                "time_limit INT," +
                "passing_score INT," +
                "created_by INT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE," +
                "FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL" +
                ")";
        executeStatement(stmt, testsSql, "Таблица tests");

        String questionsSql = "CREATE TABLE IF NOT EXISTS test_questions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "test_id INT NOT NULL," +
                "question TEXT NOT NULL," +
                "question_type VARCHAR(20) NOT NULL CHECK (question_type IN ('single_choice', 'multiple_choice', 'text_answer'))," +
                "points INT DEFAULT 1," +
                "FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE" +
                ")";
        executeStatement(stmt, questionsSql, "Таблица test_questions");

        String optionsSql = "CREATE TABLE IF NOT EXISTS answer_options (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "question_id INT NOT NULL," +
                "option_text TEXT NOT NULL," +
                "is_correct BOOLEAN DEFAULT FALSE," +
                "FOREIGN KEY (question_id) REFERENCES test_questions(id) ON DELETE CASCADE" +
                ")";
        executeStatement(stmt, optionsSql, "Таблица answer_options");
    }

    private static void createEnrollmentTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS enrollments (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "course_id INT NOT NULL," +
                "enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "completed_at TIMESTAMP NULL," +
                "is_active BOOLEAN DEFAULT TRUE," +
                "progress INT DEFAULT 0," +
                "UNIQUE (user_id, course_id)," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
                "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE" +
                ")";
        executeStatement(stmt, sql, "Таблица enrollments");
    }

    private static void createAssignmentTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS assignments (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "course_id INT NOT NULL," +
                "title VARCHAR(100) NOT NULL," +
                "description TEXT," +
                "deadline TIMESTAMP," +
                "max_score INT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE" +
                ")";
        executeStatement(stmt, sql, "Таблица assignments");
    }

    private static void createWebinarTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS webinars (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "course_id INT NOT NULL," +
                "title VARCHAR(100) NOT NULL," +
                "description TEXT," +
                "scheduled_at TIMESTAMP NOT NULL," +
                "teacher_id INT NOT NULL," +
                "was_conducted BOOLEAN DEFAULT FALSE," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE," +
                "FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")";
        executeStatement(stmt, sql, "Таблица webinars");
    }

    private static void createCertificateTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS certificates (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "course_id INT NOT NULL," +
                "certificate_number VARCHAR(50) NOT NULL," +
                "issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
                "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE" +
                ")";
        executeStatement(stmt, sql, "Таблица certificates");
    }

    private static void createCourseMaterialsTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS course_materials (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "course_id INT NOT NULL," +
                "title VARCHAR(100) NOT NULL," +
                "content TEXT," +
                "material_type VARCHAR(20) NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE" +
                ")";
        executeStatement(stmt, sql, "Таблица course_materials");
    }

    private static void createScheduleEventsTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS schedule_events (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "course_id INT NOT NULL," +
                "title VARCHAR(100) NOT NULL," +
                "event_type VARCHAR(20) NOT NULL," +
                "event_time TIMESTAMP NOT NULL," +
                "created_by INT NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE," +
                "FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE" +
                ")";
        executeStatement(stmt, sql, "Таблица schedule_events");
    }

    private static void executeStatement(Statement stmt, String sql, String tableName) throws SQLException {
        try {
            stmt.execute(sql);
            System.out.println(tableName + " создана/проверена");
        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблицы " + tableName);
            System.err.println("SQL: " + sql);
            throw e;
        }
    }
}