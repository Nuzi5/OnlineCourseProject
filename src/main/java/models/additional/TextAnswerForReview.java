package models.additional;

public class TextAnswerForReview {
    private final int answerId;
    private final String studentName;
    private final String questionText;
    private final String answer;
    private final int maxPoints;

    public TextAnswerForReview(int answerId, String studentName,
                               String questionText, String answer, int maxPoints) {
        this.answerId = answerId;
        this.studentName = studentName;
        this.questionText = questionText;
        this.answer = answer;
        this.maxPoints = maxPoints;
    }
    public int getAnswerId() { return answerId; }
    public String getStudentName() { return studentName; }
    public String getQuestionText() { return questionText; }
    public String getAnswer() { return answer; }
    public int getMaxPoints() { return maxPoints; }
}
