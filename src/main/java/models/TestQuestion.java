package models;

import java.util.ArrayList;
import java.util.List;

public class TestQuestion {
    private int id;
    private final int testId;
    private final String questionText;
    private final String questionType;
    private final int points;
    private List<AnswerOption> answerOptions = new ArrayList<>();

    public TestQuestion(int id, int testId, String questionText, String questionType, int points) {
        this.id = id;
        this.testId = testId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public int getPoints() {
        return points;
    }

    public List<AnswerOption> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<AnswerOption> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public int getTestId() {
        return testId;
    }

    public void setId(int id) {
        this.id = id;
    }
}