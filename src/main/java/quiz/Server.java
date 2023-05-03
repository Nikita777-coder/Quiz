package quiz;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Server {
    private final static Logger QUIZ_LOGGER = LoggerFactory.getLogger(Server.class);
    private String currentPath;
    private final int COUNT_OF_QUESTIONS = 10;
    private final List<AnswerAndQuestion> questionsAndAnswers;
    private int countOfAnsweredQuestions, countOfCorrectAnsweredQuestions;
    private String currentVerdict;
    private final Gson GSON = new Gson();
    private final JsonHandler jsonHandler;
    private class JsonHandler {
        private final static Logger JSON_HANDLER_LOGGER = LoggerFactory.getLogger(JsonHandler.class);
        private AnswerAndQuestion getAnswerAndQuestion(JsonElement jsonElement, int i) {
            JsonObject jsonObject = GSON.fromJson(jsonElement, JsonObject.class);

            if (jsonObject == null) {
                throw new IllegalArgumentException(String.format("JsonElement[%d] is null!", i));
            }

            int id;

            if (jsonObject.get("id") == null) {
                throw new IllegalArgumentException("json str ans hasn't id");
            } else {
                id = jsonObject.get("id").getAsInt();
            }

            if (jsonObject.get("question") == null) {
                throw new IllegalArgumentException(String.format("Json object with id = %d hasn't question ", id));
            }

            String question = jsonObject.get("question").getAsString();

            if (jsonObject.get("answer") == null) {
                throw new IllegalArgumentException(String.format("Json object with id = %d hasn't answer ", id));
            }

            String answer = jsonObject.get("answer").getAsString();

            return new AnswerAndQuestion(question, answer);
        }

        private void parseAnswersAndQuestions(String output) {
            if (output == null) {
                JSON_HANDLER_LOGGER.error(String.format("Json content from %s current url is null", currentPath));
            }

            JsonArray jsonArray = GSON.fromJson(output, JsonArray.class);

            for (int i = 0; i < COUNT_OF_QUESTIONS; ++i) {
                try {
                    AnswerAndQuestion answerAndQuestion = getAnswerAndQuestion(jsonArray.get(i), i);
                    questionsAndAnswers.add(answerAndQuestion);
                } catch (IllegalArgumentException ex) {
                    JSON_HANDLER_LOGGER.error(ex.getMessage(), ex);
                }
            }

            JSON_HANDLER_LOGGER.info("ok parsing of content from current path");
        }
    }
    private static class Service {
        private final static Logger SERVICE_LOGGER = LoggerFactory.getLogger(Service.class);
        private static int countOfReconnections = 0;
        private static HttpURLConnection conn;
        private static void getConnectionToSource(URL urlObj) throws IOException, InterruptedException {
            conn = (HttpURLConnection) urlObj.openConnection();
            SERVICE_LOGGER.info("Successful connections to url");

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            while (conn.getResponseCode() != 200 && countOfReconnections < 20) {
                ++countOfReconnections;
                SERVICE_LOGGER.warn(String.format("Failed: HTTP error code from page %s: %d", urlObj.getPath(), conn.getResponseCode()));

                conn = (HttpURLConnection) urlObj.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.disconnect();

                TimeUnit.MILLISECONDS.sleep(new Random().nextLong(1, Long.MAX_VALUE));
            }

            if (countOfReconnections == 20) {
                SERVICE_LOGGER.error(String.format("Fail getting of response from %s", urlObj.getPath()));
            }
        }
        private static String getQuestionsAndAnswersFromSource(String currentPath) throws IOException, InterruptedException {
            URL urlObj = new URL(currentPath);
            getConnectionToSource(urlObj);
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            output = br.readLine();

            conn.disconnect();

            return output;
        }
    }
    private String getCurrentQuestion() {
        String q = questionsAndAnswers.get(countOfAnsweredQuestions).question();
        QUIZ_LOGGER.info("ok getting of question");
        return q;
    }
    private String getCurrentAnswer() {
        String ans = questionsAndAnswers.get(countOfAnsweredQuestions).answer();
        QUIZ_LOGGER.info("ok getting of answer");
        return ans;
    }
    private void addQuestionsToArr() throws IOException, InterruptedException {
        currentPath = "http://jservice.io/api/random?count=" + (COUNT_OF_QUESTIONS - questionsAndAnswers.size());
        jsonHandler.parseAnswersAndQuestions(Service.getQuestionsAndAnswersFromSource(currentPath));
        QUIZ_LOGGER.info("ok connection and parsing of answers and questions");
    }
    private void fillingTheArrayToTheEnd() throws IOException, InterruptedException {
        while (questionsAndAnswers.size() != COUNT_OF_QUESTIONS) {
            addQuestionsToArr();
        }

        QUIZ_LOGGER.info("ok filling array with questions and answers");
    }
    public Server() throws IOException, InterruptedException {
        countOfAnsweredQuestions = 0;
        countOfCorrectAnsweredQuestions = 0;
        questionsAndAnswers = new ArrayList<>(COUNT_OF_QUESTIONS);
        jsonHandler = new JsonHandler();
        fillingTheArrayToTheEnd();
        QUIZ_LOGGER.info("ok Quiz initialization");
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
