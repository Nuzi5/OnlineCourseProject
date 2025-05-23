package models.additional;

public class AnswerOption {
    private final int id;
    private final int questionId;
    private final String optionText;
    private final boolean isCorrect;

    public AnswerOption(int id, int questionId, String optionText, boolean isCorrect) {
        this.id = id;
        this.questionId = questionId;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    public int getId() { return id; }
    public String getOptionText() { return optionText; }
}