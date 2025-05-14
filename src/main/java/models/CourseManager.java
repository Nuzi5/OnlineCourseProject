package models;

import dao.ManagerDAO;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CourseManager extends User {
    private final Scanner scanner;
    private final ManagerDAO managerDAO;

    public CourseManager(int id, String username, String password,
                         String email, String fullName, Connection connection) {
        super(id, username, password, email, fullName, "MANAGER");
        this.scanner = new Scanner(System.in);
        this.managerDAO = new ManagerDAO(connection);
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n=== МЕНЮ МЕНЕДЖЕРА КУРСОВ ===");
            System.out.println("1. Управление расписанием");
            System.out.println("2. Мониторинг курсов");
            System.out.println("3. Анализ успеваемости");
            System.out.println("4. Генерация отчетов");
            System.out.println("5. Управление сертификатами");
            System.out.println("6. Выход");
            System.out.print("Выберите опцию: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> manageSchedule();
                case 2 -> monitorCourses();
                case 3 -> analyzePerformance();
                case 4 -> generateReports();
                case 5 -> manageCertificates();
                case 6 -> {
                    System.out.println("Выход из системы...");
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private void manageSchedule() {
        System.out.println("\n--- Управление расписанием ---");
        System.out.println("1. Просмотр расписания");
        System.out.println("2. Добавить событие");
        System.out.println("3. Удалить событие");
        System.out.print("Выберите действие: ");

        int action = scanner.nextInt();
        scanner.nextLine();

        switch (action) {
            case 1 -> viewSchedule();
            case 2 -> addScheduleEvent();
            case 3 -> deleteScheduleEvent();
            default -> System.out.println("Неверный выбор!");
        }
    }

    private void viewSchedule() {
        try {
            System.out.print("Введите ID курса: ");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            List<ScheduleEvent> events = managerDAO.getScheduleEvents(courseId);
            System.out.println("\nРасписание курса:");
            for (ScheduleEvent event : events) {
                System.out.printf("ID: %d | %s | Тип: %s | Время: %s\n",
                        event.getId(),
                        event.getTitle(),
                        event.getEventType(),
                        event.getEventTime());
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении расписания: " + e.getMessage());
        }
    }

    private void addScheduleEvent() {
        try {
            System.out.print("Введите ID курса: ");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Тип события (LECTURE/PRACTICE/EXAM): ");
            String eventType = scanner.nextLine();

            System.out.print("Название события: ");
            String title = scanner.nextLine();

            System.out.print("Дата и время (гггг-мм-дд чч:мм): ");
            LocalDateTime eventTime = LocalDateTime.parse(scanner.nextLine(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            if (managerDAO.addScheduleEvent(courseId, title, eventType, eventTime, this.getId())) {
                System.out.println("Событие успешно добавлено!");
            } else {
                System.out.println("Не удалось добавить событие");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении события: " + e.getMessage());
        }
    }

    private void deleteScheduleEvent() {
        try {
            System.out.print("Введите ID события для удаления: ");
            int eventId = scanner.nextInt();
            scanner.nextLine();

            if (managerDAO.deleteScheduleEvent(eventId)) {
                System.out.println("Событие успешно удалено!");
            } else {
                System.out.println("Не удалось удалить событие");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении события: " + e.getMessage());
        }
    }

    private void monitorCourses() {
        try {
            List<CourseStats> stats = managerDAO.getCourseStatistics();
            System.out.println("\nСтатистика курсов:");
            System.out.println("ID | Название | Количество студентов");
            for (CourseStats stat : stats) {
                System.out.printf("%d | %s | %d\n",
                        stat.getCourseId(),
                        stat.getCourseTitle(),
                        stat.getStudentCount());
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении статистики: " + e.getMessage());
        }
    }

    private void analyzePerformance() {
        try {
            System.out.print("Введите ID курса для анализа: ");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            List<StudentProgress> progress = managerDAO.getStudentProgress(courseId);
            System.out.println("\nУспеваемость студентов:");
            System.out.println("ID | Студент | Средний балл");
            for (StudentProgress p : progress) {
                System.out.printf("%d | %s | %.2f\n",
                        p.getStudentId(),
                        p.getStudentName(),
                        p.getAverageScore());
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при анализе успеваемости: " + e.getMessage());
        }
    }

    private void generateReports() {
        System.out.println("\n*** Генерация отчетов ***");
        System.out.println("1. Отчет по курсу");
        System.out.println("2. Общий отчет по платформе");
        System.out.print("Выберите тип отчета: ");

        int reportType = scanner.nextInt();
        scanner.nextLine();

        try {
            if (reportType == 1) {
                System.out.print("Введите ID курса: ");
                int courseId = scanner.nextInt();
                scanner.nextLine();

                CourseReport report = managerDAO.generateCourseReport(courseId);
                if (report != null) {
                    System.out.println("\nОтчет по курсу:");
                    System.out.println("Название: " + report.getCourseTitle());
                    System.out.println("Количество студентов: " + report.getStudentCount());
                    System.out.printf("Средний балл: %.2f\n", report.getAverageScore());
                }
            } else if (reportType == 2) {
                PlatformReport report = managerDAO.generatePlatformReport();
                if (report != null) {
                    System.out.println("\nОбщий отчет по платформе:");
                    System.out.println("Пользователей: " + report.getUserCount());
                    System.out.println("Курсов: " + report.getCourseCount());
                    System.out.println("Записей на курсы: " + report.getEnrollmentCount());
                    System.out.println("Выданных сертификатов: " + report.getCertificateCount());
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при генерации отчета: " + e.getMessage());
        }
    }

    private void manageCertificates() {
        System.out.println("\n*** Управление сертификатами ***");
        System.out.println("1. Просмотреть выданные сертификаты");
        System.out.println("2. Выдать новый сертификат");
        System.out.print("Выберите действие: ");

        int action = scanner.nextInt();
        scanner.nextLine();

        try {
            if (action == 1) {
                System.out.print("Введите ID курса: ");
                int courseId = scanner.nextInt();
                scanner.nextLine();

                List<Certificate> certificates = managerDAO.getCourseCertificates(courseId);
                System.out.println("\nВыданные сертификаты:");
                System.out.println("ID | Студент | Дата выдачи | Оценка");
                for (Certificate cert : certificates) {
                    System.out.printf("%d | %s | %s | %d\n",
                            cert.getId(),
                            cert.getStudentName(),
                            cert.getIssueDate(),
                            cert.getFinalScore());
                }
            } else if (action == 2) {
                System.out.print("Введите ID студента: ");
                int studentId = scanner.nextInt();
                System.out.print("Введите ID курса: ");
                int courseId = scanner.nextInt();
                System.out.print("Введите итоговую оценку: ");
                int finalScore = scanner.nextInt();
                scanner.nextLine();

                if (managerDAO.issueCertificate(studentId, courseId, finalScore)) {
                    System.out.println("Сертификат успешно выдан!");
                } else {
                    System.out.println("Не удалось выдать сертификат");
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с сертификатами: " + e.getMessage());
        }
    }
}