package models;

public class Student extends User {

    public Student(int id, String username, String password, String email, String fullName) {
        super(id, username, password, email, fullName, "STUDENT");
    }

    public Student(String username, String password, String email, String fullName) {
        super(username, password, email, fullName, "STUDENT");
    }

    @Override
    public void showMenu() {
        System.out.println("\n=== МЕНЮ СТУДЕНТА ===");
        System.out.println("1. Доступные курсы");
        System.out.println("2. Мои курсы");
        System.out.println("3. Задания и тесты");
        System.out.println("4. Расписание вебинаров");
        System.out.println("5. Прогресс обучения");
        System.out.println("6. Мои сертификаты");
        System.out.println("7. Выход из системы");
        System.out.print("Выберите опцию: ");
    }

    public void enrollInCourse(int courseId) {

    }

    public void viewCourseDetails(int courseId) {

    }

    public void viewCourseMaterial(int materialId) {

    }

    public void submitAssignment(int assignmentId, String answer) {

    }

    public void takeTest(int testId) {

    }

    public void viewProgress() {

    }

    public void viewCertificates() {
        // Логика просмотра полученных сертификатов
    }
}


