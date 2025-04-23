package models;

public class CourseManager extends User {
    public CourseManager(int id, String username, String password, String email, String fullName) {
        super(id, username, password, email, fullName, "MANAGER");
    }

    public CourseManager(String username, String password, String email, String fullName) {
        super(username, password, email, fullName, "MANAGER");
    }

    @Override
    public void showMenu() {
        System.out.println("\n=== МЕНЮ МЕНЕДЖЕРА КУРСОВ ===");
        System.out.println("1. Управление расписанием");
        System.out.println("2. Мониторинг курсов");
        System.out.println("3. Анализ успеваемости студентов");
        System.out.println("4. Генерация отчетов");
        System.out.println("5. Управление сертификатами");
        System.out.println("6. Выход из системы");
        System.out.print("Выберите опцию: ");
    }

////    public void createScheduleEvent(int courseId) {
//        System.out.println("\n=== СОЗДАНИЕ СОБЫТИЯ В РАСПИСАНИИ ===");
//
//        System.out.print("Тип события (LECTURE/PRACTICE/WEBINAR/EXAM): ");
//        String eventType = scanner.nextLine();
//
//        System.out.print("Название события: ");
//        String title = scanner.nextLine();
//
//        System.out.print("Описание: ");
//        String description = scanner.nextLine();
//
//        System.out.print("Дата и время (гггг-мм-дд чч:мм): ");
//        LocalDateTime eventTime = LocalDateTime.parse(scanner.nextLine(), dtf);
//
//        System.out.print("ID преподавателя: ");
//        int teacherId = Integer.parseInt(scanner.nextLine());
//
//        ScheduleEvent event = new ScheduleEvent(0, courseId, title, description,
//                eventType, eventTime, teacherId);
//
//        if (scheduleDAO.createEvent(event)) {
//            System.out.println("Событие успешно создано!");
//        } else {
//            System.out.println("Ошибка при создании события!");
//        }
//    }

////    public void monitorCourseActivity(int courseId) {
//        Course course = courseDAO.getCourseById(courseId);
//        if (course == null) {
//            System.out.println("Курс с ID " + courseId + " не найден!");
//            return;
//        }
//
//        System.out.println("\n=== АКТИВНОСТЬ КУРСА: " + course.getTitle() + " ===");
//
//        // Статистика по студентам
//        int totalStudents = enrollmentDAO.getCourseStudents(courseId).size();
//        int activeStudents = enrollmentDAO.getActiveStudentsCount(courseId);
//        System.out.println("Студентов: " + activeStudents + "/" + totalStudents + " активны");
//
//        // Последние события
//        System.out.println("\nПоследние события курса:");
//        List<ScheduleEvent> recentEvents = scheduleDAO.getRecentCourseEvents(courseId, 5);
//        recentEvents.forEach(e -> System.out.println(
//                "[" + e.getEventTime().format(dtf) + "] " + e.getTitle() + " (" + e.getEventType() + ")"
//        ));
//
//        // Прогресс студентов
//        System.out.println("\nСредний прогресс: " + enrollmentDAO.getAverageCourseProgress(courseId) + "%");
//    }

////    public void generateStudentProgressReport(int courseId) {
//        Course course = courseDAO.getCourseById(courseId);
//        if (course == null) {
//            System.out.println("Курс с ID " + courseId + " не найден!");
//            return;
//        }
//
//        System.out.println("\n=== ОТЧЕТ ПО УСПЕВАЕМОСТИ: " + course.getTitle() + " ===");
//
//        List<User> students = enrollmentDAO.getCourseStudents(courseId);
//        if (students.isEmpty()) {
//            System.out.println("На курсе нет студентов!");
//            return;
//        }
//
//        System.out.printf("%-20s %-10s %-15s %-10s%n",
//                "Студент", "Прогресс", "Последняя активность", "Задания");
//        System.out.println("--------------------------------------------------");
//
//        for (User student : students) {
//            int progress = enrollmentDAO.getCourseProgress(student.getId(), courseId);
//            LocalDateTime lastActive = enrollmentDAO.getLastActivityDate(student.getId(), courseId);
//            int assignmentsDone = assignmentDAO.getCompletedAssignmentsCount(student.getId(), courseId);
//
//            System.out.printf("%-20s %-10d %-15s %-10d%n",
//                    student.getFullName(),
//                    progress,
//                    lastActive != null ? lastActive.toLocalDate().toString() : "нет данных",
//                    assignmentsDone
//            );
//        }
//    }

////    public void issueCertificate(int studentId, int courseId) {
//        if (!enrollmentDAO.isCourseCompleted(studentId, courseId)) {
//            System.out.println("Студент не завершил курс!");
//            return;
//        }
//
//        if (certificateDAO.hasCertificate(studentId, courseId)) {
//            System.out.println("Сертификат уже выдан этому студенту!");
//            return;
//        }
//
//        String certNumber = "CERT-" + System.currentTimeMillis();
//        Certificate cert = new Certificate(0, studentId, courseId, LocalDateTime.now(), certNumber);
//
//        if (certificateDAO.issueCertificate(cert)) {
//            System.out.println("Сертификат успешно выдан! Номер: " + certNumber);
//        } else {
//            System.out.println("Ошибка при выдаче сертификата!");
//        }
//    }

////    public void reviewAssignmentResults(int courseId) {
//        System.out.println("\n=== РЕЗУЛЬТАТЫ ЗАДАНИЙ ===");
//
//        List<Assignment> assignments = assignmentDAO.getCourseAssignments(courseId);
//        if (assignments.isEmpty()) {
//            System.out.println("На курсе нет заданий!");
//            return;
//        }
//
//        for (Assignment assignment : assignments) {
//            System.out.println("\nЗадание: " + assignment.getTitle());
//            System.out.println("Средний балл: " + assignmentDAO.getAverageScore(assignment.getId()));
//
//            List<AssignmentSubmission> topSubmissions = assignmentDAO.getTopSubmissions(assignment.getId(), 3);
//            if (!topSubmissions.isEmpty()) {
//                System.out.println("Лучшие работы:");
//                for (AssignmentSubmission sub : topSubmissions) {
//                    User student = new UserDAO().getUserById(sub.getStudentId());
//                    System.out.printf("- %s: %d/%d (%s)%n",
//                            student.getFullName(),
//                            sub.getScore(),
//                            assignment.getMaxScore(),
//                            sub.getFeedback() != null ? sub.getFeedback() : "без комментария"
//                    );
//                }
//            }
//        }
//    }
}