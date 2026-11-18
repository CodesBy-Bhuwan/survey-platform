
package com.survey.service;

import tools.jackson.databind.JsonNode;

import com.survey.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ResponseAnswerValidator {

    public void validate(
            JsonNode schema,
            JsonNode answers
    ) {

        if (answers == null || !answers.isObject()) {
            throw new BadRequestException(
                    "Answers must be a JSON object keyed by question ID."
            );
        }

        Set<String> knownIds = new HashSet<>();

        JsonNode questions = schema.path("questions");

        for (JsonNode question : questions) {

            String id = question.path("id").asText();
            knownIds.add(id);

            boolean visible = isVisible(question, answers);

            JsonNode answer = answers.get(id);

            if (!visible) {

                if (answer != null && !answer.isNull()) {
                    throw new BadRequestException(
                            "Answer supplied for hidden question: " + id
                    );
                }

                continue;
            }

            boolean required = question.path("required")
                    .asBoolean(false);

            boolean unanswered =
                    answer == null
                            || answer.isNull()
                            || (answer.isTextual()
                            && answer.asText().isBlank())
                            || (answer.isArray()
                            && answer.size() == 0);

            if (unanswered) {

                if (required) {
                    throw new BadRequestException(
                            "Required question is unanswered: " + id
                    );
                }

                continue;
            }

            validateType(question, answer);
        }

        // Check for unknown question IDs
        for (Map.Entry<String, JsonNode> entry : answers.properties()) {

            String id = entry.getKey();

            if (!knownIds.contains(id)) {
                throw new BadRequestException(
                        "Unknown question ID: " + id
                );
            }
        }
    }

    private boolean isVisible(
            JsonNode question,
            JsonNode answers
    ) {

        JsonNode condition = question.get("condition");

        if (condition == null || condition.isNull()) {
            return true;
        }

        String parentId = condition.path("questionId").asText();

        JsonNode parentAnswer = answers.get(parentId);

        if (parentAnswer == null || parentAnswer.isNull()) {
            return false;
        }

        JsonNode expected = condition.get("value");

        boolean matches = parentAnswer.equals(expected);

        String operator = condition.path("operator").asText();

        return operator.equals("EQUALS")
                ? matches
                : !matches;
    }

    private void validateType(
            JsonNode question,
            JsonNode answer
    ) {

        String id = question.path("id").asText();
        String type = question.path("type").asText();

        JsonNode options = question.path("options");

        switch (type) {

            case "TEXT" -> {

                if (!answer.isTextual()) {
                    throw new BadRequestException(
                            "Question " + id + " expects text."
                    );
                }
            }

            case "SINGLE_CHOICE" -> {

                if (!answer.isTextual()
                        || !containsOption(options, answer.asText())) {

                    throw new BadRequestException(
                            "Invalid selected option for question " + id
                    );
                }
            }

            case "MULTI_SELECT" -> {

                if (!answer.isArray() || answer.size() == 0) {
                    throw new BadRequestException(
                            "Question " + id
                                    + " expects an array of selected options."
                    );
                }

                Set<String> selectedOptions = new HashSet<>();

                for (JsonNode selected : answer) {

                    if (!selected.isTextual()
                            || !containsOption(options, selected.asText())
                            || !selectedOptions.add(selected.asText())) {

                        throw new BadRequestException(
                                "Invalid or duplicate option for question " + id
                        );
                    }
                }
            }

            case "RATING" -> {

                if (!answer.isIntegralNumber()
                        || answer.intValue() < 1
                        || answer.intValue() > 5) {

                    throw new BadRequestException(
                            "Question " + id
                                    + " expects an integer rating from 1 to 5."
                    );
                }
            }

            default -> throw new BadRequestException(
                    "Unsupported question type: " + type
            );
        }
    }

    private boolean containsOption(
            JsonNode options,
            String value
    ) {

        for (JsonNode option : options) {

            if (option.isTextual()
                    && option.asText().equals(value)) {
                return true;
            }
        }

        return false;
    }
}
