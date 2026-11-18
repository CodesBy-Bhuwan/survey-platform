
package com.survey.service;

import tools.jackson.databind.JsonNode;

import com.survey.dto.response.AnalyticsDto;
import com.survey.entity.Survey;
import com.survey.entity.SurveyResponse;
import com.survey.repo.SurveyResponseRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AnalyticsService {

    private final SurveyService surveyService;
    private final SurveyResponseRepo responseRepo;
    private final JsonService jsonService;

    public AnalyticsService(
            SurveyService surveyService,
            SurveyResponseRepo responseRepository,
            JsonService jsonService
    ) {

        this.surveyService = surveyService;
        this.responseRepo = responseRepository;
        this.jsonService = jsonService;
    }

    @Transactional(readOnly = true)
    public AnalyticsDto getAnalytics(UUID surveyId) {

        Survey survey = surveyService.getSurvey(surveyId);

        JsonNode schema = jsonService.read(
                survey.getSchemaJson()
        );

        List<SurveyResponse> responses =
                responseRepo
                        .findBySurvey_IdOrderBySubmittedAtDesc(surveyId);

        List<AnalyticsDto.QuestionAnalytics> results =
                new ArrayList<>();

        for (JsonNode question : schema.path("questions")) {

            String questionId = question.path("id").asText();
            String title = question.path("title").asText();
            String type = question.path("type").asText();

            Map<String, Long> counts = new LinkedHashMap<>();
            List<String> textAnswers = new ArrayList<>();

            // Initialize choice options with zero counts.
            if (type.equals("SINGLE_CHOICE")
                    || type.equals("MULTI_SELECT")) {

                for (JsonNode option : question.path("options")) {
                    counts.put(option.asText(), 0L);
                }
            }

            long answered = 0;
            long ratingTotal = 0;
            long ratingCount = 0;

            for (SurveyResponse response : responses) {

                JsonNode answers = jsonService.read(
                        response.getAnswersJson()
                );

                JsonNode answer = answers.get(questionId);

                if (answer == null || answer.isNull()) {
                    continue;
                }

                switch (type) {

                    case "TEXT" -> {

                        if (answer.isTextual()
                                && !answer.asText().isBlank()) {

                            textAnswers.add(answer.asText());
                            answered++;
                        }
                    }

                    case "SINGLE_CHOICE" -> {

                        if (answer.isTextual()) {

                            counts.merge(
                                    answer.asText(),
                                    1L,
                                    Long::sum
                            );

                            answered++;
                        }
                    }

                    case "MULTI_SELECT" -> {

                        if (answer.isArray()) {

                            for (JsonNode selected : answer) {

                                counts.merge(
                                        selected.asText(),
                                        1L,
                                        Long::sum
                                );
                            }

                            if (answer.size() > 0) {
                                answered++;
                            }
                        }
                    }

                    case "RATING" -> {

                        if (answer.isIntegralNumber()) {

                            ratingTotal += answer.intValue();
                            ratingCount++;
                            answered++;
                        }
                    }

                    default -> {
                        // No analytics for unsupported question types.
                    }
                }
            }

            Double averageRating = ratingCount == 0
                    ? null
                    : (double) ratingTotal / ratingCount;

            results.add(
                    new AnalyticsDto.QuestionAnalytics(
                            questionId,
                            title,
                            type,
                            answered,
                            counts,
                            averageRating,
                            textAnswers
                    )
            );
        }

        return new AnalyticsDto(
                surveyId,
                responses.size(),
                results
        );
    }
}
