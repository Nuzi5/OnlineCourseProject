package models;

public class AnswerOption {
    private int id;
    private int questionId;
    private String optionText;
    private boolean isCorrect;

    public AnswerOption(int id, int questionId, String optionText, boolean isCorrect) {
        this.id = id;
        this.questionId = questionId;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    public void setId(int id) {
        this.id = id;
    }

////    public void setCorrect(boolean correct) {
//        isCorrect = correct;
//    }

////    public void setOptionText(String optionText) {
//        this.optionText = optionText;
//    }

////    public void setQuestionId(int questionId) {
//        this.questionId = questionId;
//    }

    public int getId() { return id; }
    public int getQuestionId() { return questionId; }
    public String getOptionText() { return optionText; }
    public boolean isCorrect() { return isCorrect; }
}