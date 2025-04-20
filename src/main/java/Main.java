package main;

import dao.UserDAO;
import models.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class Main {
    private static Connection connection;
    private static UserDAO userDAO;

    public static void main(String[] args) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/education_platform\n");

            Scanner scanner = new Scanner(System.in);

            System.out.println("Добро пожаловать на платформу!");
            System.out.print("Логин: ");
            String login = scanner.nextLine();
            System.out.print("Пароль: ");
            String password = scanner.nextLine();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}