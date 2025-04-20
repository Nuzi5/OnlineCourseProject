package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    private static final String URL = "jdbc:mysql://localhost:3306/education_platform";
    private static final String USER = "root"; // Стандартное имя пользователя MySQL
    private static final String PASSWORD = "Nurzat211125";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initDatabase() {
        try {
            // Регистрация драйвера MySQL - это рекомендуется, хотя в новых версиях Java необязательно
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {

                // Создание таблицы пользователей
                stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "username VARCHAR(50) UNIQUE NOT NULL," +
                        "password VARCHAR(255) NOT NULL," +
                        "email VARCHAR(100) UNIQUE NOT NULL," +
                        "full_name VARCHAR(1)00) NOT NULL," +
                        "role VARCHAR(20) NOT NULL," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")");

                // Создание таблицы курсов
                stmt.execute("CREATE TABLE IF NOT EXISTS courses (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "title VARCHAR(100) NOT NULL," +
                        "description TEXT," +
                        "created_by INT," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "is_active BOOLEAN DEFAULT TRUE," +
                        "FOREIGN KEY (created_by) REFERENCES users(id)" +
                        ")");

                // Создание таблицы материалов курса
                stmt.execute("CREATE TABLE IF NOT EXISTS course_materials (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "course_id INT," +
                        "title VARCHAR(100) NOT NULL," +
                        "content TEXT," +
                        "material_type VARCHAR(20) NOT NULL," +
                        "created_by INT," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (course_id) REFERENCES courses(id)," +
                        "FOREIGN KEY (created_by) REFERENCES users(id)" +
                        ")");

                // Создание таблицы заданий
                stmt.execute("CREATE TABLE IF NOT EXISTS assignments (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "course_id INT," +
                        "title VARCHAR(100) NOT NULL," +
                        "description TEXT," +
                        "deadline TIMESTAMP," +
                        "max_score INT," +
                        "created_by INT," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (course_id) REFERENCES courses(id)," +
                        "FOREIGN KEY (created_by) REFERENCES users(id)" +
                        ")");

                // Создание таблицы тестов
                stmt.execute("CREATE TABLE IF NOT EXISTS tests (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "course_id INT," +
                        "title VARCHAR(100) NOT NULL," +
                        "description TEXT," +
                        "time_limit INT," +
                        "passing_score INT," +
                        "created_by INT," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (course_id) REFERENCES courses(id)," +
                        "FOREIGN KEY (created_by) REFERENCES users(id)" +
                        ")");

                // Создание таблицы вопросов теста
                stmt.execute("CREATE TABLE IF NOT EXISTS test_questions (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "test_id INT," +
                        "question TEXT NOT NULL," +
                        "question_type VARCHAR(20) NOT NULL," +
                        "points INT DEFAULT 1," +
                        "FOREIGN KEY (test_id) REFERENCES tests(id)" +
                        ")");

                // Создание таблицы вариантов ответов
                stmt.execute("CREATE TABLE IF NOT EXISTS answer_options (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "question_id INT," +
                        "option_text TEXT NOT NULL," +
                        "is_correct BOOLEAN DEFAULT FALSE," +
                        "FOREIGN KEY (question_id) REFERENCES test_questions(id)" +
                        ")");

                // Создание таблицы записи на курсы
                stmt.execute("CREATE TABLE IF NOT EXISTS enrollments (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "user_id INT," +
                        "course_id INT," +
                        "enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "completed_at TIMESTAMP," +
                        "is_active BOOLEAN DEFAULT TRUE," +
                        "UNIQUE (user_id, course_id)," +
                        "FOREIGN KEY (user_id) REFERENCES users(id)," +
                        "FOREIGN KEY (course_id) REFERENCES courses(id)" +
                        ")");

                // Создание таблицы успеваемости студентов
                stmt.execute("CREATE TABLE IF NOT EXISTS student_progress (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "enrollment_id INT," +
                        "material_id INT," +
                        "material_type VARCHAR(20)," +
                        "status VARCHAR(20) DEFAULT 'not_started'," +
                        "score INT," +
                        "completed_at TIMESTAMP," +
                        "feedback TEXT," +
                        "FOREIGN KEY (enrollment_id) REFERENCES enrollments(id)" +
                        ")");

                // Создание таблицы вебинаров
                stmt.execute("CREATE TABLE IF NOT EXISTS webinars (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "course_id INT," +
                        "title VARCHAR(100) NOT NULL," +
                        "description TEXT," +
                        "scheduled_at TIMESTAMP NOT NULL," +
                        "teacher_id INT," +
                        "was_conducted BOOLEAN DEFAULT FALSE," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (course_id) REFERENCES courses(id)," +
                        "FOREIGN KEY (teacher_id) REFERENCES users(id)" +
                        ")");

                // Создание таблицы расписания
                stmt.execute("CREATE TABLE IF NOT EXISTS schedule (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "course_id INT," +
                        "event_title VARCHAR(100) NOT NULL," +
                        "event_type VARCHAR(20) NOT NULL," +
                        "event_date TIMESTAMP NOT NULL," +
                        "created_by INT," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (course_id) REFERENCES courses(id)," +
                        "FOREIGN KEY (created_by) REFERENCES users(id)" +
                        ")");

                // Создание таблицы сертификатов
                stmt.execute("CREATE TABLE IF NOT EXISTS certificates (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "user_id INT," +
                        "course_id INT," +
                        "issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "certificate_number VARCHAR(50) UNIQUE NOT NULL," +
                        "FOREIGN KEY (user_id) REFERENCES users(id)," +
                        "FOREIGN KEY (course_id) REFERENCES courses(id)" +
                        ")");

                System.out.println("База данных успешно инициализирована.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при инициализации базы данных: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Драйвер MySQL не найден: " + e.getMessage());
            e.printStackTrace();
        }
    }
}