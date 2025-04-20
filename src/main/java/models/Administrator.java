package models;

public class Administrator extends User {

    public Administrator(int id, String username, String password, String email, String fullName) {
        super(id, username, password, email, fullName, "ADMIN");
    }
    public Administrator(String username, String password, String email, String fullName) {
        super( username, password, email, fullName, "ADMIN");
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

    public void createCourse() {

    }

    public void editCourse(int courseId) {

    }

    public void deleteCourse(int courseId) {

    }

    public void createUser(String role) {

    }

    public void editUser(int userId) {

    }

    public void deleteUser(int userId) {

    }

    public void viewActivityLogs() {

    }

    public void generateSystemReport() {

    }
}