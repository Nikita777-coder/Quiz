package quiz.console.server;

import lombok.NonNull;

public record AnswerAndQuestion(@NonNull String question, @NonNull String answer) {
}
