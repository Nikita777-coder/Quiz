package quiz;

import javax.validation.constraints.NotNull;

public record AnswerAndQuestion(@NotNull String question, @NotNull String answer) {
}
