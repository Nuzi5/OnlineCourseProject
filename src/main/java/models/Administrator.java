package models;

import dao.AdminDAO;
import dao.UserDAO;
import db.DatabaseSetup;
import models.additional.CourseWithTeacher;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class Administrator extends User {
    private final Scanner scanner;
    private final AdminDAO adminDAO;
    private final UserDAO userDAO;
    private boolean isActive;

    public Administrator(int id, String username, String password,
                         String email, String fullName, Connection connection) {
        super(id, username, password, email, fullName, "ADMIN", true);
        this.scanner = new Scanner(System.in);
        this.adminDAO = new AdminDAO(connection);
        this.userDAO = new UserDAO(connection);
        this.isActive = true;
    }
    public boolean isActive() { return isActive;}

    public void setActive(boolean active) {isActive = active;}

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n*** МЕНЮ АДМИНИСТРАТОРА ***");
            System.out.println("1. Управление курсами");
            System.out.println("2. Управление пользователями");
            System.out.println("3. Мониторинг активности");
            System.out.println("4. Выход из системы");
            System.out.print("Выберите опцию: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> manageCourses();
                case 2 -> manageUsers();
                case 3 -> monitorActivity();
                case 4 -> {
                    System.out.println("Выход из системы...\n\nРабота системы завершена. До свидания!");
                    System.exit(0);
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private void manageCourses() {
        System.out.println("\n*** Управление курсами ***");
        System.out.println("1. Создать курс");
        System.out.println("2. Редактировать курс");
        System.out.println("3. Активировать/деактивировать курс");
        System.out.println("4. Просмотреть все курсы");
        System.out.println("5. Удалить курс");
        System.out.println("6. Назад в меню администратора");
        System.out.print("Выберите действие: ");

        int action = scanner.nextInt();
        scanner.nextLine();

        switch (action) {
            case 1 -> createCourse();
            case 2 -> editCourse();
            case 3 -> toggleCourseStatus();
            case 4 -> viewAllCourses();
            case 5 -> deleteCourse();
            case 6 -> {return;}
            default -> System.out.println("Неверный выбор!");
        }
    }

    private void createCourse() {
        try {
            List<User> teachers = userDAO.getTeachers();
            System.out.println("\nСписок преподавателей:");
            teachers.forEach(t -> System.out.printf("ID: %d | %s\n", t.getId(), t.getFullName()));

            System.out.print("Введите ID преподавателя: ");
            int teacherId = scanner.nextInt();
            scanner.nextLine();

            if (teachers.stream().noneMatch(t -> t.getId() == teacherId)) {
                System.out.println("Преподаватель с таким ID не найден!");
                return;
            }

            System.out.print("Введите название курса: ");
            String title = scanner.nextLine();

            System.out.print("Введите описание курса: ");
            String description = scanner.nextLine();

            System.out.print("Активен ли курс? (y/n): ");
            boolean isActive = scanner.nextLine().equalsIgnoreCase("y");

            if (adminDAO.createCourse(title, description, this.getId(), teacherId, isActive)) {
                System.out.println("Курс успешно создан и привязан к преподавателю!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при создании курса: " + e.getMessage());
        }
    }

    private void editCourse() {
        try {
            viewAllCourses();
            System.out.print("Введите ID курса для редактирования: ");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Новое название (оставьте пустым, чтобы не менять): ");
            String newTitle = scanner.nextLine();

            System.out.print("Новое описание (оставьте пустым, чтобы не менять): ");
            String newDescription = scanner.nextLine();

            if (adminDAO.updateCourse(courseId, newTitle, newDescription)) {
                System.out.println("Курс успешно обновлен!");
            } else {
                System.out.println("Не удалось обновить курс");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении курса: " + e.getMessage());
        }
    }

    private void toggleCourseStatus() {
        try {
            viewAllCourses();
            System.out.print("Введите ID курса: ");
            int courseId = scanner.nextInt();

            if (adminDAO.toggleCourseStatus(courseId)) {
                System.out.println("Статус курса изменен!");
            } else {
                System.out.println("Курс с указанным ID не найден");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при изменении статуса курса: " + e.getMessage());
        }
    }

    private void viewAllCourses() {
        try {
            System.out.println("\nСписок всех курсов:");
            System.out.println("ID  | Название              | Преподаватель       | Статус");
            for (CourseWithTeacher course : adminDAO.getAllCoursesWithTeachers()) {
                System.out.printf("%-4d| %-20s | %-20s | %s%n",
                        course.getId(),
                        course.getTitle(),
                        course.getTeacherName() != null ? course.getTeacherName() : "Не назначен",
                        course.isActive() ? "Активен" : "Неактивен");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении курсов: " + e.getMessage());
        }
    }

    private void deleteCourse() {
        try {
            viewAllCourses();
            System.out.print("\nВведите ID курса для удаления: ");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Вы уверены, что хотите удалить курс? (y/n): ");
            String confirmation = scanner.nextLine();

            if (confirmation.equalsIgnoreCase("y")) {
                if (adminDAO.deleteCourse(courseId)) {
                    System.out.println("Курс успешно удален!");
                } else {
                    System.out.println("Курс с указанным ID не найден");
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении курса: " + e.getMessage());
        }
    }

    private void manageUsers() {
        System.out.println("\n*** Управление пользователями ***");
        System.out.println("1. Просмотреть всех пользователей");
        System.out.println("2. Добавить пользователя");
        System.out.println("3. Изменить роль пользователя");
        System.out.println("4. Блокировка/разблокировка");
        System.out.println("5. Удалить пользователя");
        System.out.println("6. Полное редактирование пользователя");
        System.out.println("7. Назад в меню админстратора");
        System.out.print("Выберите действие: ");

        int action = scanner.nextInt();
        scanner.nextLine();

        switch (action) {
            case 1 -> viewAllUsers();
            case 2 -> addUser();
            case 3 -> changeUserRole();
            case 4 -> toggleUserStatus();
            case 5 -> deleteUser();
            case 6 -> editUser();
            case 7 -> {return;}
            default -> System.out.println("Неверный выбор!");
        }
    }

    private void viewAllUsers() {
        try {
            System.out.println("\nСписок пользователей:");
            System.out.println("ID | Логин | Имя | Роль | Статус");
            for (User user : adminDAO.getAllUsers()) {
                System.out.printf(
                        "%d | %s | %s | %s | %s\n",
                        user.getId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getRole(),
                        user.isActive() ? "Активен" : "Заблокирован"
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении пользователей: " + e.getMessage());
        }
    }

    private void addUser() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine();

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        System.out.print("Введите email: ");
        String email = scanner.nextLine();

        System.out.print("Введите полное имя: ");
        String fullName = scanner.nextLine();

        System.out.print("Введите роль (ADMIN/TEACHER/STUDENT/MANAGER): ");
        String role = scanner.nextLine().toUpperCase();

        System.out.print("Активен? (y/n): ");
        boolean isActive = scanner.nextLine().equalsIgnoreCase("y");

        try {
            Connection connection = DatabaseSetup.getConnection();

            UserDAO userDAO = new UserDAO(connection);

            int newUserId = userDAO.createUser(username, password, email, fullName, role, isActive);
            if(newUserId > 0) {
                System.out.println("Пользователь успешно добавлен! ID: " + newUserId);
            } else {
                System.out.println("Не удалось добавить пользователя");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении пользователя: " + e.getMessage());
        }
    }

    private void changeUserRole() {
        viewAllUsers();
        System.out.print("Введите ID пользователя: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Новая роль (ADMIN/TEACHER/STUDENT/MANAGER): ");
        String newRole = scanner.nextLine().toUpperCase();

        try {
            if (adminDAO.updateUserRole(userId, newRole)) {
                System.out.println("Роль пользователя изменена!");
            } else {
                System.out.println("Пользователь с указанным ID не найден");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при изменении роли: " + e.getMessage());
        }
    }

    private void toggleUserStatus() {
        viewAllUsers();
        System.out.print("Введите ID пользователя: ");
        int userId = scanner.nextInt();

        try {
            if (adminDAO.toggleUserStatus(userId)) {
                System.out.println("Статус пользователя изменен!");
            } else {
                System.out.println("Пользователь с указанным ID не найден");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при изменении статуса: " + e.getMessage());
        }
    }

    private void deleteUser() {
        viewAllUsers();
        System.out.print("\nВведите ID пользователя для удаления: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        if (userId == this.getId()) {
            System.out.println("Ошибка: Нельзя удалить самого себя!");
            return;
        }

        System.out.print("Вы уверены, что хотите удалить пользователя ID " + userId + "? (y/n): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("y")) {
            try {
                if (!adminDAO.userExists(userId)) {
                    System.out.println("Ошибка: Пользователь с ID " + userId + " не найден!");
                    return;
                }

                if (adminDAO.deleteUser(userId)) {
                    System.out.println("Пользователь успешно удален!");

                    if (adminDAO.isTeacher(userId)) {
                        System.out.println("Курсы преподавателя были освобождены");
                    }
                } else {
                    System.out.println("Ошибка при удалении пользователя!");
                }
            } catch (SQLException e) {
                System.out.println("Ошибка базы данных: " + e.getMessage());
            }
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    private void editUser() {
        try {
            viewAllUsers();
            System.out.print("\nВведите ID пользователя для редактирования: ");
            int userId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Новый логин: ");
            String username = scanner.nextLine();

            System.out.print("Новое ФИО: ");
            String fullName = scanner.nextLine();

            System.out.print("Новый email: ");
            String email = scanner.nextLine();

            System.out.print("Новая роль (ADMIN/TEACHER/STUDENT/MANAGER): ");
            String role = scanner.nextLine().toUpperCase();

            System.out.print("Новый статус (active/blocked): ");
            boolean isActive = scanner.nextLine().equalsIgnoreCase("active");

            if (adminDAO.updateUser(userId, username, fullName, email, role, isActive)) {
                System.out.println("Данные пользователя успешно обновлены!");
            } else {
                System.out.println("Не удалось обновить данные пользователя");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при редактировании пользователя: " + e.getMessage());
        }
    }

    private void monitorActivity() {
        System.out.println("\n*** Мониторинг активности ***");
        System.out.println("1. Статистика платформы");
        System.out.println("2. Логи действий");
        System.out.println("3. Активные пользователи");
        System.out.println("4. Назад в меню администратора");
        System.out.print("Выберите действие: ");

        int action = scanner.nextInt();
        scanner.nextLine();

        switch (action) {
            case 1 -> viewPlatformStats();
            case 2 -> viewActivityLogs();
            case 3 -> viewActiveUsers();
            case 4 -> {return;}
            default -> System.out.println("Неверный выбор!");
        }
    }

    private void viewPlatformStats() {
        try {
            ResultSet rs = adminDAO.getPlatformStats();
            if (rs.next()) {
                System.out.println("\nОбщая статистика платформы:");
                System.out.println("Пользователей: " + rs.getInt("user_count"));
                System.out.println("Курсов: " + rs.getInt("course_count"));
                System.out.println("Записей на курсы: " + rs.getInt("enrollment_count"));
                System.out.println("Пройденных тестов: " + rs.getInt("test_count"));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении статистики: " + e.getMessage());
        }
    }

    private void viewActivityLogs() {
        System.out.print("\nВведите количество последних записей (0 - все): ");
        int limit = scanner.nextInt();

        try {
            ResultSet rs = adminDAO.getActivityLogs(limit);
            System.out.println("\nЛоги действий:");
            System.out.println("Дата | Пользователь | Действие");
            while (rs.next()) {
                System.out.printf(
                        "%s | %s | %s\n",
                        rs.getTimestamp("action_time"),
                        rs.getString("username"),
                        rs.getString("action")
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении логов: " + e.getMessage());
        }
    }

    private void viewActiveUsers() {
        try {
            ResultSet rs = adminDAO.getActiveUsers();
            System.out.println("\nСамые активные пользователи:");
            System.out.println("ID | Логин | Имя | Последний вход");
            while (rs.next()) {
                System.out.printf(
                        "%d | %s | %s | %s\n",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getTimestamp("last_login")
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении активных пользователей: " + e.getMessage());
        }
    }
}