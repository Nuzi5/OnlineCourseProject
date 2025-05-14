package models;

public class CourseStats {
    private final int courseId;
    private final String courseTitle;
    private final int studentCount;

    public CourseStats(int courseId, String courseTitle, int studentCount) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.studentCount = studentCount;
    }

    public int getCourseId() { return courseId; }
    public String getCourseTitle() { return courseTitle; }
    public int getStudentCount() { return studentCount; }
}
