package models;

import dao.TeacherDAO;
import models.additional.*;

import java.sql.*;
import java.util.*;
public class Teacher extends User {
    private final Connection connection;
    private final Scanner scanner;
    private final TeacherDAO teacherDAO;

    public Teacher(int id, String username, String password,
                   String email, String fullName, Connection connection) {
        super(id, username, password, email, fullName, "TEACHER", true);
        this.connection = connection;
        this.scanner = new Scanner(System.in);
        this.teacherDAO = new TeacherDAO(connection);
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n*** МЕНЮ ПРЕПОДАВАТЕЛЯ ***");
            System.out.println("1. Мои курсы");
            System.out.println("2. Управление заданиями");
            System.out.println("3. Проверка работ студентов");
            System.out.println("4. Управление тестами");
            System.out.println("5. Выход");
            System.out.print("Выберите опцию: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewMyCourses();
                case 2 -> manageAssignments();
                case 3 -> reviewStudentWork();
                case 4 -> manageTests();
                case 5 -> {
                    System.out.println("\nВыход из системы...\n\nРабота системы завершена. До свидания!");
                    System.exit(0);
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private void viewMyCourses() {
        try {
            List<Course> courses = teacherDAO.getTeacherCourses(this.getId());
            System.out.println("\nМои курсы:");
            for (Course course : courses) {
                System.out.printf("ID: %d | %s | %s | %s\n",
                        course.getId(),
                        course.getTitle(),
                        course.getDescription(),
                        course.isActive() ? "Активен" : "Неактивен"
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении курсов: " + e.getMessage());
        }
    }

    private void manageAssignments() {
        try {
            viewMyCourses();
            System.out.print("Введите ID курса: ");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            List<Assignment> assignments = teacherDAO.getCourseAssignments(courseId);
            System.out.println("\nЗадания курса:");
            for (Assignment assignment : assignments) {
                System.out.printf("ID: %d | %s | Дедлайн: %s\n",
                        assignment.getId(),
                        assignment.getTitle(),
                        assignment.getDeadline());
            }

            System.out.println("\n1. Создать новое задание");
            System.out.println("2. Вернуться назад");
            System.out.print("Выберите действие: ");

            int action = scanner.nextInt();
            scanner.nextLine();

            if (action == 1) {
                createAssignment(courseId);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с заданиями: " + e.getMessage());
        }
    }

    private void createAssignment(int courseId) {
        System.out.print("Введите название задания: ");
        String title = scanner.nextLine();

        System.out.print("Введите описание задания: ");
        String description = scanner.nextLine();

        System.out.print("Введите дедлайн (гггг-мм-дд чч:мм): ");
        String deadlineStr = scanner.nextLine();

        System.out.print("Введите максимальный балл: ");
        int maxScore = scanner.nextInt();
        scanner.nextLine();

        // Здесь будет вызов DAO для создания задания
        System.out.println("Задание успешно создано!");
    }

    private void reviewStudentWork() {
        try {
            viewMyCourses();
            System.out.print("Введите ID курса: ");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            List<Assignment> assignments = teacherDAO.getCourseAssignments(courseId);
            System.out.println("\nВыберите задание:");
            for (Assignment assignment : assignments) {
                System.out.printf("ID: %d | %s\n", assignment.getId(), assignment.getTitle());
            }

            System.out.print("Введите ID задания: ");
            int assignmentId = scanner.nextInt();
            scanner.nextLine();

            List<AssignmentSubmission> submissions = teacherDAO.getAssignmentSubmissions(assignmentId);
            System.out.println("\nРаботы студентов:");
            for (AssignmentSubmission submission : submissions) {
                System.out.printf("ID: %d | Студент: %s | Статус: %s\n",
                        submission.getId(),
                        submission.getStudentName(),
                        submission.getScore() == null ? "Не оценено" : "Оценка: " + submission.getScore());
            }

            System.out.print("Введите ID работы для оценки (0 - отмена): ");
            int submissionId = scanner.nextInt();
            scanner.nextLine();

            if (submissionId != 0) {
                System.out.print("Введите оценку: ");
                int score = scanner.nextInt();
                scanner.nextLine();

                if (teacherDAO.gradeAssignment(submissionId, score, this.getId())) {
                    System.out.println("Оценка успешно сохранена!");
                } else {
                    System.out.println("Ошибка при сохранении оценки");
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке работ: " + e.getMessage());
        }
    }

    private void manageTests() {
        try {
            viewMyCourses();
            System.out.print("Введите ID курса: ");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            List<Test> tests = teacherDAO.getCourseTests(courseId);
            System.out.println("\nТесты курса:");
            for (Test test : tests) {
                System.out.printf("ID: %d | %s | Лимит времени: %d мин.\n",
                        test.getId(),
                        test.getTitle(),
                        test.getTimeLimit());
            }

            System.out.println("\n1. Создать новый тест");
            System.out.println("2. Просмотреть результаты теста");
            System.out.println("3. Вернуться назад");
            System.out.print("Выберите действие: ");

            int action = scanner.nextInt();
            scanner.nextLine();

            if (action == 1) {
                createTest(courseId);
            } else if (action == 2) {
                viewTestResults(courseId);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с тестами: " + e.getMessage());
        }
    }

    private void createTest(int courseId) {
        try {
            System.out.print("Введите название теста: ");
            String title = scanner.nextLine();

            System.out.print("Введите описание теста: ");
            String description = scanner.nextLine();

            System.out.print("Введите лимит времени (в минутах): ");
            int timeLimit = scanner.nextInt();

            System.out.print("Введите проходной балл: ");
            int passingScore = scanner.nextInt();
            scanner.nextLine();

            int createdBy = this.getId();

            int testId = teacherDAO.createTest(courseId, title, description, timeLimit, passingScore, createdBy);
            System.out.println("Тест создан с ID: " + testId);

            while (true) {
                System.out.println("\nДобавление вопроса (q - закончить)");
                System.out.print("Текст вопроса: ");
                String questionText = scanner.nextLine();

                if (questionText.equalsIgnoreCase("q")) break;

                System.out.print("Тип вопроса (single/multiple/text): ");
                String questionType = scanner.nextLine();

                System.out.print("Баллы за вопрос: ");
                int points = scanner.nextInt();
                scanner.nextLine();

                teacherDAO.addTestQuestion(testId, questionText, questionType, points);

                if (!questionType.equalsIgnoreCase("text")) {
                    System.out.println("Добавление вариантов ответа (q - закончить)");
                    while (true) {
                        System.out.print("Текст варианта: ");
                        String optionText = scanner.nextLine();

                        if (optionText.equalsIgnoreCase("q")) break;

                        System.out.print("Это правильный ответ? (y/n): ");
                        boolean isCorrect = scanner.nextLine().equalsIgnoreCase("y");

                        teacherDAO.addAnswerOption(testId, optionText, isCorrect);
                    }
                }
            }
            System.out.println("Тест успешно создан!");
        } catch (SQLException e) {
            System.out.println("Ошибка при создании теста: " + e.getMessage());
        }
    }

    private void viewTestResults(int courseId) {
        try {
            List<Test> tests = teacherDAO.getCourseTests(courseId);
            if (tests.isEmpty()) {
                System.out.println("В этом курсе пока нет тестов");
                return;
            }

            System.out.println("\nВыберите тест:");
            for (Test test : tests) {
                System.out.printf("ID: %d | %s (проходной балл: %d)\n",
                        test.getId(), test.getTitle(), test.getPassingScore());
            }

            System.out.print("Введите ID теста: ");
            int testId = scanner.nextInt();
            scanner.nextLine();

            List<TestResult> results = teacherDAO.getTestResults(testId);
            if (results.isEmpty()) {
                System.out.println("По этому тесту пока нет результатов");
                return;
            }

            System.out.println("\nРезультаты теста:");
            System.out.println("--------------------------------------------------");
            System.out.printf("%-20s %-10s %-10s %-10s\n",
                    "Студент", "Баллы", "Статус", "Дата");
            System.out.println("--------------------------------------------------");

            for (TestResult result : results) {
                System.out.printf("%-20s %-10d %-10s %-10s\n",
                        result.getStudentName(),
                        result.getScore(),
                        result.isPassed() ? "Сдал" : "Не сдал",
                        result.getCompletedAt().toLocalDate());
            }

            double avgScore = results.stream()
                    .mapToInt(TestResult::getScore)
                    .average()
                    .orElse(0);

            long passedCount = results.stream()
                    .filter(TestResult::isPassed)
                    .count();

            System.out.println("\nСтатистика:");
            System.out.printf("Средний балл: %.1f\n", avgScore);
            System.out.printf("Сдали: %d из %d (%.1f%%)\n",
                    passedCount, results.size(),
                    (double) passedCount / results.size() * 100);
        } catch (SQLException e) {
            System.out.println("Ошибка при получении результатов: " + e.getMessage());
        }
    }

    public void reviewTextAnswers() {
        try {
            List<Course> courses = teacherDAO.getTeacherCourses(this.getId());
            if (courses.isEmpty()) {
                System.out.println("У вас нет курсов для проверки.");
                return;
            }

            System.out.println("\nВыберите курс:");
            for (Course course : courses) {
                System.out.printf("ID: %d | %s\n", course.getId(), course.getTitle());
            }
            System.out.print("Введите ID курса: ");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            List<Test> tests = teacherDAO.getTestsWithTextQuestions(courseId);
            if (tests.isEmpty()) {
                System.out.println("В этом курсе нет тестов с текстовыми вопросами.");
                return;
            }

            System.out.println("\nВыберите тест:");
            for (Test test : tests) {
                System.out.printf("ID: %d | %s\n", test.getId(), test.getTitle());
            }
            System.out.print("Введите ID теста: ");
            int testId = scanner.nextInt();
            scanner.nextLine();

            List<TextAnswerForReview> answersToReview = teacherDAO.getTextAnswersForReview(testId);
            if (answersToReview.isEmpty()) {
                System.out.println("Нет ответов для проверки.");
                return;
            }

            for (TextAnswerForReview answer : answersToReview) {
                System.out.println("\n=== Ответ на проверку ===");
                System.out.printf("Студент: %s\n", answer.getStudentName());
                System.out.printf("Вопрос: %s\n", answer.getQuestionText());
                System.out.printf("Ответ: %s\n", answer.getAnswer());
                System.out.printf("Макс. баллов: %d\n", answer.getMaxPoints());

                System.out.print("Введите оценку (0-" + answer.getMaxPoints() + "): ");
                int score = scanner.nextInt();
                scanner.nextLine();

                teacherDAO.saveTextAnswerScore(answer.getAnswerId(), score, this.getId());
                System.out.println("Оценка сохранена!");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при проверке ответов: " + e.getMessage());
        }
    }
}