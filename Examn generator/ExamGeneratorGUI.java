import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExamGeneratorGUI extends JFrame {
    private JPanel partPanel;
    private JLabel partLabel;
    private JLabel timerLabel;
    private JLabel questionLabel;
    private JPanel answerPanel;
    private List<Question> part1Questions;
    private List<Question> part2Questions;
    private int currentQuestionNumber = 0;
    private int remainingTime = 30 * 60;

    public ExamGeneratorGUI(String part1FilePath, String part2FilePath) {
        part1Questions = readQuestionsFromFile(part1FilePath, 15,3);
        part2Questions = readQuestionsFromFile(part2FilePath, 15, 4);

        setTitle("Exam Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 800);
        setLocationRelativeTo(null);

        partLabel = new JLabel("", SwingConstants.CENTER);
        partLabel.setFont(new Font("Arial", Font.BOLD, 40));
        partLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel = new JLabel("", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        partPanel = new JPanel(new BorderLayout());
        partPanel.add(partLabel, BorderLayout.NORTH);
        partPanel.add(timerLabel, BorderLayout.CENTER);
        questionLabel = new JLabel("", SwingConstants.LEFT);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        answerPanel = new JPanel();
        answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));


        JButton prevButton = new JButton("Inapoi");
        JButton nextButton = new JButton("Inainte");
        JButton scoreButton = new JButton("Calculeaza scorul");


        setLayout(new GridLayout(3, 2));


        add(questionLabel);
        add(partPanel);
        add(answerPanel);
        add(scoreButton);
        add(prevButton);
        add(nextButton);


        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Move to previous question
                if (currentQuestionNumber > 0) {
                    currentQuestionNumber--;
                    updateQuestion();
                }
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Move to next question
                if (currentQuestionNumber < part1Questions.size() + part2Questions.size() - 1) {
                    currentQuestionNumber++;
                    updateQuestion();
                }
            }
        });

        scoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int score = calculateScore();
                int totalQuestions = part1Questions.size() + part2Questions.size();
                JOptionPane.showMessageDialog(ExamGeneratorGUI.this, "Scor final: " + score + "/" + 30);
            }
        });


        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingTime--;
                if (remainingTime <= 0) {
                    ((Timer) e.getSource()).stop();
                    int score = calculateScore();
                    int totalQuestions = part1Questions.size() + part2Questions.size();
                    JOptionPane.showMessageDialog(ExamGeneratorGUI.this, "Scor final: " + score + "/" + 30);
                    dispose(); // Close the frame
                } else {
                    updateTimerLabel();
                }
            }
        });
        timer.start();


        shuffleQuestions();
        updateQuestion();
    }

    private List<Question> readQuestionsFromFile(String filePath, int numberOfQuestions, int numberOfAnswers) {
        List<Question> questions = new ArrayList<>();
        int questionsRead = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder questionBuilder = new StringBuilder();
            List<String> answers = new ArrayList<>();
            List<Boolean> correctAnswers = new ArrayList<>();
            String imagePath = "";
            boolean isQuestion = true;
            boolean hasImage = false;
            while ((line = reader.readLine()) != null && questionsRead < numberOfQuestions) {
                if (!line.trim().isEmpty()) {
                    if (isQuestion) {
                        questionBuilder.append(line).append("\n");
                        isQuestion = false;
                    } else {
                        if (!hasImage) {
                            imagePath = line.trim();
                            hasImage = true;
                        } else {

                            char lastChar = line.trim().charAt(line.trim().length() - 1);
                            boolean isCorrect = lastChar == '!';
                            if (isCorrect) {
                                line = line.substring(0, line.length() - 1);
                            }
                            correctAnswers.add(isCorrect);
                            answers.add(line);
                            if (answers.size() == numberOfAnswers) {

                                questions.add(new Question(questionBuilder.toString(), new ArrayList<>(answers), new ArrayList<>(correctAnswers), imagePath));

                                questionBuilder.setLength(0);
                                answers.clear();
                                correctAnswers.clear();
                                isQuestion = true;
                                hasImage = false;
                                questionsRead++;
                            }
                        }
                    }
                } else {
                    isQuestion = true;
                }
            }

            if (questionBuilder.length() > 0 && !answers.isEmpty() && !correctAnswers.isEmpty() && questionsRead < numberOfQuestions) {
                questions.add(new Question(questionBuilder.toString(), new ArrayList<>(answers), new ArrayList<>(correctAnswers), imagePath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.shuffle(questions);

        return questions;
    }


    private void updateTimerLabel() {
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText("Time Remaining: " + timeString);
    }

    private int calculateScore() {
        int score = 0;
        for (Question question : part1Questions) {
            if (isQuestionCorrect(question)) {
                score++;
            }
        }
        for (Question question : part2Questions) {
            if (isQuestionCorrect(question)) {
                score++;
            }
        }
        return score;
    }

    private boolean isQuestionCorrect(Question question) {
        List<Boolean> selectedStates = question.getSelectedStates();
        List<Boolean> correctAnswers = question.getCorrectAnswers();
        for (int i = 0; i < selectedStates.size(); i++) {
            if (selectedStates.get(i) != correctAnswers.get(i)) {
                return false;
            }
        }
        return true;
    }

    private void updateQuestion() {
        if (currentQuestionNumber < part1Questions.size()) {
            partLabel.setText("Partea 1");
            displayQuestion(part1Questions.get(currentQuestionNumber));
        } else {
            partLabel.setText("Partea 2");
            int index = currentQuestionNumber - part1Questions.size();
            displayQuestion(part2Questions.get(index));
        }
    }

    private void displayQuestion(Question question) {

        questionLabel.removeAll();
        questionLabel.setLayout(new BorderLayout());


        String imagePath = question.getImagePath();
        if (!imagePath.isEmpty()) {
            try {
                ImageIcon imageIcon = new ImageIcon(imagePath);
                Image image = imageIcon.getImage();
                Image scaledImage = image.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                questionLabel.add(imageLabel, BorderLayout.WEST);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                e.printStackTrace();
            }
        }


        JLabel textLabel = new JLabel("<html>" + question.getQuestion() + "</html>");
        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        questionLabel.add(textLabel, BorderLayout.CENTER);

        updateAnswerPanel(question);
    }


    private void updateAnswerPanel(Question currentQuestion) {
        answerPanel.removeAll();

        List<String> answers = currentQuestion.getAnswers();
        List<Boolean> selectedStates = currentQuestion.getSelectedStates();

        for (int i = 0; i < answers.size(); i++) {
            String answer = answers.get(i);
            JPanel answerPanelRow = new JPanel();
            answerPanelRow.setLayout(new FlowLayout(FlowLayout.LEFT));

            JCheckBox checkBox = new JCheckBox(answer);
            checkBox.setFont(new Font("Arial", Font.PLAIN, 20));
            checkBox.setSelected(selectedStates.get(i));
            int finalI = i;
            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentQuestion.setSelectedState(finalI, checkBox.isSelected());
                }
            });
            answerPanelRow.add(checkBox);
            answerPanel.add(answerPanelRow);
        }
        answerPanel.revalidate();
        answerPanel.repaint();
    }

    private void shuffleQuestions() {
        Collections.shuffle(part1Questions);
        Collections.shuffle(part2Questions);
    }


}
