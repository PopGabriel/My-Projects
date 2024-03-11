public class Main {
    public static void main(String[] args) {
 
        javax.swing.SwingUtilities.invokeLater(() -> {
            ExamGeneratorGUI examGeneratorGUI = new ExamGeneratorGUI("part1_questions.txt","part2_questions.txt");
            examGeneratorGUI.setVisible(true);
        });
    }
}
