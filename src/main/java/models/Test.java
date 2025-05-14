package models;

public class Test {
    private final int id;
    private final int courseId;
    private final String title;
    private final String description;
    private final int timeLimit;
    private final int passingScore;
    private final Integer studentScore;

    public Test(int id, int courseId, String title,
                String description, int timeLimit, int passingScore) {
        this(id, courseId, title, description, timeLimit, passingScore, null);
    }

    public Test(int id, int courseId, String title,
                String description, int timeLimit,
                int passingScore, Integer studentScore) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.timeLimit = timeLimit;
        this.passingScore = passingScore;
        this.studentScore = studentScore;
    }
    public Test(int id, String title) {
        this(id, 0, title, "", 0, 0, null);
    }
    public int getId() { return id; }
    public int getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getTimeLimit() { return timeLimit; }
    public int getPassingScore() { return passingScore; }
    public Integer getScore() { return studentScore; }
}