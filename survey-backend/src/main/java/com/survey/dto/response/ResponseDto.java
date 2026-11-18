
package com.survey.dto.response;

import tools.jackson.databind.JsonNode;
import com.survey.entity.SurveyResponse;

import java.time.LocalDateTime;

public record ResponseDto(

        String id,
        String surveyId,
        JsonNode answers,
        LocalDateTime submittedAt

) {

    public static ResponseDto from(
            SurveyResponse response,
            JsonNode answers
    ) {
        return new ResponseDto(
                response.getId(),
                response.getSurvey().getId().toString(),
                answers,
                response.getSubmittedAt()
        );
    }
}
