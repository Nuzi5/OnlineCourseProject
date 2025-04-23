package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    private static final String URL = "jdbc:mysql://localhost:3306/education_platform";
    private static final String USER = "root";
    private static final String PASSWORD = "Nurzat211125";
    private static boolean silentMode = true;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initDatabase() {
        initDatabase(true);
    }

    public static void initDatabase(boolean silent) {
        silentMode = silent;
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
            createDefaultUsers(stmt);

            if (!silentMode) {
                System.out.println("База данных успешно инициализирована.");
            }

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
        executeStatement(stmt, sql, "users");
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
        executeStatement(stmt, sql, "courses");
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
        executeStatement(stmt, testsSql, "tests");

        String questionsSql = "CREATE TABLE IF NOT EXISTS test_questions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "test_id INT NOT NULL," +
                "question TEXT NOT NULL," +
                "question_type VARCHAR(20) NOT NULL CHECK (question_type IN ('single_choice', 'multiple_choice', 'text_answer'))," +
                "points INT DEFAULT 1," +
                "FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE" +
                ")";
        executeStatement(stmt, questionsSql, "test_questions");

        String optionsSql = "CREATE TABLE IF NOT EXISTS answer_options (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "question_id INT NOT NULL," +
                "option_text TEXT NOT NULL," +
                "is_correct BOOLEAN DEFAULT FALSE," +
                "FOREIGN KEY (question_id) REFERENCES test_questions(id) ON DELETE CASCADE" +
                ")";
        executeStatement(stmt, optionsSql, "answer_options");
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
        executeStatement(stmt, sql, "enrollments");
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
        executeStatement(stmt, sql, "assignments");
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
        executeStatement(stmt, sql, "webinars");
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
        executeStatement(stmt, sql, "certificates");
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
        executeStatement(stmt, sql, "course_materials");
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
        executeStatement(stmt, sql, "schedule_events");
    }

    private static void createDefaultUsers(Statement stmt) throws SQLException {
        String adminSql = "INSERT IGNORE INTO users (username, password, email, full_name, role) VALUES " +
                "('admin', 'admin123', 'admin@g.com', 'Администратор', 'ADMIN')";

        String teacherSql = "INSERT IGNORE INTO users (username, password, email, full_name, role) VALUES " +
                "('teacher', 'teacher123', 'teacher@g.com', 'Преподаватель 1', 'TEACHER')";

        String managerSql = "INSERT IGNORE INTO users (username, password, email, full_name, role) VALUES " +
                "('manager', 'manager123', 'manager@g.com', 'Менеджер 1', 'MANAGER')";

        String studentSql = "INSERT IGNORE INTO users (username, password, email, full_name, role) VALUES " +
                "('student', 'student123', 'student@g.com', 'Студент 1', 'STUDENT')";

        stmt.executeUpdate(adminSql);
        stmt.executeUpdate(teacherSql);
        stmt.executeUpdate(managerSql);
        stmt.executeUpdate(studentSql);
    }

    private static void executeStatement(Statement stmt, String sql, String tableName) throws SQLException {
        try {
            stmt.execute(sql);
            if (!silentMode) {
                System.out.println("Таблица " + tableName + " создана/проверена");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблицы " + tableName);
            System.err.println("SQL: " + sql);
            throw e;
        }
    }
}