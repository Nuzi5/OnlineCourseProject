package models;

import java.util.ArrayList;
import java.util.List;

public class Test {
    private int id;
    private int courseId;
    private String title;
    private String description;
    private final int timeLimit;
    private final int passingScore;
    private int created_by;

    public Test(int id, int courseId, String title, String description, int timeLimit, int passingScore, int created_by) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.timeLimit = timeLimit;
        this.passingScore = passingScore;
        this.created_by = created_by;
    }
    public Test(int id, int courseId, String title, String description, int timeLimit, int passingScore) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.timeLimit = timeLimit;
        this.passingScore = passingScore;

    }

    public int getId() {
        return id;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getPassingScore() {
        return passingScore;
    }

    public int getCreatedBy() {
        return created_by;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuestions(List<TestQuestion> testQuestions) {
        List<TestQuestion> questions = new ArrayList<>(testQuestions);
        for (TestQuestion question : questions) {
            question.setTestId(this.id);
        }
    }
}
