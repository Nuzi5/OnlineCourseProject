package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Student extends User {
    private final Connection connection;
    private final Scanner scanner;

    public Student(int id, String username, String password,
                   String email, String fullName, Connection connection) {
        super(id, username, password, email, fullName, "STUDENT");
        this.connection = connection;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n=== МЕНЮ СТУДЕНТА ===");
            System.out.println("1. Доступные курсы");
            System.out.println("2. Мои курсы");
            System.out.println("3. Задания и тесты");
            System.out.println("4. Расписание вебинаров");
            System.out.println("5. Прогресс обучения");
            System.out.println("6. Мои сертификаты");
            System.out.println("7. Выход");
            System.out.print("Выберите опцию: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: viewAvailableCourses(); break;
                case 2: viewMyCourses(); break;
                case 3: viewAssignmentsAndTests(); break;
                case 4: viewWebinarSchedule(); break;
                case 5: viewProgress(); break;
                case 6: viewMyCertificates(); break;
                case 7: return;
                default: System.out.println("Неверный выбор!");
            }
        }
    }

    private void viewAvailableCourses() {
        String sql = "SELECT id, title, description FROM courses " +
                "WHERE is_active = true AND id NOT IN " +
                "(SELECT course_id FROM enrollments WHERE student_id = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, this.getId());
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nДоступные курсы:");
            while (rs.next()) {
                System.out.printf(
                        "ID: %d | Название: %s | Описание: %s\n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description")
                );
            }

            enrollToCoursePrompt();
        } catch (SQLException e) {
            System.out.println("Ошибка при получении курсов: " + e.getMessage());
        }
    }

    private void enrollToCoursePrompt() {
        System.out.print("\nВведите ID курса для записи (0 - отмена): ");
        int courseId = scanner.nextInt();
        scanner.nextLine();

        if (courseId != 0) {
            enrollToCourse(courseId);
        }
    }

    private void enrollToCourse(int courseId) {
        String sql = "INSERT INTO enrollments (student_id, course_id, enrolled_at) " +
                "VALUES (?, ?, CURRENT_DATE)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, this.getId());
            stmt.setInt(2, courseId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Вы успешно записаны на курс!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при записи на курс: " + e.getMessage());
        }
    }

    private void viewMyCourses() {
        String sql = "SELECT c.id, c.title, c.description FROM courses c " +
                "JOIN enrollments e ON c.id = e.course_id " +
                "WHERE e.student_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, this.getId());
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nМои курсы:");
            while (rs.next()) {
                System.out.printf(
                        "ID: %d | Название: %s | Описание: %s\n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении курсов: " + e.getMessage());
        }
    }

    private void viewAssignmentsAndTests() {
        System.out.println("\n--- Задания и тесты ---");
        System.out.println("1. Просмотреть задания");
        System.out.println("2. Просмотреть тесты");
        System.out.print("Выберите опцию: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            viewAssignments();
        } else if (choice == 2) {
            viewTests();
        } else {
            System.out.println("Неверный выбор!");
        }
    }

    private void viewAssignments() {
        String sql = "SELECT a.id, a.title, a.description, a.deadline, " +
                "s.score, a.max_score FROM assignments a " +
                "LEFT JOIN assignment_submissions s ON a.id = s.assignment_id AND s.student_id = ? " +
                "WHERE a.course_id IN (SELECT course_id FROM enrollments WHERE student_id = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, this.getId());
            stmt.setInt(2, this.getId());
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nМои задания:");
            while (rs.next()) {
                String status = rs.getObject("score") == null ?
                        (rs.getDate("deadline").toLocalDate().isBefore(LocalDate.now()) ?
                                "Просрочено" : "Не сдано") :
                        String.format("Оценка: %d/%d", rs.getInt("score"), rs.getInt("max_score"));

                System.out.printf(
                        "ID: %d | %s | Дедлайн: %s | %s\n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDate("deadline"),
                        status
                );
            }

            submitAssignmentPrompt();
        } catch (SQLException e) {
            System.out.println("Ошибка при получении заданий: " + e.getMessage());
        }
    }

    private void submitAssignmentPrompt() {
        System.out.print("\nВведите ID задания для сдачи (0 - отмена): ");
        int assignmentId = scanner.nextInt();
        scanner.nextLine();

        if (assignmentId != 0) {
            System.out.print("Введите ваш ответ (текст задания): ");
            String answer = scanner.nextLine();
            submitAssignment(assignmentId, answer);
        }
    }

    private void submitAssignment(int assignmentId, String answer) {
        String sql = "INSERT INTO assignment_submissions " +
                "(assignment_id, student_id, answer, submitted_at) " +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP) " +
                "ON DUPLICATE KEY UPDATE answer = VALUES(answer), submitted_at = VALUES(submitted_at)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            stmt.setInt(2, this.getId());
            stmt.setString(3, answer);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Задание успешно отправлено!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при отправке задания: " + e.getMessage());
        }
    }

    private void viewTests() {
        String sql = "SELECT t.id, t.title, t.description, " +
                "COALESCE(tr.score, -1) as score, t.passing_score " +
                "FROM tests t " +
                "LEFT JOIN test_results tr ON t.id = tr.test_id AND tr.student_id = ? " +
                "WHERE t.course_id IN (SELECT course_id FROM enrollments WHERE student_id = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, this.getId());
            stmt.setInt(2, this.getId());
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nМои тесты:");
            while (rs.next()) {
                int score = rs.getInt("score");
                String status = score == -1 ? "Не пройден" :
                        String.format("Оценка: %d/%d", score, rs.getInt("passing_score"));

                System.out.printf(
                        "ID: %d | %s | %s\n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        status
                );
            }

            takeTestPrompt();
        } catch (SQLException e) {
            System.out.println("Ошибка при получении тестов: " + e.getMessage());
        }
    }

    private void takeTestPrompt() {
        System.out.print("\nВведите ID теста для прохождения (0 - отмена): ");
        int testId = scanner.nextInt();
        scanner.nextLine();

        if (testId != 0) {
            takeTest(testId);
        }
    }

    private void takeTest(int testId) {
        try {
            String testSql = "SELECT title, time_limit FROM tests WHERE id = ?";
            PreparedStatement testStmt = connection.prepareStatement(testSql);
            testStmt.setInt(1, testId);
            ResultSet testRs = testStmt.executeQuery();

            if (!testRs.next()) {
                System.out.println("Тест не найден!");
                return;
            }

            String testTitle = testRs.getString("title");
            int timeLimit = testRs.getInt("time_limit");

            System.out.printf("\nНачало теста: %s\nЛимит времени: %d мин.\n", testTitle, timeLimit);

            String questionsSql = "SELECT id, question_text, question_type, points " +
                    "FROM test_questions WHERE test_id = ? ORDER BY id";
            PreparedStatement questionsStmt = connection.prepareStatement(questionsSql);
            questionsStmt.setInt(1, testId);
            ResultSet questionsRs = questionsStmt.executeQuery();

            int totalScore = 0;
            List<Integer> questionIds = new ArrayList<>();

            while (questionsRs.next()) {
                int questionId = questionsRs.getInt("id");
                questionIds.add(questionId);

                System.out.printf("\nВопрос (%d баллов): %s\n",
                        questionsRs.getInt("points"),
                        questionsRs.getString("question_text"));

                String optionsSql = "SELECT id, option_text FROM answer_options WHERE question_id = ?";
                PreparedStatement optionsStmt = connection.prepareStatement(optionsSql);
                optionsStmt.setInt(1, questionId);
                ResultSet optionsRs = optionsStmt.executeQuery();

                while (optionsRs.next()) {
                    System.out.printf("%d. %s\n",
                            optionsRs.getInt("id"),
                            optionsRs.getString("option_text"));
                }

                System.out.print("Ваш ответ (ID варианта через запятую для MULTIPLE): ");
                String answer = scanner.nextLine();

                int score = checkAnswer(questionId, answer);
                totalScore += score;

                System.out.printf("Баллов за вопрос: %d\n", score);
            }

            saveTestResult(testId, totalScore);

        } catch (SQLException e) {
            System.out.println("Ошибка при прохождении теста: " + e.getMessage());
        }
    }

    private int checkAnswer(int questionId, String studentAnswer) throws SQLException {
        String correctSql = "SELECT id FROM answer_options WHERE question_id = ? AND is_correct = true";
        PreparedStatement correctStmt = connection.prepareStatement(correctSql);
        correctStmt.setInt(1, questionId);
        ResultSet correctRs = correctStmt.executeQuery();

        Set<Integer> correctAnswers = new HashSet<>();
        while (correctRs.next()) {
            correctAnswers.add(correctRs.getInt("id"));
        }

        Set<Integer> studentAnswers = Arrays.stream(studentAnswer.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        return studentAnswers.equals(correctAnswers) ?
                getQuestionPoints(questionId) : 0;
    }

    private int getQuestionPoints(int questionId) throws SQLException {
        String sql = "SELECT points FROM test_questions WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, questionId);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt("points") : 0;
    }

    private void saveTestResult(int testId, int score) throws SQLException {
        String passingSql = "SELECT passing_score FROM tests WHERE id = ?";
        PreparedStatement passingStmt = connection.prepareStatement(passingSql);
        passingStmt.setInt(1, testId);
        ResultSet passingRs = passingStmt.executeQuery();
        passingRs.next();
        int passingScore = passingRs.getInt("passing_score");

        String resultSql = "INSERT INTO test_results " +
                "(student_id, test_id, score, passing_score, completed_at) " +
                "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                "ON DUPLICATE KEY UPDATE score = VALUES(score), completed_at = VALUES(completed_at)";

        PreparedStatement resultStmt = connection.prepareStatement(resultSql);
        resultStmt.setInt(1, this.getId());
        resultStmt.setInt(2, testId);
        resultStmt.setInt(3, score);
        resultStmt.setInt(4, passingScore);
        resultStmt.executeUpdate();

        System.out.printf("\nТест завершен! Ваш результат: %d/%d\n", score, passingScore);
    }

    private void viewWebinarSchedule() {
        String sql = "SELECT w.id, w.title, w.scheduled_at, c.title as course_title " +
                "FROM webinars w " +
                "JOIN courses c ON w.course_id = c.id " +
                "WHERE w.course_id IN (SELECT course_id FROM enrollments WHERE student_id = ?) " +
                "AND w.was_conducted = false " +
                "ORDER BY w.scheduled_at";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, this.getId());
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nПредстоящие вебинары:");
            while (rs.next()) {
                System.out.printf(
                        "ID: %d | Курс: %s | %s | Время: %s\n",
                        rs.getInt("id"),
                        rs.getString("course_title"),
                        rs.getString("title"),
                        rs.getTimestamp("scheduled_at")
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении расписания: " + e.getMessage());
        }
    }

    private void viewProgress() {
        String sql = "SELECT c.id, c.title, " +
                "COALESCE(AVG(a.score), 0) as avg_assignment_score, " +
                "COALESCE(AVG(t.score), 0) as avg_test_score " +
                "FROM enrollments e " +
                "JOIN courses c ON e.course_id = c.id " +
                "LEFT JOIN assignment_submissions a ON a.student_id = e.student_id AND " +
                "a.assignment_id IN (SELECT id FROM assignments WHERE course_id = c.id) " +
                "LEFT JOIN test_results t ON t.student_id = e.student_id AND " +
                "t.test_id IN (SELECT id FROM tests WHERE course_id = c.id) " +
                "WHERE e.student_id = ? " +
                "GROUP BY c.id, c.title";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, this.getId());
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nМой прогресс:");
            while (rs.next()) {
                System.out.printf(
                        "Курс: %s | Средний балл заданий: %.1f | Средний балл тестов: %.1f\n",
                        rs.getString("title"),
                        rs.getDouble("avg_assignment_score"),
                        rs.getDouble("avg_test_score")
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении прогресса: " + e.getMessage());
        }
    }

    private void viewMyCertificates() {
        String sql = "SELECT c.id, co.title as course_name, c.issue_date, c.final_score " +
                "FROM certificates c " +
                "JOIN courses co ON c.course_id = co.id " +
                "WHERE c.student_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, this.getId());
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nМои сертификаты:");
            while (rs.next()) {
                System.out.printf(
                        "ID: %d | Курс: %s | Дата выдачи: %s | Оценка: %d\n",
                        rs.getInt("id"),
                        rs.getString("course_name"),
                        rs.getDate("issue_date"),
                        rs.getInt("final_score")
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении сертификатов: " + e.getMessage());
        }
    }
}

