
package com.survey.dto.response;

import com.survey.entity.Survey;
import com.survey.entity.enums.SurveyStatus;

import java.time.LocalDateTime;

public record SurveySummaryDto(

        String id,
        String title,
        String description,
        SurveyStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {

    public static SurveySummaryDto from(Survey survey) {
        return new SurveySummaryDto(
                survey.getId().toString(),
                survey.getTitle(),
                survey.getDescription(),
                survey.getStatus(),
                survey.getCreatedAt(),
                survey.getUpdatedAt()
        );
    }
}
