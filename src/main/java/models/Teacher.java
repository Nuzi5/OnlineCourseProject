package models;

public class Teacher extends User {

    public Teacher(int id, String username, String password, String email, String fullName) {
        super(id, username, password, email, fullName, "TEACHER");
    }

    public Teacher(String username, String password, String email, String fullName) {
        super(username, password, email, fullName, "TEACHER");
    }

    @Override
    public void showMenu() {
        System.out.println("\n=== МЕНЮ ПРЕПОДАВАТЕЛЯ ===");
        System.out.println("1. Мои курсы");
        System.out.println("2. Создать учебный материал");
        System.out.println("3. Создать задание");
        System.out.println("4. Создать тест");
        System.out.println("5. Проверить задания студентов");
        System.out.println("6. Управление вебинарами");
        System.out.println("7. Выход из системы");
        System.out.print("Выберите опцию: ");
    }
}