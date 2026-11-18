
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

    public SurveyService(
            SurveyRepo surveyRepository,
            SurveyResponseRepo responseRepository,
            JsonService jsonService,
            SurveySchemaValidator schemaValidator
    ) {

        this.surveyRepo = surveyRepository;
        this.responseRepo = responseRepository;
        this.jsonService = jsonService;
        this.schemaValidator = schemaValidator;
    }

    @Transactional
    public SurveyDto create(CreateSurveyReq request) {

        schemaValidator.validateSchema(request.schema());

        Survey survey = new Survey(
                request.title().trim(),
                request.description(),
                jsonService.write(request.schema())
        );

        return toDto(surveyRepo.save(survey));
    }

    @Transactional(readOnly = true)
    public List<SurveySummaryDto> findAll() {

        return surveyRepo.findAll()
                .stream()
                .map(SurveySummaryDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SurveyDto findById(String id) {

        return toDto(getSurvey(id));
    }

    @Transactional
    public SurveyDto update(
            String id,
            UpdateSurveyReq request
    ) {

        Survey survey = getSurvey(id);

        if (survey.getStatus() != SurveyStatus.DRAFT) {
            throw new BadRequestException(
                    "Only draft surveys can be edited."
            );
        }

        schemaValidator.validateSchema(request.schema());

        survey.updateDetails(
                request.title().trim(),
                request.description(),
                jsonService.write(request.schema())
        );

        return toDto(surveyRepo.save(survey));
    }

    @Transactional
    public void publish(String id) {

        Survey survey = getSurvey(id);

        if (survey.getStatus() != SurveyStatus.DRAFT) {
            throw new BadRequestException(
                    "Only draft surveys can be published."
            );
        }

        schemaValidator.validateSchema(
                jsonService.read(survey.getSchemaJson())
        );

        survey.publish();
    }

    @Transactional
    public void close(String id) {

        Survey survey = getSurvey(id);

        if (survey.getStatus() != SurveyStatus.PUBLISHED) {
            throw new BadRequestException(
                    "Only published surveys can be closed."
            );
        }

        survey.close();
    }

    @Transactional
    public void delete(String id) {

        Survey survey = getSurvey(id);

        // Remove responses before deleting the survey.
        responseRepo.deleteBySurvey_Id(id);

        surveyRepo.delete(survey);
    }

    @Transactional(readOnly = true)
    public Survey getSurvey(String id) {

        return surveyRepo.findById(UUID.fromString(id))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Survey not found: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public Survey getPublishedSurvey(String id) {

        Survey survey = getSurvey(id);

        if (survey.getStatus() != SurveyStatus.PUBLISHED) {
            throw new ResourceNotFoundException(
                    "Published survey not found: " + id
            );
        }

        return survey;
    }

    public SurveyDto toDto(Survey survey) {

        JsonNode schema = jsonService.read(
                survey.getSchemaJson()
        );

        return SurveyDto.from(survey, schema);
    }
}
