import java.util.ArrayList;
import java.util.List;

public class Question {
    private String question;
    private List<String> answers;
    private List<Boolean> selectedStates;
    private List<Boolean> correctAnswers;
    private String imagePath;

    public Question(String question, List<String> answers, List<Boolean> correctAnswers, String imagePath) {
        this.question = question;
        this.answers = answers;
        this.selectedStates = new ArrayList<>(answers.size());
        for (int i = 0; i < answers.size(); i++) {
            selectedStates.add(false);
        }
        this.correctAnswers = correctAnswers;
        this.imagePath = imagePath;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public boolean getSelectedState(int index) {
        return selectedStates.get(index);
    }

    public void setSelectedState(int index, boolean state) {
        selectedStates.set(index, state);
    }

    public List<Boolean> getSelectedStates() {
        return selectedStates;
    }

    public List<Boolean> getCorrectAnswers() {
        return correctAnswers;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
