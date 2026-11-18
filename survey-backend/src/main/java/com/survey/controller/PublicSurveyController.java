
package com.survey.controller;

import com.survey.dto.response.SurveyDto;
import com.survey.service.SurveyService;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/surveys")
public class PublicSurveyController {

    private final SurveyService surveyService;

    public PublicSurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping("/{id}")
    public SurveyDto getPublishedSurvey(
            @PathVariable UUID id
    ) {

        return surveyService.toDto(
                surveyService.getPublishedSurvey(id)
        );
    }
}
