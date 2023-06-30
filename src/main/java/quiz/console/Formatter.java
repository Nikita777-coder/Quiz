package quiz.console;


import jakarta.validation.constraints.NotNull;

public class Formatter {
    public static void formatQuestion(@NotNull String question) {
        System.out.printf("%s\n", question);
    }
    public static void formatAnswer() {
        System.out.print("Answer: ");
    }
    public static void formatVerdict(@NotNull String verdict) {
        System.out.printf("\n%s\n", verdict);
    }
    public static void formatResult(int countOfQuestions, int countOfCorrectAnswers) {
        System.out.printf("\nQuiz completed!\nYour score is: %d correct answers from %d",
                countOfCorrectAnswers, countOfQuestions);
    }
}
