package quiz.console.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.List;

@Slf4j
class JsonHandler {
    private final Gson GSON = new Gson();

    public void parseAnswersAndQuestions(@NonNull String output, List<AnswerAndQuestion> questionsAndAnswers) {
        Type type = new TypeToken<List<AnswerAndQuestion>>(){}.getType();
        List<AnswerAndQuestion> arr = GSON.fromJson(output, type);

        for (AnswerAndQuestion answerAndQuestion : arr) {
            try {
                questionsAndAnswers.add(answerAndQuestion);
                log.info("ok getting new AnswerAndQuestion");
            } catch (IllegalArgumentException ex) {
                log.error(ex.getMessage(), ex);
            }
        }

        log.info("ok parsing of content from current path");
    }
}
