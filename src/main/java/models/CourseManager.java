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
}