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
}