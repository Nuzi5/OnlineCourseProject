package models;

import dao.StudentDAO;
import models.additional.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class Student extends User {
    private final Scanner scanner;
    private final StudentDAO studentDAO;

    public Student(int id, String username, String password,
                   String email, String fullName, Connection connection) {
        super(id, username, password, email, fullName, "STUDENT", true);
        this.scanner = new Scanner(System.in);
        this.studentDAO = new StudentDAO(connection);
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n*** МЕНЮ СТУДЕНТА ***");
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
                case 1 -> viewAvailableCourses();
                case 2 -> viewMyCourses();
                case 3 -> viewAssignmentsAndTests();
                case 4 -> viewWebinarSchedule();
                case 5 -> viewProgress();
                case 6 -> viewMyCertificates();
                case 7 -> {
                    System.out.println("Выход из системы...\n\nРабота системы завершена. До свидания!");
                    System.exit(0);
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private void viewAvailableCourses() {
        try {
            List<CourseWithTeacher> courses = studentDAO.getAvailableCourses(this.getId());
            System.out.println("\nДоступные курсы:");
            for (CourseWithTeacher course : courses) {
                System.out.printf("ID: %d | Название: %s | Описание: %s | Преподаватель: %s\n",
                        course.getId(),
                        course.getTitle(),
                        course.getDescription(),
                        course.getTeacherName() != null ? course.getTeacherName() : "Не назначен");
            }

            System.out.print("\nВведите ID курса для записи (0 - отмена): ");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            if (courseId != 0) {
                if (studentDAO.enrollInCourse(this.getId(), courseId)) {
                    System.out.println("Вы успешно записаны на курс!");
                } else {
                    System.out.println("Не удалось записаться на курс");
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении курсов: " + e.getMessage());
        }
    }

    private void viewMyCourses() {
        try {
            List<Course> courses = studentDAO.getStudentCourses(this.getId());
            System.out.println("\nМои курсы:");
            for (Course course : courses) {
                System.out.printf("ID: %d | Название: %s | Описание: %s\n",
                        course.getId(),
                        course.getTitle(),
                        course.getDescription());
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении курсов: " + e.getMessage());
        }
    }

    private void viewAssignmentsAndTests() {
        System.out.println("\n*** Задания и тесты ***");
        System.out.println("1. Просмотреть задания");
        System.out.println("2. Просмотреть тесты");
        System.out.println("3. Назад в меню студента");
        System.out.print("Выберите опцию: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice){
            case 1 -> viewAssignments();
            case 2 -> viewTests();
            case 3 -> {return;}
            default -> System.out.println("Неверный выбор!");
        }
    }

    private void viewAssignments() {
        try {
            viewMyCourses();
            System.out.print("Введите ID курса: ");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            List<Assignment> assignments = studentDAO.getCourseAssignments(this.getId(), courseId);
            System.out.println("\nМои задания:");
            for (Assignment assignment : assignments) {
                String status;
                if (assignment.getSubmittedAt() == null) {
                    status = assignment.getDeadline().isBefore(LocalDateTime.now()) ?
                            "Просрочено" : "Не сдано";
                } else {
                    status = String.format("Оценка: %d/%d",
                            assignment.getScore() != null ? assignment.getScore() : 0,
                            assignment.getMaxScore());
                }

                System.out.printf("ID: %d | %s | Дедлайн: %s | %s\n",
                        assignment.getId(),
                        assignment.getTitle(),
                        assignment.getDeadline(),
                        status);
            }

            System.out.print("\nВведите ID задания для сдачи (0 - отмена): ");
            int assignmentId = scanner.nextInt();
            scanner.nextLine();

            if (assignmentId != 0) {
                System.out.print("Введите ваш ответ (текст задания): ");
                String answer = scanner.nextLine();

                if (studentDAO.submitAssignment(assignmentId, this.getId(), answer)) {
                    System.out.println("Задание успешно отправлено!");
                } else {
                    System.out.println("Не удалось отправить задание");
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с заданиями: " + e.getMessage());
        }
    }

    private void viewTests() {
        try {
            viewMyCourses();
            System.out.print("Введите ID курса: ");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            List<Test> tests = studentDAO.getCourseTests(this.getId(), courseId);
            System.out.println("\nМои тесты:");
            for (Test test : tests) {
                String status = test.getScore() == null ?
                        "Не пройден" :
                        String.format("Оценка: %d/%d", test.getScore(), test.getPassingScore());

                System.out.printf("ID: %d | %s | %s\n",
                        test.getId(),
                        test.getTitle(),
                        status);
            }

            System.out.print("\nВведите ID теста для прохождения (0 - отмена): ");
            int testId = scanner.nextInt();
            scanner.nextLine();

            if (testId != 0) {
                takeTest(testId);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с тестами: " + e.getMessage());
        }
    }

    private void takeTest(int testId) {
        try {
            Test test = studentDAO.getTestById(testId);
            if (test == null) {
                System.out.println("Тест не найден!");
                return;
            }

            System.out.printf("\n*** Начало теста: %s ***\n", test.getTitle());
            System.out.printf("Лимит времени: %d мин. | Проходной балл: %d\n\n",
                    test.getTimeLimit(), test.getPassingScore());

            List<TestQuestion> questions = studentDAO.getTestQuestions(testId);
            if (questions.isEmpty()) {
                System.out.println("Тест не содержит вопросов!");
                return;
            }

            int totalScore = 0;
            Map<Integer, String> studentAnswers = new HashMap<>();

            for (TestQuestion question : questions) {
                System.out.printf("Вопрос #%d (%d баллов):\n%s\n",
                        question.getId(),
                        question.getPoints(),
                        question.getQuestionText());

                if (!"text".equals(question.getQuestionType())) {
                    List<AnswerOption> options = studentDAO.getAnswerOptions(question.getId());
                    for (AnswerOption option : options) {
                        System.out.printf("%d) %s\n", option.getId(), option.getOptionText());
                    }
                }

                System.out.print("Ваш ответ: ");
                String answer = scanner.nextLine();
                studentAnswers.put(question.getId(), answer);

                if (!"text".equals(question.getQuestionType())) {
                    boolean isCorrect = studentDAO.checkAnswer(question.getId(), answer);
                    if (isCorrect) {
                        totalScore += question.getPoints();
                        System.out.println("✓ Верно!");
                    } else {
                        System.out.println("✗ Неверно!");
                    }
                }
                System.out.println();
            }

            if (questions.stream().anyMatch(q -> "text".equals(q.getQuestionType()))) {
                System.out.println("\nТекстовые вопросы будут проверены преподавателем позже.");
                System.out.printf("Предварительный балл: %d/%d\n",
                        totalScore, test.getPassingScore());
            }

            if (studentDAO.saveTestResult(
                    this.getId(),
                    testId,
                    totalScore,
                    test.getPassingScore(),
                    studentAnswers)) {

                System.out.println("\n*** Тест завершен ***");
                System.out.printf("Итоговый балл: %d/%d\n", totalScore, test.getPassingScore());
                System.out.println("Результаты сохранены.");
            } else {
                System.out.println("Ошибка при сохранении результатов теста!");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при прохождении теста: " + e.getMessage());
        }
    }

    private void viewWebinarSchedule() {
        try {
            List<Webinar> webinars = studentDAO.getUpcomingWebinars(this.getId());
            System.out.println("\nПредстоящие вебинары:");
            for (Webinar webinar : webinars) {
                System.out.printf("ID: %d | Курс: %s | %s | Время: %s\n",
                        webinar.getId(),
                        webinar.getCourseTitle(),
                        webinar.getTitle(),
                        webinar.getScheduledAt());
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении расписания: " + e.getMessage());
        }
    }

    private void viewProgress() {
        try {
            java.util.Map<String, Double> progress = studentDAO.getStudentProgress(this.getId());
            System.out.println("\nМой прогресс:");
            for (Map.Entry<String, Double> entry : progress.entrySet()) {
                System.out.printf("%s: %.1f%%\n", entry.getKey(), entry.getValue());
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении прогресса: " + e.getMessage());
        }
    }

    private void viewMyCertificates() {
        try {
            List<Certificate> certificates = studentDAO.getStudentCertificates(this.getId());
            System.out.println("\nМои сертификаты:");
            for (Certificate cert : certificates) {
                System.out.printf("ID: %d | Дата выдачи: %s | Оценка: %d\n",
                        cert.getId(),
                        cert.getIssueDate(),
                        cert.getFinalScore());
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении сертификатов: " + e.getMessage());
        }
    }
}
