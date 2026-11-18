
package com.survey.controller;

import com.survey.dto.request.SubmitResponseReq;
import com.survey.dto.response.ResponseDto;
import com.survey.service.ResponseService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ResponseController {

    private final ResponseService responseService;

    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    // Public response submission
    @PostMapping("/api/public/surveys/{surveyId}/responses")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto submit(
            @PathVariable String surveyId,
            @Valid @RequestBody SubmitResponseReq request
    ) {

        return responseService.submit(
                surveyId,
                request.answers()
        );
    }

    // Admin: retrieve survey responses
    @GetMapping("/api/admin/surveys/{surveyId}/responses")
    public List<ResponseDto> findResponses(
            @PathVariable String surveyId
    ) {

        return responseService.findBySurvey(surveyId);
    }
}
