
package com.survey.service;

import tools.jackson.databind.JsonNode;

import com.survey.dto.response.ResponseDto;
import com.survey.entity.Survey;
import com.survey.entity.SurveyResponse;
import com.survey.repo.SurveyResponseRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResponseService {

    private final SurveyService surveyService;
    private final SurveyResponseRepo responseRepository;
    private final JsonService jsonService;
    private final ResponseAnswerValidator answerValidator;

    public ResponseService(
            SurveyService surveyService,
            SurveyResponseRepo responseRepository,
            JsonService jsonService,
            ResponseAnswerValidator answerValidator
    ) {

        this.surveyService = surveyService;
        this.responseRepository = responseRepository;
        this.jsonService = jsonService;
        this.answerValidator = answerValidator;
    }

    @Transactional
    public ResponseDto submit(
            String surveyId,
            JsonNode answers
    ) {

        Survey survey = surveyService.getPublishedSurvey(surveyId);

        JsonNode schema = jsonService.read(
                survey.getSchemaJson()
        );

        answerValidator.validate(schema, answers);

        SurveyResponse response = new SurveyResponse(
                survey,
                jsonService.write(answers)
        );

        SurveyResponse savedResponse =
                responseRepository.save(response);

        return ResponseDto.from(savedResponse, answers);
    }

    @Transactional(readOnly = true)
    public List<ResponseDto> findBySurvey(String surveyId) {

        surveyService.getSurvey(surveyId);

        return responseRepository
                .findBySurvey_IdOrderBySubmittedAtDesc(surveyId)
                .stream()
                .map(response -> ResponseDto.from(
                        response,
                        jsonService.read(response.getAnswersJson())
                ))
                .toList();
    }
}
