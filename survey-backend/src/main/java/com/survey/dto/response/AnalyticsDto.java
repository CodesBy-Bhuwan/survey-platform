
package com.survey.dto.response;

import java.util.List;
import java.util.Map;

public record AnalyticsDto(

        String surveyId,
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
