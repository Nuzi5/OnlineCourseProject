package models;

public class PlatformReport {
    private final int userCount;
    private final int courseCount;
    private final int enrollmentCount;
    private final int certificateCount;

    public PlatformReport(int userCount, int courseCount,
                          int enrollmentCount, int certificateCount) {
        this.userCount = userCount;
        this.courseCount = courseCount;
        this.enrollmentCount = enrollmentCount;
        this.certificateCount = certificateCount;
    }
    public int getUserCount() { return userCount; }
    public int getCourseCount() { return courseCount; }
    public int getEnrollmentCount() { return enrollmentCount; }
    public int getCertificateCount() { return certificateCount; }
}