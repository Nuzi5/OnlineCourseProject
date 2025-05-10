package models;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Teacher extends User {
    private final Connection connection;
    private final Scanner scanner;

    public Teacher(int id, String username, String password, String email, String fullName, Connection connection) {
        super(id, username, password, email, fullName, "TEACHER");
        this.connection = connection;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n=== МЕНЮ ПРЕПОДАВАТЕЛЯ ===");
            System.out.println("1. Мои курсы");
            System.out.println("2. Создать учебный материал");
            System.out.println("3. Создать задание");
            System.out.println("4. Создать тест");
            System.out.println("5. Проверить задания студентов");
            System.out.println("6. Управление вебинарами");
            System.out.println("7. Выход");
            System.out.print("Выберите опцию: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: viewMyCourses(); break;
                case 2: createCourseMaterial(); break;
                case 3: createAssignment(); break;
                case 4: createTest(); break;
                case 5: reviewAssignments(); break;
                case 6: manageWebinars(); break;
                case 7: return;
                default: System.out.println("Неверный выбор!");
            }
        }
    }

    private void viewMyCourses(){
        String sql = "SELECT c.id, c.title, c.description FROM courses c " +
                "JOIN course_teachers ct ON c.id = ct.course_id " +
                "WHERE ct.teacher_id = ?";

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

    private void createCourseMaterial() {
        System.out.print("Введите ID курса: ");
        int courseId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Тип материала (LECTURE/VIDEO/TASK): ");
        String materialType = scanner.nextLine();

        System.out.print("Название материала: ");
        String title = scanner.nextLine();

        System.out.print("Содержание (или ссылка): ");
        String content = scanner.nextLine();

        String sql = "INSERT INTO course_materials " +
                "(course_id, title, content, material_type, created_by) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setString(2, title);
            stmt.setString(3, content);
            stmt.setString(4, materialType);
            stmt.setInt(5, this.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Материал успешно создан!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при создании материала: " + e.getMessage());
        }
    }

    private void createAssignment() {
        System.out.print("Введите ID курса: ");
        int courseId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Название задания: ");
        String title = scanner.nextLine();

        System.out.print("Описание: ");
        String description = scanner.nextLine();

        System.out.print("Максимальный балл: ");
        int maxScore = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Срок сдачи (гггг-мм-дд): ");
        String deadlineStr = scanner.nextLine();
        Date deadline = Date.valueOf(deadlineStr);

        String sql = "INSERT INTO assignments " +
                "(course_id, title, description, max_score, deadline, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setInt(4, maxScore);
            stmt.setDate(5, deadline);
            stmt.setInt(6, this.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Задание успешно создано!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при создании задания: " + e.getMessage());
        }
    }

    private void createTest() {
        System.out.print("Введите ID курса: ");
        int courseId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Название теста: ");
        String title = scanner.nextLine();

        System.out.print("Описание: ");
        String description = scanner.nextLine();

        System.out.print("Лимит времени (минуты): ");
        int timeLimit = scanner.nextInt();

        System.out.print("Проходной балл: ");
        int passingScore = scanner.nextInt();
        scanner.nextLine();

        String testSql = "INSERT INTO tests " +
                "(course_id, title, description, time_limit, passing_score, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(testSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, courseId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setInt(4, timeLimit);
            stmt.setInt(5, passingScore);
            stmt.setInt(6, this.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Создание теста не удалось");
            }

            int testId;
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    testId = generatedKeys.getInt(1);
                    System.out.println("Тест создан! ID: " + testId);
                    addQuestionsToTest(testId);
                } else {
                    throw new SQLException("Не удалось получить ID теста");
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при создании теста: " + e.getMessage());
        }
    }

    private void addQuestionsToTest(int testId) {
        System.out.print("Сколько вопросов будет в тесте? ");
        int questionCount = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < questionCount; i++) {
            System.out.printf("\nВопрос %d:\n", i+1);
            System.out.print("Текст вопроса: ");
            String questionText = scanner.nextLine();

            System.out.print("Тип вопроса (SINGLE/MULTIPLE): ");
            String questionType = scanner.nextLine();

            System.out.print("Баллы за вопрос: ");
            int points = scanner.nextInt();
            scanner.nextLine();

            String questionSql = "INSERT INTO test_questions " +
                    "(test_id, question_text, question_type, points) " +
                    "VALUES (?, ?, ?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(questionSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, testId);
                stmt.setString(2, questionText);
                stmt.setString(3, questionType);
                stmt.setInt(4, points);
                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int questionId = generatedKeys.getInt(1);
                        addAnswerOptions(questionId);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Ошибка при добавлении вопроса: " + e.getMessage());
            }
        }
    }

    private void addAnswerOptions(int questionId) {
        System.out.print("Сколько вариантов ответа? ");
        int optionCount = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < optionCount; i++) {
            System.out.printf("Вариант %d:\n", i+1);
            System.out.print("Текст варианта: ");
            String optionText = scanner.nextLine();

            System.out.print("Это правильный ответ? (y/n): ");
            boolean isCorrect = scanner.nextLine().equalsIgnoreCase("y");

            String optionSql = "INSERT INTO answer_options " +
                    "(question_id, option_text, is_correct) " +
                    "VALUES (?, ?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(optionSql)) {
                stmt.setInt(1, questionId);
                stmt.setString(2, optionText);
                stmt.setBoolean(3, isCorrect);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Ошибка при добавлении варианта: " + e.getMessage());
            }
        }
    }

    private void reviewAssignments() {
        System.out.print("Введите ID курса: ");
        int courseId = scanner.nextInt();

        String sql = "SELECT a.id, a.title, s.student_id, u.full_name, s.answer, s.score " +
                "FROM assignments a " +
                "JOIN assignment_submissions s ON a.id = s.assignment_id " +
                "JOIN users u ON s.student_id = u.id " +
                "WHERE a.course_id = ? AND s.score IS NULL";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("Нет работ для проверки");
                return;
            }

            System.out.println("\nРаботы для проверки:");
            while (rs.next()) {
                System.out.printf(
                        "ID работы: %d | Задание: %s | Студент: %s\nОтвет: %s\n\n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("full_name"),
                        rs.getString("answer")
                );

                System.out.print("Введите оценку (0-" + rs.getInt("max_score") + "): ");
                int score = scanner.nextInt();
                scanner.nextLine();

                gradeAssignment(rs.getInt("id"), rs.getInt("student_id"), score);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении работ: " + e.getMessage());
        }
    }

    private void gradeAssignment(int submissionId, int studentId, int score) {
        String sql = "UPDATE assignment_submissions SET score = ?, graded_by = ?, graded_at = NOW() " +
                "WHERE id = ? AND student_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, score);
            stmt.setInt(2, this.getId());
            stmt.setInt(3, submissionId);
            stmt.setInt(4, studentId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Оценка сохранена!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении оценки: " + e.getMessage());
        }
    }

    private void manageWebinars() {
        System.out.println("\n--- Управление вебинарами ---");
        System.out.println("1. Создать вебинар");
        System.out.println("2. Мои вебинары");
        System.out.println("3. Отметить проведение");
        System.out.print("Выберите действие: ");

        int action = scanner.nextInt();
        scanner.nextLine();

        switch (action) {
            case 1: createWebinar(); break;
            case 2: viewMyWebinars(); break;
            case 3: markWebinarCompleted(); break;
            default: System.out.println("Неверный выбор!");
        }
    }

    private void createWebinar() {
        System.out.print("Введите ID курса: ");
        int courseId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Название вебинара: ");
        String title = scanner.nextLine();

        System.out.print("Описание: ");
        String description = scanner.nextLine();

        System.out.print("Дата и время (гггг-мм-дд чч:мм): ");
        String dateTimeStr = scanner.nextLine();
        LocalDateTime scheduledAt = LocalDateTime.parse(dateTimeStr,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        String sql = "INSERT INTO webinars " +
                "(course_id, title, description, scheduled_at, teacher_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setTimestamp(4, Timestamp.valueOf(scheduledAt));
            stmt.setInt(5, this.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Вебинар успешно создан!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при создании вебинара: " + e.getMessage());
        }
    }

    private void viewMyWebinars() {
        String sql = "SELECT id, title, scheduled_at, was_conducted FROM webinars " +
                "WHERE teacher_id = ? ORDER BY scheduled_at";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, this.getId());
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nМои вебинары:");
            while (rs.next()) {
                System.out.printf(
                        "ID: %d | %s | Время: %s | Статус: %s\n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getTimestamp("scheduled_at"),
                        rs.getBoolean("was_conducted") ? "Проведен" : "Запланирован"
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении вебинаров: " + e.getMessage());
        }
    }

    private void markWebinarCompleted() {
        System.out.print("Введите ID вебинара: ");
        int webinarId = scanner.nextInt();

        String sql = "UPDATE webinars SET was_conducted = true WHERE id = ? AND teacher_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, webinarId);
            stmt.setInt(2, this.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Статус вебинара обновлен!");
            } else {
                System.out.println("Вебинар не найден или у вас нет прав");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении статуса: " + e.getMessage());
        }
    }
}