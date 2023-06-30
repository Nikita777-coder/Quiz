package quiz.console.server;

import lombok.extern.slf4j.Slf4j;
import quiz.console.Formatter;
import quiz.console.server.communicator.Communicator;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Server {
    private final int COUNT_OF_QUESTIONS = 10;
    private final List<AnswerAndQuestion> questionsAndAnswers;
    private final Communicator communicator;
    private int countOfAnsweredQuestions, countOfCorrectAnsweredQuestions;
    private String currentVerdict;
    private final JsonHandler jsonHandler;
    private String getCurrentQuestion() {
        String q = questionsAndAnswers.get(countOfAnsweredQuestions).question();
        log.info("ok getting of question");
        return q;
    }
    private String getCurrentAnswer() {
        String ans = questionsAndAnswers.get(countOfAnsweredQuestions).answer();
        log.info("ok getting of answer");
        return ans;
    }
    private void addQuestionsToArr() {
        communicator.setPathToConnect("http://jservice.io/api/random?count=" +
                (COUNT_OF_QUESTIONS - questionsAndAnswers.size()));
        jsonHandler.parseAnswersAndQuestions(communicator.getQuestionsAndAnswersFromSource(), questionsAndAnswers);
        log.info("ok connection and parsing of answers and questions");
    }
    private void fillingTheArrayToTheEnd() {
        while (questionsAndAnswers.size() != COUNT_OF_QUESTIONS) {
            addQuestionsToArr();
        }

        log.info("ok filling array with questions and answers");
    }
    public Server() {
        countOfAnsweredQuestions = 0;
        countOfCorrectAnsweredQuestions = 0;
        questionsAndAnswers = new ArrayList<>(COUNT_OF_QUESTIONS);
        communicator = new Communicator();
        jsonHandler = new JsonHandler();
        fillingTheArrayToTheEnd();
        log.info("ok Server initialization");
    }
    public boolean hasQuestion() {
        return countOfAnsweredQuestions != COUNT_OF_QUESTIONS;
    }
    public String getQuestion() {
        return getCurrentQuestion();
    }
    public void handleAnswer(String ans) {
        if (ans == null) {
            throw new NullPointerException("user answer is null!");
        }

        if (ans.equals(getCurrentAnswer())) {
            ++countOfCorrectAnsweredQuestions;
            currentVerdict = "Correct!";
        } else {
            currentVerdict = "Incorrect!";
        }

        ++countOfAnsweredQuestions;
    }
    public String getVerdict() {
        return currentVerdict;
    }
    public void showResults() {
        Formatter.formatResult(COUNT_OF_QUESTIONS, countOfCorrectAnsweredQuestions);
    }
}
