package db;
import java.sql.*;

public class DatabaseSetup {
    private static final String ROOT_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/education_platform";
    private static final String USER = "root";
    private static final String PASSWORD = "Nurzat211125";
    public static final String DB_NAME = "education_platform";
    private static boolean silentMode = true;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    public static void initDatabase() {
        initDatabase(true);}

    public static void initDatabase(boolean silent) {
        silentMode = silent;
        Connection rootConn = null;
        Statement rootStmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            rootConn = DriverManager.getConnection(ROOT_URL, USER, PASSWORD);
            rootStmt = rootConn.createStatement();

            rootStmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);

            rootStmt.close();
            rootConn.close();

            try (Connection dbConn = getConnection();
                 Statement dbStmt = dbConn.createStatement()) {

                createUserTable(dbStmt);
                createCourseTable(dbStmt);
                createEnrollmentTable(dbStmt);
                createAssignmentTable(dbStmt);
                createAssignmentSubmissionsTable(dbStmt);
                createTestTables(dbStmt);
                createTestResultsTable(dbStmt);
                createWebinarTable(dbStmt);
                createCertificateTable(dbStmt);
                createCourseMaterialsTable(dbStmt);
                createScheduleEventsTable(dbStmt);
                createDefaultUsers(dbStmt);
                createCourseTeachersTable(dbStmt);
                createActivityLogsTable(dbStmt);

                updateUserTable(dbStmt);
                updateTestTable(dbStmt);

                if (!silentMode) {
                    System.out.println("База данных успешно инициализирована.");
                }
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка: MySQL драйвер не найден.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Ошибка при работе с базой данных:");
            e.printStackTrace();
        } finally {
            try {
                if (rootStmt != null) rootStmt.close();
                if (rootConn != null) rootConn.close();
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
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "is_active BOOLEAN DEFAULT TRUE," +
                "last_login TIMESTAMP NULL" +
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
        String sql = "INSERT IGNORE INTO users (id, username, password, email, full_name, role) VALUES "
                + "(1, 'admin', 'admin123', 'admin@g.com', 'Администратор', 'ADMIN'),"
                + "(2, 'teacher', 'teacher123', 'teacher@g.com', 'Преподаватель 1', 'TEACHER'),"
                + "(3, 'manager', 'manager123', 'manager@g.com', 'Менеджер 1', 'MANAGER'),"
                + "(4, 'student', 'student123', 'student@g.com', 'Студент 1', 'STUDENT')";

        stmt.executeUpdate(sql);

        stmt.execute("ALTER TABLE users AUTO_INCREMENT = 5");
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

    private static void createAssignmentSubmissionsTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS assignment_submissions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "assignment_id INT NOT NULL," +
                "student_id INT NOT NULL," +
                "answer TEXT," +
                "score INT," +
                "graded_by INT," +
                "graded_at TIMESTAMP NULL," +
                "submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE," +
                "FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE," +
                "FOREIGN KEY (graded_by) REFERENCES users(id) ON DELETE SET NULL," +
                "UNIQUE KEY (assignment_id, student_id)" +
                ")";
        executeStatement(stmt, sql, "assignment_submissions");
    }

    private static void createTestResultsTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS test_results (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "student_id INT NOT NULL," +
                "test_id INT NOT NULL," +
                "score INT NOT NULL," +
                "passing_score INT NOT NULL," +
                "completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE," +
                "FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE," +
                "UNIQUE KEY (student_id, test_id)" +
                ")";
        executeStatement(stmt, sql, "test_results");
    }

    private static void createCourseTeachersTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS course_teachers (" +
                "course_id INT NOT NULL," +
                "teacher_id INT NOT NULL," +
                "assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "PRIMARY KEY (course_id, teacher_id)," +
                "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE," +
                "FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")";
        executeStatement(stmt, sql, "course_teachers");
    }

    private static void createActivityLogsTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS activity_logs (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT," +
                "username VARCHAR(50)," +
                "action VARCHAR(255) NOT NULL," +
                "action_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL" +
                ")";
        executeStatement(stmt, sql, "activity_logs");
    }

    private static void updateUserTable(Statement stmt) throws SQLException {
        ResultSet rs1 = stmt.executeQuery(
                "SELECT COUNT(*) AS cnt FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_NAME = 'users' AND COLUMN_NAME = 'is_active'"
        );
        rs1.next();
        if (rs1.getInt("cnt") == 0) {
            stmt.execute("ALTER TABLE users ADD COLUMN is_active BOOLEAN DEFAULT TRUE");
            if (!silentMode) {
                System.out.println("Колонка 'is_active' добавлена в таблицу users");
            }
        }

        ResultSet rs2 = stmt.executeQuery(
                "SELECT COUNT(*) AS cnt FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_NAME = 'users' AND COLUMN_NAME = 'last_login'"
        );
        rs2.next();
        if (rs2.getInt("cnt") == 0) {
            stmt.execute("ALTER TABLE users ADD COLUMN last_login TIMESTAMP NULL");
            if (!silentMode) {
                System.out.println("Колонка 'last_login' добавлена в таблицу users");
            }
        }
    }

    private static void updateTestTable(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) AS cnt FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_NAME = 'tests' AND COLUMN_NAME = 'created_by'"
        );
        rs.next();
        if (rs.getInt("cnt") == 0) {
            stmt.execute("ALTER TABLE tests ADD COLUMN created_by INT");
            stmt.execute("ALTER TABLE tests ADD CONSTRAINT fk_tests_created_by " +
                    "FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL");
        }

        rs = stmt.executeQuery(
                "SELECT COUNT(*) AS cnt FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_NAME = 'tests' AND COLUMN_NAME = 'created_at'"
        );
        rs.next();
        if (rs.getInt("cnt") == 0) {
            stmt.execute("ALTER TABLE tests ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
        }
    }

    private static void createTestAnswersTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS test_answers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "test_result_id INT NOT NULL," +
                "question_id INT NOT NULL," +
                "answer TEXT NOT NULL," +
                "FOREIGN KEY (test_result_id) REFERENCES test_results(id) ON DELETE CASCADE," +
                "FOREIGN KEY (question_id) REFERENCES test_questions(id) ON DELETE CASCADE" +
                ")";
        executeStatement(stmt, sql, "test_answers");
    }

    private static void createTextAnswerGradesTable(Statement stmt) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS text_answer_grades (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "answer_id INT NOT NULL," +
                "score INT NOT NULL," +
                "graded_by INT NOT NULL," +
                "graded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (answer_id) REFERENCES test_answers(id)," +
                "FOREIGN KEY (graded_by) REFERENCES users(id)" +
                ")";
        executeStatement(stmt, sql, "text_answer_grades");
    }

}