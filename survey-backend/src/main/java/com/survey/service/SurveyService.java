
package com.survey.service;

import tools.jackson.databind.JsonNode;

import com.survey.dto.request.CreateSurveyReq;
import com.survey.dto.request.UpdateSurveyReq;
import com.survey.dto.response.SurveyDto;
import com.survey.dto.response.SurveySummaryDto;

import com.survey.entity.Survey;
import com.survey.entity.enums.SurveyStatus;

import com.survey.exception.BadRequestException;
import com.survey.exception.ResourceNotFoundException;

import com.survey.repo.SurveyRepo;
import com.survey.repo.SurveyResponseRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SurveyService {

    private final SurveyRepo surveyRepo;
    private final SurveyResponseRepo responseRepo;
    private final JsonService jsonService;
    private final SurveySchemaValidator schemaValidator;

    // Constructor injection for all required dependencies.
    public SurveyService(
            SurveyRepo surveyRepo,
            SurveyResponseRepo responseRepo,
            JsonService jsonService,
            SurveySchemaValidator schemaValidator
    ) {
        this.surveyRepo = surveyRepo;
        this.responseRepo = responseRepo;
        this.jsonService = jsonService;
        this.schemaValidator = schemaValidator;
    }

    // ---------------------------------------------------------
    // CREATE SURVEY
    // ---------------------------------------------------------

    @Transactional
    public SurveyDto create(CreateSurveyReq request) {

        // Validate the questions and survey schema before saving.
        schemaValidator.validateSchema(request.schema());

        // Create a new survey.
        // Hibernate automatically generates its UUID.
        Survey survey = new Survey(
                request.title().trim(),
                request.description(),
                jsonService.write(request.schema())
        );

        // Save the survey and return its DTO.
        Survey savedSurvey = surveyRepo.save(survey);

        return toDto(savedSurvey);
    }

    // ---------------------------------------------------------
    // GET ALL SURVEYS
    // ---------------------------------------------------------

    @Transactional(readOnly = true)
    public List<SurveySummaryDto> findAll() {

        // Retrieve all surveys and convert each entity to a summary DTO.
        return surveyRepo.findAll()
                .stream()
                .map(SurveySummaryDto::from)
                .toList();
    }

    // ---------------------------------------------------------
    // GET SURVEY BY ID
    // ---------------------------------------------------------

    @Transactional(readOnly = true)
    public SurveyDto findById(UUID id) {

        // Retrieve the survey using its UUID.
        Survey survey = getSurvey(id);

        return toDto(survey);
    }

    // ---------------------------------------------------------
    // UPDATE SURVEY
    // ---------------------------------------------------------

    @Transactional
    public SurveyDto update(
            UUID id,
            UpdateSurveyReq request
    ) {

        // Find the existing survey.
        Survey survey = getSurvey(id);

        // Only draft surveys can be edited.
        if (survey.getStatus() != SurveyStatus.DRAFT) {
            throw new BadRequestException(
                    "Only draft surveys can be edited."
            );
        }

        // Validate the updated question schema.
        schemaValidator.validateSchema(request.schema());

        // Update survey information.
        survey.updateDetails(
                request.title().trim(),
                request.description(),
                jsonService.write(request.schema())
        );

        // The entity is managed by JPA within this transaction.
        return toDto(surveyRepo.save(survey));
    }

    // ---------------------------------------------------------
    // PUBLISH SURVEY
    // ---------------------------------------------------------

    @Transactional
    public void publish(UUID id) {

        // Find the survey.
        Survey survey = getSurvey(id);

        // Only draft surveys can be published.
        if (survey.getStatus() != SurveyStatus.DRAFT) {
            throw new BadRequestException(
                    "Only draft surveys can be published."
            );
        }

        // Validate the stored schema before publishing.
        schemaValidator.validateSchema(
                jsonService.read(survey.getSchemaJson())
        );

        // Change the status to PUBLISHED.
        survey.publish();
    }

    // ---------------------------------------------------------
    // CLOSE SURVEY
    // ---------------------------------------------------------

    @Transactional
    public void close(UUID id) {

        // Find the survey.
        Survey survey = getSurvey(id);

        // Only published surveys can be closed.
        if (survey.getStatus() != SurveyStatus.PUBLISHED) {
            throw new BadRequestException(
                    "Only published surveys can be closed."
            );
        }

        // Change the status to CLOSED.
        survey.close();
    }

    // ---------------------------------------------------------
    // DELETE SURVEY
    // ---------------------------------------------------------

    @Transactional
    public void delete(UUID id) {

        // Find the survey before deleting it.
        Survey survey = getSurvey(id);

        // Delete associated responses first.
        // Both the survey ID and repository parameter are UUID.
        responseRepo.deleteBySurvey_Id(id);

        // Delete the survey after its responses.
        surveyRepo.delete(survey);
    }

    // ---------------------------------------------------------
    // FIND SURVEY ENTITY
    // ---------------------------------------------------------

    @Transactional(readOnly = true)
    public Survey getSurvey(UUID id) {

        // Retrieve the survey using its UUID primary key.
        // Throw an exception if no survey exists.
        return surveyRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Survey not found: " + id
                        )
                );
    }

    // ---------------------------------------------------------
    // GET PUBLISHED SURVEY
    // ---------------------------------------------------------

    @Transactional(readOnly = true)
    public Survey getPublishedSurvey(UUID id) {

        // First retrieve the survey.
        Survey survey = getSurvey(id);

        // Public users can only access published surveys.
        if (survey.getStatus() != SurveyStatus.PUBLISHED) {
            throw new ResourceNotFoundException(
                    "Published survey not found: " + id
            );
        }

        return survey;
    }

    // ---------------------------------------------------------
    // CONVERT SURVEY ENTITY TO DTO
    // ---------------------------------------------------------

    public SurveyDto toDto(Survey survey) {

        // Convert the stored JSON schema back into a JsonNode.
        JsonNode schema = jsonService.read(
                survey.getSchemaJson()
        );

        // Create and return the response DTO.
        return SurveyDto.from(survey, schema);
    }
}
