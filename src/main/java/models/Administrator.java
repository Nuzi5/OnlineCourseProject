package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Administrator extends User {
    private final Connection connection;
    private final Scanner scanner;

    public Administrator(int id, String username, String password,
                         String email, String fullName, Connection connection) {
        super(id, username, password, email, fullName, "ADMIN");
        this.connection = connection;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n=== МЕНЮ АДМИНИСТРАТОРА ===");
            System.out.println("1. Управление курсами");
            System.out.println("2. Управление пользователями");
            System.out.println("3. Мониторинг активности");
            System.out.println("4. Выход из системы");
            System.out.print("Выберите опцию: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: manageCourses(); break;
                case 2: manageUsers(); break;
                case 3: monitorActivity(); break;
                case 4: return;
                default: System.out.println("Неверный выбор!");
            }
        }
    }

    private void manageCourses() {
        System.out.println("\n--- Управление курсами ---");
        System.out.println("1. Создать курс");
        System.out.println("2. Редактировать курс");
        System.out.println("3. Активировать/деактивировать курс");
        System.out.println("4. Просмотреть все курсы");
        System.out.print("Выберите действие: ");

        int action = scanner.nextInt();
        scanner.nextLine();

        switch (action) {
            case 1: createCourse(); break;
            case 2: editCourse(); break;
            case 3: toggleCourseStatus(); break;
            case 4: viewAllCourses(); break;
            default: System.out.println("Неверный выбор!");
        }
    }

    private void createCourse() {
        System.out.print("Введите название курса: ");
        String title = scanner.nextLine();

        System.out.print("Введите описание курса: ");
        String description = scanner.nextLine();

        System.out.print("Активен ли курс? (y/n): ");
        boolean isActive = scanner.nextLine().equalsIgnoreCase("y");

        String sql = "INSERT INTO courses (title, description, created_by, is_active) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setInt(3, this.getId());
            stmt.setBoolean(4, isActive);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Курс успешно создан!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при создании курса: " + e.getMessage());
        }
    }

    private void editCourse() {
        viewAllCourses();
        System.out.print("Введите ID курса для редактирования: ");
        int courseId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Новое название (оставьте пустым, чтобы не менять): ");
        String newTitle = scanner.nextLine();

        System.out.print("Новое описание (оставьте пустым, чтобы не менять): ");
        String newDescription = scanner.nextLine();

        StringBuilder sql = new StringBuilder("UPDATE courses SET ");
        List<Object> params = new ArrayList<>();

        if (!newTitle.isEmpty()) {
            sql.append("title = ?, ");
            params.add(newTitle);
        }
        if (!newDescription.isEmpty()) {
            sql.append("description = ?, ");
            params.add(newDescription);
        }

        if (params.isEmpty()) {
            System.out.println("Ничего не изменено!");
            return;
        }

        sql.delete(sql.length()-2, sql.length()); // Удаляем последнюю ", "
        sql.append(" WHERE id = ?");
        params.add(courseId);

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i+1, params.get(i));
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Курс успешно обновлен!");
            } else {
                System.out.println("Курс с указанным ID не найден");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении курса: " + e.getMessage());
        }
    }

    private void toggleCourseStatus() {
        viewAllCourses();
        System.out.print("Введите ID курса: ");
        int courseId = scanner.nextInt();

        String sql = "UPDATE courses SET is_active = NOT is_active WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Статус курса изменен!");
            } else {
                System.out.println("Курс с указанным ID не найден");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при изменении статуса курса: " + e.getMessage());
        }
    }

    private void viewAllCourses() {
        String sql = "SELECT id, title, description, is_active FROM courses";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nСписок всех курсов:");
            System.out.println("ID | Название | Статус");
            while (rs.next()) {
                System.out.printf(
                        "%d | %s | %s\n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getBoolean("is_active") ? "Активен" : "Неактивен"
                );
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении курсов: " + e.getMessage());
        }
    }

    private void manageUsers() {
        System.out.println("\n--- Управление пользователями ---");
        System.out.println("1. Просмотреть всех пользователей");
        System.out.println("2. Добавить пользователя");
        System.out.println("3. Изменить роль пользователя");
        System.out.println("4. Блокировка/разблокировка");
        System.out.print("Выберите действие: ");

        int action = scanner.nextInt();
        scanner.nextLine();

        switch (action) {
            case 1: viewAllUsers(); break;
            case 2: addUser(); break;
            case 3: changeUserRole(); break;
            case 4: toggleUserStatus(); break;
            default: System.out.println("Неверный выбор!");
        }
    }

    private void viewAllUsers() {
        String sql = "SELECT id, username, email, full_name, role, is_active FROM users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nСписок пользователей:");
            System.out.println("ID | Логин | Имя | Роль | Статус");
            while (rs.next()) {
                System.out.printf(
                        "%d | %s | %s | %s | %s\n",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getBoolean("is_active") ? "Активен" : "Заблокирован"
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

        String sql = "INSERT INTO users " +
                "(username, password, email, full_name, role, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, fullName);
            stmt.setString(5, role);
            stmt.setBoolean(6, isActive);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Пользователь успешно добавлен!");
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

        String sql = "UPDATE users SET role = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newRole);
            stmt.setInt(2, userId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
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

        String sql = "UPDATE users SET is_active = NOT is_active WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Статус пользователя изменен!");
            } else {
                System.out.println("Пользователь с указанным ID не найден");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при изменении статуса: " + e.getMessage());
        }
    }

    private void monitorActivity() {
        System.out.println("\n--- Мониторинг активности ---");
        System.out.println("1. Статистика платформы");
        System.out.println("2. Логи действий");
        System.out.println("3. Активные пользователи");
        System.out.print("Выберите действие: ");

        int action = scanner.nextInt();
        scanner.nextLine();

        switch (action) {
            case 1: viewPlatformStats(); break;
            case 2: viewActivityLogs(); break;
            case 3: viewActiveUsers(); break;
            default: System.out.println("Неверный выбор!");
        }
    }

    private void viewPlatformStats() {
        try {
            String statsSql = "SELECT " +
                    "(SELECT COUNT(*) FROM users) as user_count, " +
                    "(SELECT COUNT(*) FROM courses) as course_count, " +
                    "(SELECT COUNT(*) FROM enrollments) as enrollment_count, " +
                    "(SELECT COUNT(*) FROM test_results) as test_count";

            Statement statsStmt = connection.createStatement();
            ResultSet statsRs = statsStmt.executeQuery(statsSql);
            statsRs.next();

            System.out.println("\nОбщая статистика платформы:");
            System.out.println("Пользователей: " + statsRs.getInt("user_count"));
            System.out.println("Курсов: " + statsRs.getInt("course_count"));
            System.out.println("Записей на курсы: " + statsRs.getInt("enrollment_count"));
            System.out.println("Пройденных тестов: " + statsRs.getInt("test_count"));

            String activitySql = "SELECT role, COUNT(*) as count, " +
                    "MAX(last_login) as last_activity " +
                    "FROM users GROUP BY role";

            Statement activityStmt = connection.createStatement();
            ResultSet activityRs = activityStmt.executeQuery(activitySql);

            System.out.println("\nАктивность по ролям:");
            System.out.println("Роль | Количество | Последняя активность");
            while (activityRs.next()) {
                System.out.printf(
                        "%s | %d | %s\n",
                        activityRs.getString("role"),
                        activityRs.getInt("count"),
                        activityRs.getTimestamp("last_activity")
                );
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при получении статистики: " + e.getMessage());
        }
    }

    private void viewActivityLogs() {
        System.out.print("\nВведите количество последних записей (0 - все): ");
        int limit = scanner.nextInt();

        String sql = "SELECT * FROM activity_logs ORDER BY action_time DESC" +
                (limit > 0 ? " LIMIT " + limit : "");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
        String sql = "SELECT id, username, full_name, last_login " +
                "FROM users WHERE is_active = true " +
                "ORDER BY last_login DESC LIMIT 20";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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