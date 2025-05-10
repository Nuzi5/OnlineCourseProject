package models;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class CourseManager extends User {
    private final Connection connection;
    private final Scanner scanner;

    public CourseManager(int id, String username, String password, String email, String fullName, Connection connection) {
        super(id, username, password, email, fullName, "MANAGER");
        this.connection = connection;
        this.scanner = new Scanner (System.in);
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n=== МЕНЮ МЕНЕДЖЕРА КУРСОВ ===");
            System.out.println("1. Управление расписанием");
            System.out.println("2. Мониторинг курсов");
            System.out.println("3. Анализ успеваемости студентов");
            System.out.println("4. Генерация отчетов");
            System.out.println("5. Управление сертификатами");
            System.out.println("6. Выход");
            System.out.print("Выберите опцию: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    manageSchedule();
                    break;
                case 2:
                    monitorCourses();
                    break;
                case 3:
                    analyzeStudentProgress();
                    break;
                case 4:
                    generateReports();
                    break;
                case 5:
                    manageCertificates();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }
    private void manageSchedule(){
        System.out.println("\n--- Управление расписанием ---");
        System.out.println("1. Просмотр расписания");
        System.out.println("2. Добавить событие");
        System.out.println("3. Удалить событие");
        System.out.print("Выберите действие: ");

        int action = scanner.nextInt();
        scanner.nextLine();

        switch (action) {
            case 1:
                viewSchedule();
                break;
            case 2:
                addScheduleEvent();
                break;
            case 3:
                deleteScheduleEvent();
                break;
            default:
                System.out.println("Неверный выбор!");
        }
    }

    private void viewSchedule(){
        String sql = "SELECT * FROM schedule_events ORDER BY event_time";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nТекущее расписание:");
            while (rs.next()) {
                System.out.printf(
                        "ID: %d | Курс: %d | Тип: %s | Время: %s | Название: %s\n",
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getString("event_type"),
                        rs.getTimestamp("event_time"),
                        rs.getString("title")
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении расписания: " + e.getMessage());
        }
    }

    private void addScheduleEvent() {
        System.out.print("Введите ID курса: ");
        int courseId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Тип события (LECTURE/WEBINAR/EXAM): ");
        String eventType = scanner.nextLine();

        System.out.print("Название события: ");
        String title = scanner.nextLine();

        System.out.print("Дата и время (гггг-мм-дд чч:мм): ");
        String dateTimeStr = scanner.nextLine();
        LocalDateTime eventTime = LocalDateTime.parse(dateTimeStr,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        String sql = "INSERT INTO schedule_events (course_id, event_type, title, event_time, created_by) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setString(2, eventType);
            stmt.setString(3, title);
            stmt.setTimestamp(4, Timestamp.valueOf(eventTime));
            stmt.setInt(5, this.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Событие успешно добавлено!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении события: " + e.getMessage());
        }
    }

    private void monitorCourses() {
        String sql = "SELECT c.id, c.title, COUNT(e.student_id) as students_count " +
                "FROM courses c LEFT JOIN enrollments e ON c.id = e.course_id " +
                "GROUP BY c.id, c.title";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nМониторинг курсов:");
            System.out.println("ID | Название курса | Количество студентов");
            while (rs.next()) {
                System.out.printf(
                        "%d | %s | %d\n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("students_count")
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при мониторинге курсов: " + e.getMessage());
        }
    }

    private void analyzeStudentProgress() {
        System.out.print("Введите ID курса для анализа: ");
        int courseId = scanner.nextInt();

        String sql = "SELECT s.id, s.full_name, AVG(ts.score) as avg_score " +
                "FROM test_results ts " +
                "JOIN users s ON ts.student_id = s.id " +
                "WHERE ts.test_id IN (SELECT id FROM tests WHERE course_id = ?) " +
                "GROUP BY s.id, s.full_name";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nУспеваемость студентов:");
            System.out.println("ID | Студент | Средний балл");
            while (rs.next()) {
                System.out.printf(
                        "%d | %s | %.2f\n",
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getDouble("avg_score")
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при анализе успеваемости: " + e.getMessage());
        }
    }

    private void generateReports() {
        System.out.println("\n--- Генерация отчетов ---");
        System.out.println("1. Отчет по курсу");
        System.out.println("2. Общий отчет");
        System.out.print("Выберите тип отчета: ");

        int reportType = scanner.nextInt();

        if (reportType == 1) {
            generateCourseReport();
        } else if (reportType == 2) {
            generateGeneralReport();
        } else {
            System.out.println("Неверный выбор!");
        }
    }

    private void generateCourseReport() {
        System.out.print("Введите ID курса: ");
        int courseId = scanner.nextInt();

        try {
            String courseSql = "SELECT title, description FROM courses WHERE id = ?";
            PreparedStatement courseStmt = connection.prepareStatement(courseSql);
            courseStmt.setInt(1, courseId);
            ResultSet courseRs = courseStmt.executeQuery();

            if (!courseRs.next()) {
                System.out.println("Курс не найден!");
                return;
            }

            String courseTitle = courseRs.getString("title");

            String statsSql = "SELECT COUNT(DISTINCT student_id) as student_count, " +
                    "AVG(score) as avg_score FROM test_results " +
                    "WHERE test_id IN (SELECT id FROM tests WHERE course_id = ?)";
            PreparedStatement statsStmt = connection.prepareStatement(statsSql);
            statsStmt.setInt(1, courseId);
            ResultSet statsRs = statsStmt.executeQuery();
            statsRs.next();

            System.out.println("\nОтчет по курсу: " + courseTitle);
            System.out.println("Количество студентов: " + statsRs.getInt("student_count"));
            System.out.printf("Средний балл: %.2f\n", statsRs.getDouble("avg_score"));

        } catch (SQLException e) {
            System.out.println("Ошибка при генерации отчета: " + e.getMessage());
        }
    }

    private void manageCertificates() {
        System.out.println("\n--- Управление сертификатами ---");
        System.out.println("1. Выдать сертификат");
        System.out.println("2. Просмотреть выданные сертификаты");
        System.out.print("Выберите действие: ");

        int action = scanner.nextInt();
        scanner.nextLine();

        if (action == 1) {
            issueCertificate();
        } else if (action == 2) {
            viewCertificates();
        } else {
            System.out.println("Неверный выбор!");
        }
    }

    private void issueCertificate() {
        System.out.print("Введите ID студента: ");
        int studentId = scanner.nextInt();

        System.out.print("Введите ID курса: ");
        int courseId = scanner.nextInt();

        System.out.print("Введите итоговую оценку: ");
        int finalScore = scanner.nextInt();

        String sql = "INSERT INTO certificates (student_id, course_id, issue_date, final_score) " +
                "VALUES (?, ?, CURRENT_DATE, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.setInt(3, finalScore);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Сертификат успешно выдан!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при выдаче сертификата: " + e.getMessage());
        }
    }

    private void viewCertificates() {
        String sql = "SELECT c.id, u.full_name as student, co.title as course, " +
                "c.issue_date, c.final_score " +
                "FROM certificates c " +
                "JOIN users u ON c.student_id = u.id " +
                "JOIN courses co ON c.course_id = co.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nВыданные сертификаты:");
            System.out.println("ID | Студент | Курс | Дата выдачи | Оценка");
            while (rs.next()) {
                System.out.printf(
                        "%d | %s | %s | %s | %d\n",
                        rs.getInt("id"),
                        rs.getString("student"),
                        rs.getString("course"),
                        rs.getDate("issue_date"),
                        rs.getInt("final_score")
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении сертификатов: " + e.getMessage());
        }
    }

    private void generateGeneralReport() {
        try {
            String coursesSql = "SELECT COUNT(*) as total_courses, " +
                    "SUM(CASE WHEN is_active THEN 1 ELSE 0 END) as active_courses " +
                    "FROM courses";
            Statement coursesStmt = connection.createStatement();
            ResultSet coursesRs = coursesStmt.executeQuery(coursesSql);
            coursesRs.next();

            String usersSql = "SELECT role, COUNT(*) as count FROM users GROUP BY role";
            Statement usersStmt = connection.createStatement();
            ResultSet usersRs = usersStmt.executeQuery(usersSql);

            String certsSql = "SELECT COUNT(*) as total_certs, AVG(final_score) as avg_score " +
                    "FROM certificates";
            Statement certsStmt = connection.createStatement();
            ResultSet certsRs = certsStmt.executeQuery(certsSql);
            certsRs.next();

            System.out.println("\n=== ОБЩИЙ ОТЧЕТ ПО ПЛАТФОРМЕ ===");
            System.out.println("\nКурсы:");
            System.out.println("Всего курсов: " + coursesRs.getInt("total_courses"));
            System.out.println("Активных курсов: " + coursesRs.getInt("active_courses"));

            System.out.println("\nПользователи:");
            while (usersRs.next()) {
                System.out.printf("%s: %d\n",
                        usersRs.getString("role"),
                        usersRs.getInt("count"));
            }

            System.out.println("\nСертификаты:");
            System.out.println("Всего выдано: " + certsRs.getInt("total_certs"));
            System.out.printf("Средний балл: %.2f\n", certsRs.getDouble("avg_score"));

        } catch (SQLException e) {
            System.out.println("Ошибка при генерации общего отчета: " + e.getMessage());
        }
    }

    private void deleteScheduleEvent() {
        System.out.print("Введите ID события для удаления: ");
        int eventId = scanner.nextInt();

        String checkSql = "SELECT id FROM schedule_events WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, eventId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Событие с ID " + eventId + " не найдено!");
                return;
            }

            System.out.print("Вы уверены, что хотите удалить событие? (y/n): ");
            String confirmation = scanner.next();

            if (confirmation.equalsIgnoreCase("y")) {
                String deleteSql = "DELETE FROM schedule_events WHERE id = ?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, eventId);
                    int affectedRows = deleteStmt.executeUpdate();

                    if (affectedRows > 0) {
                        System.out.println("Событие успешно удалено!");
                    }
                }
            } else {
                System.out.println("Удаление отменено");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении события: " + e.getMessage());
        }
    }

}