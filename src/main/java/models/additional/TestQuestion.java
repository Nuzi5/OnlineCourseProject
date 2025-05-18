package models.additional;

public class TestQuestion {
    private final int id;
    private final int testId;
    private final String questionText;
    private final String questionType;
    private final int points;

    public TestQuestion(int id, int testId, String questionText,
                        String questionType, int points) {
        this.id = id;
        this.testId = testId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.points = points;
    }

    public int getId() { return id; }
    public int getTestId() { return testId; }
    public String getQuestionText() { return questionText; }
    public String getQuestionType() { return questionType; }
    public int getPoints() { return points; }
}