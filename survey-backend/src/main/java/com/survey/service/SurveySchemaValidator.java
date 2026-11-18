package com.survey.service;

import tools.jackson.databind.JsonNode;

import com.survey.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class SurveySchemaValidator {

    private static final Set<String> TYPES = Set.of(
            "TEXT",
            "SINGLE_CHOICE",
            "MULTI_SELECT",
            "RATING"
    );

    public void validateSchema(JsonNode schema) {

        if (schema == null
                || !schema.isObject()
                || !schema.path("questions").isArray()) {

            throw new BadRequestException(
                    "Schema must contain a questions array."
            );
        }

        JsonNode questions = schema.path("questions");

        if (questions.size() == 0) {
            throw new BadRequestException(
                    "A survey must contain at least one question."
            );
        }

        Set<String> questionIds = new HashSet<>();
        Set<String> earlierQuestionIds = new HashSet<>();

        for (JsonNode question : questions) {

            String id = text(question, "id");
            String title = text(question, "title");
            String type = text(question, "type");

            if (id.isBlank() || title.isBlank()) {
                throw new BadRequestException(
                        "Every question needs an ID and title."
                );
            }

            if (!questionIds.add(id)) {
                throw new BadRequestException(
                        "Duplicate question ID: " + id
                );
            }

            if (!TYPES.contains(type)) {
                throw new BadRequestException(
                        "Unsupported question type: " + type
                );
            }

            validateOptions(question, id, type);
            validateCondition(
                    question,
                    id,
                    earlierQuestionIds
            );

            earlierQuestionIds.add(id);
        }
    }

    private void validateOptions(
            JsonNode question,
            String id,
            String type
    ) {

        if (!type.equals("SINGLE_CHOICE")
                && !type.equals("MULTI_SELECT")) {
            return;
        }

        JsonNode options = question.path("options");

        if (!options.isArray() || options.size() == 0) {
            throw new BadRequestException(
                    "Question " + id + " needs at least one option."
            );
        }

        Set<String> uniqueOptions = new HashSet<>();

        for (JsonNode option : options) {

            if (!option.isTextual()
                    || option.asText().isBlank()
                    || !uniqueOptions.add(option.asText())) {

                throw new BadRequestException(
                        "Options for " + id
                                + " must be non-empty and unique."
                );
            }
        }
    }

    private void validateCondition(
            JsonNode question,
            String id,
            Set<String> earlierQuestionIds
    ) {

        JsonNode condition = question.get("condition");

        if (condition == null || condition.isNull()) {
            return;
        }

        String parentId = text(condition, "questionId");
        String operator = text(condition, "operator");

        if (!earlierQuestionIds.contains(parentId)) {
            throw new BadRequestException(
                    "Conditional question " + id
                            + " must reference an earlier question."
            );
        }

        if (!operator.equals("EQUALS")
                && !operator.equals("NOT_EQUALS")) {

            throw new BadRequestException(
                    "Unsupported condition operator for " + id
            );
        }

        if (!condition.hasNonNull("value")) {
            throw new BadRequestException(
                    "Condition for " + id + " needs a value."
            );
        }
    }

    private String text(JsonNode node, String field) {

        JsonNode value = node.get(field);

        if (value == null || !value.isTextual()) {
            return "";
        }

        return value.asText().trim();
    }
}
