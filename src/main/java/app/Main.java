package app;

import models.*;
import dao.*;
import db.DatabaseSetup;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    private static UserDAO userDAO;
    private static CourseDAO courseDAO;
    private static WebinarDAO webinarDAO;
    private static AssignmentDAO assignmentDAO;
    private static TestDAO testDAO;
    private static EnrollmentDAO enrollmentDAO;
    private static CourseMaterialDAO courseMaterialDAO;
    private static ScheduleDAO scheduleDAO;
    private static CertificateDAO certificateDAO;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        initializeDatabaseAndDAOs();
        showMainMenu();
    }

    private static void initializeDatabaseAndDAOs() {
        DatabaseSetup.initDatabase();

        userDAO = new UserDAO();
        courseDAO = new CourseDAO();
        webinarDAO = new WebinarDAO();
        assignmentDAO = new AssignmentDAO();
        testDAO = new TestDAO();
        enrollmentDAO = new EnrollmentDAO();
        courseMaterialDAO = new CourseMaterialDAO();
        scheduleDAO = new ScheduleDAO();
        certificateDAO = new CertificateDAO();
    }

    private static void showMainMenu() {
        boolean isValidInput = false;

        while (!isValidInput) {
            try {
                System.out.println("\n=== ОБРАЗОВАТЕЛЬНАЯ ПЛАТФОРМА ===");
                System.out.println("1. Вход в систему");
                System.out.println("2. Регистрация (студент)");
                System.out.println("3. Выход");
                System.out.print("Выберите опцию: ");

                int choice = readIntInput();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> {
                        login();
                        isValidInput = true;
                    }
                    case 2 -> {
                        registerStudent();
                        isValidInput = true;
                    }
                    case 3 -> {
                        exitSystem();
                        isValidInput = true;
                    }
                    default -> System.out.println("Неверный выбор! Попробуйте снова.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: введите число!");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Произошла ошибка: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    private static void exitSystem() {
        System.out.println("\nРабота системы завершена. До свидания!");
        System.exit(0);
    }

    private static int readIntInput() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: введите число!");
                scanner.nextLine();
            }
        }
    }

    private static void login() {
        System.out.print("\nВведите логин: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        currentUser = userDAO.authenticateUser(username, password);

        if (currentUser != null) {
            System.out.printf("\nДобро пожаловать, %s (%s)!\n",
                    currentUser.getFullName(), currentUser.getRole());
            showRoleMenu();
        } else {
            System.out.println("Ошибка входа! Проверьте логин и пароль.");
        }
    }

    private static void registerStudent() {
        System.out.println("\n=== РЕГИСТРАЦИЯ СТУДЕНТА ===");
        System.out.print("ФИО: ");
        String fullName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Логин: ");
        String username = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        Student newStudent = new Student(username, password, email, fullName);
        if (userDAO.createUser(newStudent)) {
            System.out.println("Регистрация прошла успешно!");
        } else {
            System.out.println("Ошибка регистрации! Возможно, логин или email уже заняты.");
        }
    }

    private static void showRoleMenu() {
        while (currentUser != null) {
            currentUser.showMenu();
            int choice = readIntInput();

            if (currentUser instanceof Administrator) {
                handleAdminMenu(choice);
            } else if (currentUser instanceof Teacher) {
                handleTeacherMenu(choice);
            } else if (currentUser instanceof Student) {
                handleStudentMenu(choice);
            } else if (currentUser instanceof CourseManager) {
                handleManagerMenu(choice);
            }
        }
    }

    private static void handleAdminMenu(int choice) {
        switch (choice) {
            case 1 -> manageCourses();
            case 2 -> manageUsers();
            case 3 -> generateSystemReport();
            case 4 -> currentUser = null;
            default -> System.out.println("Неверный выбор!");
        }
    }

    private static void manageCourses() {
        System.out.println("\n=== УПРАВЛЕНИЕ КУРСАМИ ===");
        System.out.println("1. Создать курс");
        System.out.println("2. Редактировать курс");
        System.out.println("3. Деактивировать курс");
        System.out.println("4. Список всех курсов");
        System.out.print("Выберите опцию: ");

        int choice = readIntInput();

        switch (choice) {
            case 1 -> createCourse();
            case 2 -> editCourse();
            case 3 -> deactivateCourse();
            case 4 -> listAllCourses();
            default -> System.out.println("Неверный выбор!");
        }
    }

    private static void createCourse() {
        System.out.print("\nНазвание курса: ");
        String title = scanner.nextLine();
        System.out.print("Описание: ");
        String description = scanner.nextLine();

        Course course = new Course(title, description, currentUser.getId(), true);
        if (courseDAO.createCourse(course)) {
            System.out.println("Курс успешно создан!");
        } else {
            System.out.println("Ошибка при создании курса!");
        }
    }

    private static void editCourse() {
        System.out.print("\nВведите ID курса: ");
        int courseId = readIntInput();
        scanner.nextLine();

        Course course = courseDAO.getCourseById(courseId);
        if (course == null) {
            System.out.println("Курс не найден!");
            return;
        }

        System.out.print("Новое название (текущее: " + course.getTitle() + "): ");
        String title = scanner.nextLine();
        if (!title.isEmpty()) course.setTitle(title);

        System.out.print("Новое описание: ");
        String description = scanner.nextLine();
        if (!description.isEmpty()) course.setDescription(description);

        if (courseDAO.updateCourse(course)) {
            System.out.println("Курс обновлен успешно!");
        } else {
            System.out.println("Ошибка при обновлении курса!");
        }
    }

    private static void deactivateCourse() {
        System.out.print("\nВведите ID курса для деактивации: ");
        int courseId = readIntInput();

        if (courseDAO.deactivateCourse(courseId)) {
            System.out.println("Курс деактивирован успешно!");
        } else {
            System.out.println("Ошибка при деактивации курса!");
        }
    }

    private static void listAllCourses() {
        List<Course> courses = courseDAO.getAllCourses();
        System.out.println("\n=== СПИСОК КУРСОВ ===");
        courses.forEach(course -> System.out.printf("%d: %s (%s)\n",
                course.getId(), course.getTitle(), course.isActive() ? "Активен" : "Неактивен"));
    }

    private static void manageUsers() {
        System.out.println("\n=== УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯМИ ===");
        System.out.println("1. Список всех пользователей");
        System.out.println("2. Создать пользователя");
        System.out.println("3. Редактировать пользователя");
        System.out.println("4. Удалить пользователя");
        System.out.print("Выберите опцию: ");

        int choice = readIntInput();

        switch (choice) {
            case 1 -> listAllUsers();
            case 2 -> createUser();
            case 3 -> editUser();
            case 4 -> deleteUser(currentUser.getId());
            default -> System.out.println("Неверный выбор!");
        }
    }

    private static void listAllUsers() {
        List<User> users = userDAO.getAllUsers();
        System.out.println("\n=== СПИСОК ПОЛЬЗОВАТЕЛЕЙ ===");
        users.forEach(user -> System.out.printf("%d: %s (%s)\n",
                user.getId(), user.getFullName(), user.getRole()));
    }

    private static void createUser() {
        System.out.print("\nРоль (ADMIN/TEACHER/STUDENT/MANAGER): ");
        String role = scanner.nextLine().toUpperCase();

        System.out.print("ФИО: ");
        String fullName = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Логин: ");
        String username = scanner.nextLine();

        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        User newUser = switch (role) {
            case "ADMIN" -> new Administrator(username, password, email, fullName);
            case "TEACHER" -> new Teacher(username, password, email, fullName);
            case "STUDENT" -> new Student(username, password, email, fullName);
            case "MANAGER" -> new CourseManager(username, password, email, fullName);
            default -> null;
        };

        if (newUser != null && userDAO.createUser(newUser)) {
            System.out.println("Пользователь создан успешно!");
        } else {
            System.out.println("Ошибка при создании пользователя!");
        }
    }

    private static void editUser() {
        System.out.print("\nВведите ID пользователя: ");
        int userId = readIntInput();
        scanner.nextLine();

        User user = userDAO.getUserById(userId);
        if (user == null) {
            System.out.println("Пользователь не найден!");
            return;
        }

        System.out.print("Новое ФИО (текущее: " + user.getFullName() + "): ");
        String fullName = scanner.nextLine();
        if (!fullName.isEmpty()) user.setFullName(fullName);

        System.out.print("Новый email (текущий: " + user.getEmail() + "): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) user.setEmail(email);

        if (userDAO.updateUser(user)) {
            System.out.println("Пользователь обновлен успешно!");
        } else {
            System.out.println("Ошибка при обновлении пользователя!");
        }
    }
    private static void deleteUser(int userId) {
        System.out.print("Вы уверены, что хотите удалить пользователя ID " + userId + "? (y/n): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("y")) {
            if (userDAO.deleteUser(userId)) {
                System.out.println("Пользователь успешно удален!");
            } else {
                System.out.println("Ошибка при удалении пользователя!");
            }
        }
    }

    private static void generateSystemReport() {
        System.out.println("\n=== СИСТЕМНЫЙ ОТЧЕТ ===");
        int userCount = userDAO.getAllUsers().size();
        int courseCount = courseDAO.getAllCourses().size();

        System.out.printf("Всего пользователей: %d\n", userCount);
        System.out.printf("Всего курсов: %d\n", courseCount);
        System.out.println("Детальная статистика в разработке");
    }

    private static void handleTeacherMenu(int choice) {
        Teacher teacher = (Teacher) currentUser;
        switch (choice) {
            case 1 -> showTeacherCourses(teacher);
            case 2 -> createCourseMaterial();
            case 3 -> createAssignment();
            case 4 -> createTest();
            case 5 -> gradeAssignments(teacher);
            case 6 -> manageWebinars(teacher);
            case 7 -> currentUser = null;
            default -> System.out.println("Неверный выбор!");
        }
    }

    private static void showTeacherCourses(Teacher teacher) {
        List<Course> courses = courseDAO.getCoursesByTeacher(teacher.getId());
        System.out.println("\n=== МОИ КУРСЫ ===");
        courses.forEach(course -> System.out.printf("%d: %s (%s)\n",
                course.getId(), course.getTitle(), course.isActive() ? "Активен" : "Неактивен"));
    }

    private static void createCourseMaterial() {
        System.out.print("\nID курса: ");
        int courseId = readIntInput();
        scanner.nextLine();

        System.out.print("Тип материала (LECTURE/VIDEO/TEST): ");
        String materialType = scanner.nextLine();

        System.out.print("Название: ");
        String title = scanner.nextLine();

        System.out.print("Содержание: ");
        String content = scanner.nextLine();

        CourseMaterial material = new CourseMaterial(0, courseId, title, content, materialType);
        if (courseMaterialDAO.createMaterial(material)) {
            System.out.println("Материал успешно создан!");
        } else {
            System.out.println("Ошибка при создании материала!");
        }
    }

    private static void createAssignment() {
        System.out.print("\nID курса: ");
        int courseId = readIntInput();
        scanner.nextLine();

        System.out.print("Название задания: ");
        String title = scanner.nextLine();

        System.out.print("Описание: ");
        String description = scanner.nextLine();

        System.out.print("Максимальный балл: ");
        int maxScore = readIntInput();
        scanner.nextLine();

        System.out.print("Срок выполнения (гггг-мм-дд чч:мм): ");
        LocalDateTime deadline = LocalDateTime.parse(scanner.nextLine(), dtf);

        Assignment assignment = new Assignment(0, courseId, title, description, deadline, maxScore);
        if (assignmentDAO.createAssignment(assignment)) {
            System.out.println("Задание успешно создано!");
        } else {
            System.out.println("Ошибка при создании задания!");
        }
    }

    private static void createTest() {
        System.out.println("\n=== СОЗДАНИЕ ТЕСТА ===");
        System.out.print("ID курса: ");
        int courseId = readIntInput();
        scanner.nextLine();

        System.out.print("Название теста: ");
        String title = scanner.nextLine();

        System.out.print("Описание: ");
        String description = scanner.nextLine();

        System.out.print("Лимит времени (мин): ");
        int timeLimit = readIntInput();

        System.out.print("Проходной балл: ");
        int passingScore = readIntInput();

        Test test = new Test(0, courseId, title, description, timeLimit, passingScore);
        if (testDAO.createTest(test)) {
            System.out.println("Тест создан! ID: " + test.getId());
            addQuestionsToTest(test.getId());
        } else {
            System.out.println("Ошибка при создании теста!");
        }
    }

    private static void addQuestionsToTest(int testId) {
        while (true) {
            System.out.println("\nДобавить вопрос? (y/n)");
            if (!scanner.nextLine().equalsIgnoreCase("y")) break;

            System.out.print("Текст вопроса: ");
            String questionText = scanner.nextLine();

            System.out.print("Тип вопроса (SINGLE/MULTIPLE/TEXT): ");
            String questionType = scanner.nextLine();

            System.out.print("Баллы за вопрос: ");
            int points = readIntInput();

            TestQuestion question = new TestQuestion(0, testId, questionText, questionType, points);
            if (testDAO.addQuestionToTest(question)) {
                System.out.println("Вопрос добавлен! ID: " + question.getId());
                if (questionType.equals("SINGLE") || questionType.equals("MULTIPLE")) {
                    addAnswerOptions(question.getId());
                }
            } else {
                System.out.println("Ошибка при добавлении вопроса!");
            }
        }
    }

    private static void addAnswerOptions(int questionId) {
        System.out.println("\nДобавление вариантов ответов:");
        do {
            System.out.print("Текст варианта: ");
            String optionText = scanner.nextLine();

            System.out.print("Это правильный ответ? (y/n): ");
            boolean isCorrect = scanner.nextLine().equalsIgnoreCase("y");

            AnswerOption option = new AnswerOption(0, questionId, optionText, isCorrect);
            if (testDAO.addAnswerOption(option)) {
                System.out.println("Вариант добавлен!");
            } else {
                System.out.println("Ошибка при добавлении варианта!");
            }

            System.out.println("Добавить еще вариант? (y/n)");
        } while (scanner.nextLine().equalsIgnoreCase("y"));
    }
    private static void gradeAssignments(Teacher teacher) {
        System.out.println("\n=== ПРОВЕРКА ЗАДАНИЙ ===");
        List<Course> courses = courseDAO.getCoursesByTeacher(teacher.getId());

        if (courses.isEmpty()) {
            System.out.println("У вас нет курсов для проверки.");
            return;
        }

        System.out.println("Ваши курсы:");
        courses.forEach(c -> System.out.printf("%d - %s\n", c.getId(), c.getTitle()));

        System.out.print("Выберите ID курса: ");
        int courseId = readIntInput();

        List<Assignment> assignments = assignmentDAO.getCourseAssignments(courseId);
        if (assignments.isEmpty()) {
            System.out.println("В этом курсе нет заданий.");
            return;
        }

        System.out.println("Задания курса:");
        assignments.forEach(a -> System.out.printf("%d - %s (до %s)\n",
                a.getId(), a.getTitle(), a.getDeadline().format(DateTimeFormatter.ISO_LOCAL_DATE)));

        System.out.print("Выберите ID задания: ");
        int assignmentId = readIntInput();

        List<AssignmentSubmission> submissions = assignmentDAO.getAssignmentSubmissions(assignmentId);
        if (submissions.isEmpty()) {
            System.out.println("Нет работ для проверки.");
            return;
        }

        for (AssignmentSubmission submission : submissions) {
            System.out.printf("\nСтудент: %s\nРабота: %s\n",
                    userDAO.getUserById(submission.getStudentId()).getFullName(),
                    submission.getAnswer());

            System.out.print("Оценка (0-" + assignmentDAO.getAssignment(assignmentId).getMaxScore() + "): ");
            int score = readIntInput();

            System.out.print("Комментарий: ");
            String feedback = scanner.nextLine();

            assignmentDAO.gradeSubmission(submission.getId(), score, feedback);
            System.out.println("Оценка сохранена!");
        }
    }

    private static void manageWebinars(Teacher teacher) {
        System.out.println("\n=== УПРАВЛЕНИЕ ВЕБИНАРАМИ ===");
        System.out.println("1. Создать вебинар");
        System.out.println("2. Отметить проведенный вебинар");
        System.out.println("3. Список моих вебинаров");
        System.out.print("Выберите опцию: ");

        int choice = readIntInput();

        switch (choice) {
            case 1 -> createWebinar(teacher);
            case 2 -> markWebinarConducted(teacher);
            case 3 -> listTeacherWebinars(teacher);
            default -> System.out.println("Неверный выбор!");
        }
    }

    private static void createWebinar(Teacher teacher) {
        System.out.print("\nID курса: ");
        int courseId = readIntInput();
        scanner.nextLine(); // Очистка буфера

        System.out.print("Название вебинара: ");
        String title = scanner.nextLine();

        System.out.print("Описание: ");
        String description = scanner.nextLine();

        System.out.print("Дата и время (гггг-мм-дд чч:мм): ");
        LocalDateTime scheduledAt = LocalDateTime.parse(scanner.nextLine(), dtf);

        Webinar webinar = new Webinar(0, courseId, title, description, scheduledAt, teacher.getId(), false);
        if (webinarDAO.createWebinar(webinar)) {
            System.out.println("Вебинар успешно создан!");
        } else {
            System.out.println("Ошибка при создании вебинара!");
        }
    }

    private static void markWebinarConducted(Teacher teacher) {
        System.out.println("\n=== ВЕБИНАРЫ ===");
        List<Webinar> webinars = webinarDAO.getTeacherWebinars(teacher.getId(), false);

        if (webinars.isEmpty()) {
            System.out.println("Нет запланированных вебинаров.");
            return;
        }

        System.out.println("Запланированные вебинары:");
        webinars.forEach(w -> System.out.printf(
                "%d - %s (%s)\n",
                w.getId(), w.getTitle(), w.getScheduledAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        ));

        System.out.print("Введите ID проведенного вебинара: ");
        int webinarId = readIntInput();

        if (webinarDAO.markAsConducted(webinarId)) {
            System.out.println("Вебинар отмечен как проведенный!");
        } else {
            System.out.println("Ошибка при обновлении статуса!");
        }
    }

    private static void listTeacherWebinars(Teacher teacher) {
        System.out.println("\n=== МОИ ВЕБИНАРЫ ===");
        List<Webinar> webinars = webinarDAO.getTeacherWebinars(teacher.getId());

        if (webinars.isEmpty()) {
            System.out.println("У вас нет запланированных вебинаров.");
            return;
        }

        webinars.forEach(webinar -> {
            String status = webinar.isWasConducted() ? "Проведен" : "Запланирован";
            System.out.printf("%d - %s | %s | %s | Курс: %d\n",
                    webinar.getId(),
                    webinar.getTitle(),
                    webinar.getScheduledAt().format(dtf),
                    status,
                    webinar.getCourseId()
            );
        });
    }

    private static void handleStudentMenu(int choice) {
        Student student = (Student) currentUser;
        switch (choice) {
            case 1 -> showAvailableCourses();
            case 2 -> showMyCourses(student);
            case 3 -> showAssignmentsAndTests(student);
            case 4 -> showWebinarSchedule(student);
            case 5 -> showProgress(student);
            case 6 -> showCertificates(student);
            case 7 -> currentUser = null;
            default -> System.out.println("Неверный выбор!");
        }
    }

    private static void showAvailableCourses() {
        List<Course> courses = courseDAO.getActiveCourses();
        System.out.println("\n=== ДОСТУПНЫЕ КУРСЫ ===");
        courses.forEach(course -> System.out.printf("%d: %s\n", course.getId(), course.getTitle()));
    }

    private static void showMyCourses(Student student) {
        List<Course> courses = enrollmentDAO.getStudentCourses(student.getId());
        System.out.println("\n=== МОИ КУРСЫ ===");

        if (courses.isEmpty()) {
            System.out.println("Вы не записаны ни на один курс.");
            return;
        }

        courses.forEach(course -> {
            int progress = enrollmentDAO.getCourseProgress(student.getId(), course.getId());
            System.out.printf("%d: %s - %d%% завершено\n",
                    course.getId(), course.getTitle(), progress);
        });
    }


    private static void showAssignmentsAndTests(Student student) {
        System.out.println("\n=== ЗАДАНИЯ И ТЕСТЫ ===");
        List<Course> courses = enrollmentDAO.getStudentCourses(student.getId());

        if (courses.isEmpty()) {
            System.out.println("Нет активных курсов.");
            return;
        }

        courses.forEach(course -> {
            System.out.printf("\nКурс: %s\n", course.getTitle());

            System.out.println("  Задания:");
            List<Assignment> assignments = assignmentDAO.getCourseAssignments(course.getId());
            assignments.forEach(a -> {
                AssignmentSubmission submission = assignmentDAO.getStudentSubmission(
                        student.getId(), a.getId());

                String status = submission == null ? "Не сдано" :
                        "Оценка: " + submission.getScore() + "/" + a.getMaxScore();

                System.out.printf("  %d - %s (%s)\n", a.getId(), a.getTitle(), status);
            });

            System.out.println("  Тесты:");
            List<Test> tests = testDAO.getCourseTests(course.getId());
            tests.forEach(t -> {
                TestResult result = testDAO.getStudentResult(student.getId(), t.getId());
                String status = result == null ? "Не пройден" :
                        "Результат: " + result.getScore() + "/" + t.getPassingScore();

                System.out.printf("  %d - %s (%s)\n", t.getId(), t.getTitle(), status);
            });
        });

        System.out.print("\n1. Выполнить задание\n2. Пройти тест\n3. Назад\nВыберите: ");
        int choice = readIntInput();

        if (choice == 1) {
            submitAssignment(student);
        } else if (choice == 2) {
            takeTest(student);
        }
    }

    private static void submitAssignment(Student student) {
        System.out.print("\nВведите ID задания: ");
        int assignmentId = readIntInput();
        scanner.nextLine();

        Assignment assignment = assignmentDAO.getAssignment(assignmentId);
        if (assignment == null) {
            System.out.println("Задание не найдено!");
            return;
        }

        System.out.printf("Задание: %s\n", assignment.getTitle());
        System.out.println("Введите ваш ответ (многострочный, завершите точкой на новой строке):");

        StringBuilder answer = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals(".")) {
            answer.append(line).append("\n");
        }

        AssignmentSubmission submission = new AssignmentSubmission(
                0, assignmentId, student.getId(), answer.toString(), null, 0, null);

        if (assignmentDAO.submitAssignment(submission)) {
            System.out.println("Задание отправлено на проверку!");
        } else {
            System.out.println("Ошибка при отправке задания!");
        }
    }

    private static void takeTest(Student student) {
        System.out.print("\nВведите ID теста: ");
        int testId = readIntInput();
        scanner.nextLine();

        Test test = testDAO.getTest(testId);
        if (test == null) {
            System.out.println("Тест не найден!");
            return;
        }

        System.out.printf("\n=== ТЕСТ: %s ===\n", test.getTitle());
        System.out.printf("Время на выполнение: %d мин\n", test.getTimeLimit());
        System.out.printf("Проходной балл: %d\n", test.getPassingScore());

        List<TestQuestion> questions = testDAO.getTestQuestions(testId);
        int totalScore = 0;

        for (TestQuestion question : questions) {
            System.out.printf("\nВопрос (%d баллов): %s\n", question.getPoints(), question.getQuestionText());

            if (question.getQuestionType().equals("MULTIPLE") || question.getQuestionType().equals("SINGLE")) {
                List<AnswerOption> options = question.getAnswerOptions();
                for (int i = 0; i < options.size(); i++) {
                    System.out.printf("%d. %s\n", i+1, options.get(i).getOptionText());
                }

                System.out.print("Ваш ответ (номера через запятую): ");
                String[] answers = scanner.nextLine().split(",");

                int correctAnswers = 0;
                for (String answer : answers) {
                    try {
                        int optionIndex = Integer.parseInt(answer.trim()) - 1;
                        if (optionIndex >= 0 && optionIndex < options.size() && options.get(optionIndex).isCorrect()) {
                            correctAnswers++;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: '" + answer.trim() + "' не является числом. Этот ответ будет пропущен.");

                    }
                }

                if (question.getQuestionType().equals("SINGLE")) {
                    totalScore += (correctAnswers > 0) ? question.getPoints() : 0;
                } else {

                    long totalCorrect = options.stream().filter(AnswerOption::isCorrect).count();
                    if (totalCorrect > 0) {
                        totalScore += (int)(question.getPoints() * ((double)correctAnswers / totalCorrect));
                    }
                }
            } else {
                System.out.print("Ваш ответ: ");
                scanner.nextLine();
                totalScore += question.getPoints();
            }
        }

        System.out.printf("\nТест завершен! Ваш результат: %d/%d\n", totalScore, test.getPassingScore());
        testDAO.saveTestResult(student.getId(), testId, totalScore);
    }

    private static void showWebinarSchedule(Student student) {
        System.out.println("\n=== РАСПИСАНИЕ ВЕБИНАРОВ ===");
        List<Course> enrolledCourses = enrollmentDAO.getStudentCourses(student.getId());

        if (enrolledCourses.isEmpty()) {
            System.out.println("Вы не записаны ни на один курс.");
            return;
        }

        for (Course course : enrolledCourses) {
            System.out.println("\nКурс: " + course.getTitle());
            List<Webinar> webinars = webinarDAO.getCourseWebinars(course.getId());

            if (webinars.isEmpty()) {
                System.out.println("  Нет запланированных вебинаров");
            } else {
                for (Webinar webinar : webinars) {
                    System.out.printf("  %s - %s (%s)\n",
                            webinar.getScheduledAt().format(dtf),
                            webinar.getTitle(),
                            webinar.isWasConducted() ? "Проведен" : "Запланирован");
                }
            }
        }
    }

    private static void showProgress(Student student) {
        System.out.println("\n=== МОЙ ПРОГРЕСС ===");
        List<Course> courses = enrollmentDAO.getStudentCourses(student.getId());

        if (courses.isEmpty()) {
            System.out.println("Вы не записаны ни на один курс.");
            return;
        }

        courses.forEach(course -> {
            int progress = enrollmentDAO.getCourseProgress(student.getId(), course.getId());
            System.out.printf("Курс %d: %s - %d%% завершено\n",
                    course.getId(), course.getTitle(), progress);
        });
    }
    private static void showCertificates(Student student) {
        System.out.println("\n=== МОИ СЕРТИФИКАТЫ ===");
        List<Certificate> certificates = certificateDAO.getUserCertificates(student.getId());

        if (certificates.isEmpty()) {
            System.out.println("У вас пока нет сертификатов.");
            return;
        }

        certificates.forEach(cert -> {
            Course course = courseDAO.getCourseById(cert.getCourseId());
            System.out.printf("Сертификат №%s | Курс: %s | Выдан: %s\n",
                    cert.getCertificateNumber(),
                    course.getTitle(),
                    cert.getIssueDate().format(dtf)
            );
        });
    }

    private static void handleManagerMenu(int choice) {
        switch (choice) {
            case 1 -> manageSchedule();
            case 2 -> monitorCourses();
            case 3 -> analyzeStudentProgress();
            case 4 -> manageCertificates();
            case 5 -> currentUser = null;
            default -> System.out.println("Неверный выбор!");
        }
    }
    private static void manageSchedule() {
        System.out.println("\n=== УПРАВЛЕНИЕ РАСПИСАНИЕМ ===");
        System.out.println("1. Создать событие");
        System.out.println("2. Просмотреть расписание");
        System.out.print("Выберите: ");

        int choice = readIntInput();
        if (choice == 1) {
            createScheduleEvent();
        } else if (choice == 2) {
            viewSchedule();
        }
    }

    private static void createScheduleEvent() {
        System.out.print("\nID курса: ");
        int courseId = readIntInput();
        scanner.nextLine();

        System.out.print("Тип события (LECTURE/WEBINAR/DEADLINE): ");
        String eventType = scanner.nextLine();

        System.out.print("Название: ");
        String title = scanner.nextLine();

        System.out.print("Дата и время (гггг-мм-дд чч:мм): ");
        LocalDateTime eventDate = LocalDateTime.parse(scanner.nextLine(), dtf);

        ScheduleEvent event = new ScheduleEvent(0, courseId, title, eventType, eventDate, currentUser.getId());
        if (scheduleDAO.createEvent(event)) {
            System.out.println("Событие создано!");
        } else {
            System.out.println("Ошибка при создании события!");
        }
    }

    private static void viewSchedule() {
        System.out.println("\n=== РАСПИСАНИЕ КУРСОВ ===");
        List<ScheduleEvent> events = scheduleDAO.getAllUpcomingEvents();

        if (events.isEmpty()) {
            System.out.println("Нет предстоящих событий.");
            return;
        }

        events.forEach(event -> {
            Course course = courseDAO.getCourseById(event.getCourseId());
            System.out.printf("%s | %s | %s | %s\n",
                    event.getEventTime().format(dtf),
                    course.getTitle(),
                    event.getEventType(),
                    event.getTitle()
            );
        });
    }

    private static void monitorCourses() {
        System.out.println("\n=== МОНИТОРИНГ КУРСОВ ===");
        List<Course> courses = courseDAO.getAllCourses();

        courses.forEach(course -> {
            int studentsCount = enrollmentDAO.getActiveStudentsCount(course.getId());
            int assignmentsCount = assignmentDAO.getCourseAssignments(course.getId()).size();

            System.out.printf("Курс %d: %s | Студентов: %d | Заданий: %d | %s\n",
                    course.getId(),
                    course.getTitle(),
                    studentsCount,
                    assignmentsCount,
                    course.isActive() ? "Активен" : "Неактивен"
            );
        });
    }

    private static void analyzeStudentProgress() {
        System.out.print("Введите ID курса для анализа: ");
        int courseId = readIntInput();
        generateStudentProgressReport(courseId);
    }

    private static void generateStudentProgressReport(int courseId) {
        Course course = courseDAO.getCourseById(courseId);
        if (course == null) {
            System.out.println("Курс не найден!");
            return;
        }

        System.out.println("\n=== ОТЧЕТ ПО УСПЕВАЕМОСТИ ===");
        System.out.println("Курс: " + course.getTitle());

        List<User> students = enrollmentDAO.getCourseStudents(courseId);
        if (students.isEmpty()) {
            System.out.println("На курсе нет студентов.");
            return;
        }

        System.out.println("\nСписок студентов и их прогресс:");
        students.forEach(student -> {
            int progress = enrollmentDAO.getCourseProgress(student.getId(), courseId);
            System.out.printf("%s %s: %d%%\n",
                    student.getFullName(),
                    progress >= 80 ? "✓" : "",
                    progress
            );
        });

        double avgProgress = students.stream()
                .mapToInt(s -> enrollmentDAO.getCourseProgress(s.getId(), courseId))
                .average()
                .orElse(0);

        System.out.printf("\nСредний прогресс по курсу: %.1f%%\n", avgProgress);
    }

    private static void manageCertificates() {
        System.out.println("\n=== УПРАВЛЕНИЕ СЕРТИФИКАТАМИ ===");
        System.out.println("1. Выдать сертификат");
        System.out.println("2. Просмотреть выданные сертификаты");
        System.out.print("Выберите опцию: ");

        int choice = readIntInput();

        switch (choice) {
            case 1 -> issueCertificate();
            case 2 -> viewIssuedCertificates();
            default -> System.out.println("Неверный выбор!");
        }
    }

    private static void issueCertificate() {
        System.out.println("\n=== ВЫДАЧА СЕРТИФИКАТОВ ===");
        System.out.print("ID студента: ");
        int studentId = readIntInput();

        System.out.print("ID курса: ");
        int courseId = readIntInput();

        if (enrollmentDAO.isCourseCompleted(studentId, courseId)) {
            String certNumber = "CERT-" + System.currentTimeMillis();
            Certificate cert = new Certificate(0, studentId, courseId, LocalDateTime.now(), certNumber);

            if (certificateDAO.issueCertificate(cert)) {
                System.out.println("Сертификат выдан! Номер: " + certNumber);
            } else {
                System.out.println("Ошибка при выдаче сертификата!");
            }
        } else {
            System.out.println("Студент не завершил курс!");
        }
    }
    private static void viewIssuedCertificates() {
        System.out.println("\n=== ВЫДАННЫЕ СЕРТИФИКАТЫ ===");
        List<Certificate> certificates = certificateDAO.getAllCertificates();

        if (certificates.isEmpty()) {
            System.out.println("Сертификаты еще не выдавались.");
            return;
        }

        certificates.forEach(cert -> {
            User student = userDAO.getUserById(cert.getUserId());
            Course course = courseDAO.getCourseById(cert.getCourseId());

            System.out.printf("Сертификат №%s | Студент: %s | Курс: %s | Выдан: %s\n",
                    cert.getCertificateNumber(),
                    student.getFullName(),
                    course.getTitle(),
                    cert.getIssueDate().format(dtf)
            );
        });
    }
}