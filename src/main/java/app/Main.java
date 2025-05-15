package app;

import db.*;
import models.*;
import dao.*;

import java.sql.*;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    private static UserDAO userDAO;

    public static void main(String[] args) {
        initializeDatabaseAndDAOs();
        showMainMenu();
    }

    private static void initializeDatabaseAndDAOs() {
        DatabaseSetup.initDatabase();
        DatabaseMigrator.applyMigrations();

        try {
            Connection connection = DatabaseSetup.getConnection();
            userDAO = new UserDAO(connection);
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void showMainMenu() {
        boolean isValidInput = false;

        while (!isValidInput) {
            try {
                System.out.println("\n*** ОБРАЗОВАТЕЛЬНАЯ ПЛАТФОРМА ***");
                System.out.println("1. Вход в систему");
                System.out.println("2. Регистрация (для студентов)");
                System.out.println("3. Выход");
                System.out.print("Выберите опцию(1-3): ");

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
                    default -> System.out.println("Неверный выбор! Попробуйте снова:");
                }
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: Введите число!");
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
        System.out.println("\n*** РЕГИСТРАЦИЯ СТУДЕНТА ***");
        System.out.print("Ваше ФИО: ");
        String fullName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Логин: ");
        String username = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        try {
            int newId = userDAO.createUser(
                    username,
                    password,
                    email,
                    fullName,
                    "STUDENT" ,
                    true
            );

            Student student = new Student(
                    newId,
                    username,
                    password,
                    email,
                    fullName,
                    DatabaseSetup.getConnection()
            );
            System.out.println("Студент успешно зарегистрирован! ID: " + newId);

        } catch (SQLException e) {
            System.err.println("Ошибка регистрации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showRoleMenu() {
        while (currentUser != null) {
            currentUser.showMenu();
        }
    }
}