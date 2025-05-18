package models.additional;

public class CourseWithTeacher {
    private final int id;
    private final String title;
    private final String description;
    private final String teacherName;
    private final boolean isActive;

    public CourseWithTeacher(int id, String title, String description,
                             String teacherName, boolean isActive) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.teacherName = teacherName;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }
}