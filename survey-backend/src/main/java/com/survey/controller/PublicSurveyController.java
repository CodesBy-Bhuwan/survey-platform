
package com.survey.controller;

import com.survey.dto.response.SurveyDto;
import com.survey.service.SurveyService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/surveys")
public class PublicSurveyController {

    private final SurveyService surveyService;

    public PublicSurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping("/{id}")
    public SurveyDto getPublishedSurvey(
            @PathVariable String id
    ) {

        return surveyService.toDto(
                surveyService.getPublishedSurvey(id)
        );
    }
}
