package models.additional;

public class CourseReport {
    private final String courseTitle;
    private final int studentCount;
    private final double averageScore;

    public CourseReport(String courseTitle, int studentCount, double averageScore) {
        this.courseTitle = courseTitle;
        this.studentCount = studentCount;
        this.averageScore = averageScore;
    }
    public String getCourseTitle() { return courseTitle; }
    public int getStudentCount() { return studentCount; }
    public double getAverageScore() { return averageScore; }
}
