
package com.survey.controller;

import com.survey.dto.request.SubmitResponseReq;
import com.survey.dto.response.ResponseDto;
import com.survey.entity.SurveyResponse;
import com.survey.service.ResponseService;
import org.springframework.http.ContentDisposition;
import java.nio.charset.StandardCharsets;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ResponseController {

    private final ResponseService responseService;

    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    // Public API: submit a response to a published survey.
    @PostMapping("/api/public/surveys/{surveyId}/responses")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto submit(
            @PathVariable UUID surveyId,
            @Valid @RequestBody SubmitResponseReq request
    ) {

        return responseService.submit(
                surveyId,
                request.answers()
        );
    }

    // Admin API: retrieve all responses for a survey.
    @GetMapping("/api/admin/surveys/{surveyId}/responses")
    public List<SurveyResponse> findResponses(
            @PathVariable UUID surveyId
    ) {

        return responseService.findBySurvey(surveyId);
    }

    // Admin API: download survey responses as a CSV file.
    // Export is permitted only when the survey is CLOSED.
    @GetMapping("/api/admin/surveys/{surveyId}/responses/export")
    public ResponseEntity<byte[]> exportResponses(
            @PathVariable UUID surveyId
    ) {

        // Generate the CSV file through the service.
        byte[] csvFile = responseService.exportCsv(surveyId);

        // Set a downloadable filename.
        String filename = "survey-responses-" + surveyId + ".csv";

        // Configure the response headers for file download.
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.parseMediaType("text/csv; charset=UTF-8")
        );

        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(filename, StandardCharsets.UTF_8)
                        .build()
        );

        headers.setContentLength(csvFile.length);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(csvFile);
    }
}
