// ExamGenerator.java
public class ExamGenerator {
    public static void main(String[] args) {
        String part1FilePath = "part1_questions.txt";
        String part2FilePath = "part2_questions.txt";

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ExamGeneratorGUI examGenerator = new ExamGeneratorGUI(part1FilePath, part2FilePath);
                examGenerator.setVisible(true);
            }
        });
    }
}
