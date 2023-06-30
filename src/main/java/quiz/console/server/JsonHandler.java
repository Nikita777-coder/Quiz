package quiz.console.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
class JsonHandler {
    private final Gson GSON = new Gson();
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

    public void parseAnswersAndQuestions(@NonNull String output, List<AnswerAndQuestion> questionsAndAnswers) {
        JsonArray jsonArray = GSON.fromJson(output, JsonArray.class);

        for (int i = 0; i < jsonArray.size(); ++i) {
            try {
                AnswerAndQuestion answerAndQuestion = getAnswerAndQuestion(jsonArray.get(i), i);
                questionsAndAnswers.add(answerAndQuestion);
                log.info("ok getting new AnswerAndQuestion");
            } catch (IllegalArgumentException ex) {
                log.error(ex.getMessage(), ex);
            }
        }

        log.info("ok parsing of content from current path");
    }
}
