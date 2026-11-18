
package com.survey.dto.response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record AnalyticsDto(

        UUID surveyId,
        long totalResponses,
        List<QuestionAnalytics> questions

) {

    public record QuestionAnalytics(

            String questionId,
            String title,
            String type,
            long answeredCount,
            Map<String, Long> optionCounts,
            Double averageRating,
            List<String> textAnswers

    ) {
    }
}
