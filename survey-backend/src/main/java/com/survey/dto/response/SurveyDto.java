
package com.survey.dto.response;

import tools.jackson.databind.JsonNode;
import com.survey.entity.Survey;
import com.survey.entity.enums.SurveyStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public record SurveyDto(

        String id,
        String title,
        String description,
        JsonNode schema,
        SurveyStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {

    public static SurveyDto from(
            Survey survey,
            JsonNode schema
    ) {
        return new SurveyDto(
                Objects.toString(survey.getId(), null),
                survey.getTitle(),
                survey.getDescription(),
                schema,
                survey.getStatus(),
                survey.getCreatedAt(),
                survey.getUpdatedAt()
        );
    }
}
