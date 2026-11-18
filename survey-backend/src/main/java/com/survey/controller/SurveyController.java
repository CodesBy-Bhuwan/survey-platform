
package com.survey.controller;

import com.survey.dto.request.CreateSurveyReq;
import com.survey.dto.request.UpdateSurveyReq;
import com.survey.dto.response.SurveyDto;
import com.survey.dto.response.SurveySummaryDto;
import com.survey.service.SurveyService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    // Create a survey
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SurveyDto create(
            @Valid @RequestBody CreateSurveyReq request
    ) {

        return surveyService.create(request);
    }

    // Get all surveys
    @GetMapping
    public List<SurveySummaryDto> findAll() {

        return surveyService.findAll();
    }

    // Get a survey by ID
    @GetMapping("/{id}")
    public SurveyDto findById(
            @PathVariable String id
    ) {

        return surveyService.findById(id);
    }

    // Update a draft survey
    @PutMapping("/{id}")
    public SurveyDto update(
            @PathVariable String id,
            @Valid @RequestBody UpdateSurveyReq request
    ) {

        return surveyService.update(id, request);
    }

    // Publish a survey
    @PatchMapping("/{id}/publish")
    public Map<String, String> publish(
            @PathVariable String id
    ) {

        surveyService.publish(id);

        return Map.of(
                "message",
                "Survey published successfully."
        );
    }

    // Close a survey
    @PatchMapping("/{id}/close")
    public Map<String, String> close(
            @PathVariable String id
    ) {

        surveyService.close(id);

        return Map.of(
                "message",
                "Survey closed successfully."
        );
    }

    // Delete a survey
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String id
    ) {

        surveyService.delete(id);
    }
}
