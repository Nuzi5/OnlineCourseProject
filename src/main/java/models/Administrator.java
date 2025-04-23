package models;

import dao.CourseDAO;
import dao.UserDAO;
import java.util.Scanner;

public class Administrator extends User {
    private final Scanner scanner = new Scanner(System.in);
    private final CourseDAO courseDAO;
    private final UserDAO userDAO;

    public Administrator(int id, String username, String password, String email, String fullName) {
        super(id, username, password, email, fullName, "ADMIN");
        this.courseDAO = new CourseDAO();
        this.userDAO = new UserDAO();
    }

    public Administrator( String username, String password, String email, String fullName) {
        super( username, password, email, fullName, "ADMIN");
        this.courseDAO = new CourseDAO();
        this.userDAO = new UserDAO();
    }

    @Override
    public void showMenu() {
        System.out.println("\n=== МЕНЮ АДМИНИСТРАТОРА ===");
        System.out.println("1. Управление курсами");
        System.out.println("2. Управление пользователями");
        System.out.println("3. Мониторинг активности");
        System.out.println("4. Анализ производительности платформы");
        System.out.println("5. Выход из системы");
        System.out.print("Выберите опцию: ");
    }

//    public void createCourse() {
//        System.out.print("\nНазвание курса: ");
//        String title = scanner.nextLine();
//        System.out.print("Описание: ");
//        String description = scanner.nextLine();
//
//        Course course = new Course(title, description, this.getId(), true);
//        if (courseDAO.createCourse(course)) {
//            System.out.println("Курс успешно создан! ID: " + course.getId());
//        } else {
//            System.out.println("Ошибка при создании курса!");
//        }
//    }
//
//    public void editCourse(int courseId) {
//        Course course = courseDAO.getCourseById(courseId);
//        if (course == null) {
//            System.out.println("Курс с ID " + courseId + " не найден!");
//            return;
//        }
//
//        System.out.println("\nРедактирование курса ID: " + courseId);
//        System.out.println("Текущее название: " + course.getTitle());
//        System.out.print("Новое название (оставьте пустым, чтобы не менять): ");
//        String newTitle = scanner.nextLine();
//
//        System.out.println("Текущее описание: " + course.getDescription());
//        System.out.print("Новое описание: ");
//        String newDescription = scanner.nextLine();
//
//        if (!newTitle.isEmpty()) {
//            course.setTitle(newTitle);
//        }
//        if (!newDescription.isEmpty()) {
//            course.setDescription(newDescription);
//        }
//
//        if (courseDAO.updateCourse(course)) {
//            System.out.println("Курс успешно обновлен!");
//        } else {
//            System.out.println("Ошибка при обновлении курса!");
//        }
//    }
//
//    public void deleteCourse(int courseId) {
//        System.out.print("Вы уверены, что хотите удалить курс ID " + courseId + "? (y/n): ");
//        String confirmation = scanner.nextLine();
//
//        if (confirmation.equalsIgnoreCase("y")) {
//            if (courseDAO.deleteCourse(courseId)) {
//                System.out.println("Курс успешно удален!");
//            } else {
//                System.out.println("Ошибка при удалении курса!");
//            }
//        }
//    }
//
//    public void createUser(String role) {
//        System.out.print("\nФИО: ");
//        String fullName = scanner.nextLine();
//        System.out.print("Email: ");
//        String email = scanner.nextLine();
//        System.out.print("Логин: ");
//        String username = scanner.nextLine();
//        System.out.print("Пароль: ");
//        String password = scanner.nextLine();
//
//        User newUser = switch (role.toUpperCase()) {
//            case "TEACHER" -> new Teacher(username, password, email, fullName);
//            case "STUDENT" -> new Student(username, password, email, fullName);
//            case "MANAGER" -> new CourseManager(username, password, email, fullName);
//            default -> null;
//        };
//
//        if (newUser != null && userDAO.createUser(newUser)) {
//            System.out.println("Пользователь создан успешно! ID: " + newUser.getId());
//        } else {
//            System.out.println("Ошибка при создании пользователя!");
//        }
//    }
//
//    public void editUser(int userId) {
//        User user = userDAO.getUserById(userId);
//        if (user == null) {
//            System.out.println("Пользователь с ID " + userId + " не найден!");
//            return;
//        }
//
//        System.out.println("\nРедактирование пользователя ID: " + userId);
//        System.out.println("Текущее ФИО: " + user.getFullName());
//        System.out.print("Новое ФИО (оставьте пустым, чтобы не менять): ");
//        String newFullName = scanner.nextLine();
//
//        System.out.println("Текущий email: " + user.getEmail());
//        System.out.print("Новый email: ");
//        String newEmail = scanner.nextLine();
//
//        if (!newFullName.isEmpty()) {
//            user.setFullName(newFullName);
//        }
//        if (!newEmail.isEmpty()) {
//            user.setEmail(newEmail);
//        }
//
//        if (userDAO.updateUser(user)) {
//            System.out.println("Пользователь успешно обновлен!");
//        } else {
//            System.out.println("Ошибка при обновлении пользователя!");
//        }
//    }
//
//    public void deleteUser(int userId) {
//        System.out.print("Вы уверены, что хотите удалить пользователя ID " + userId + "? (y/n): ");
//        String confirmation = scanner.nextLine();
//
//        if (confirmation.equalsIgnoreCase("y")) {
//            if (userDAO.deleteUser(userId)) {
//                System.out.println("Пользователь успешно удален!");
//            } else {
//                System.out.println("Ошибка при удалении пользователя!");
//            }
//        }
//    }
//
//    public void viewActivityLogs() {
//        try {
//            System.out.println("\n=== ЖУРНАЛ АКТИВНОСТИ ===");
//            // В реальной реализации здесь будет запрос к логам
//            System.out.println("Дата/Время\t\tДействие\t\tПользователь");
//            System.out.println("--------------------------------------------------");
//            System.out.println("2023-05-15 10:30\tВход в систему\t\tadmin");
//            System.out.println("2023-05-15 10:35\tСоздание курса\t\tadmin");
//            System.out.println("2023-05-15 11:20\tРедактирование пользователя\tadmin");
//        } catch (Exception e) {
//            System.out.println("Ошибка при получении логов: " + e.getMessage());
//        }
//    }
//
//    public void generateSystemReport() {
//        try {
//            System.out.println("\n=== СИСТЕМНЫЙ ОТЧЕТ ===");
//
//            int userCount = userDAO.getAllUsers().size();
//            int courseCount = courseDAO.getAllCourses().size();
//            int activeCourses = courseDAO.getActiveCourses().size();
//
//            System.out.println("Общая статистика:");
//            System.out.println("Пользователей: " + userCount);
//            System.out.println("Курсов: " + courseCount + " (активных: " + activeCourses + ")");
//
//            System.out.println("\nРаспределение по ролям:");
//            System.out.println("Администраторы: " + userDAO.getUsersByRole("ADMIN").size());
//            System.out.println("Преподаватели: " + userDAO.getUsersByRole("TEACHER").size());
//            System.out.println("Студенты: " + userDAO.getUsersByRole("STUDENT").size());
//            System.out.println("Менеджеры: " + userDAO.getUsersByRole("MANAGER").size());
//
//        } catch (Exception e) {
//            System.out.println("Ошибка при генерации отчета: " + e.getMessage());
//        }
//    }
}