
package com.survey.service;

import tools.jackson.databind.JsonNode;

import com.survey.dto.response.ResponseDto;
import com.survey.entity.Survey;
import com.survey.entity.SurveyResponse;
import com.survey.entity.enums.SurveyStatus;
import com.survey.repo.SurveyResponseRepo;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    // Submit a response to a published survey.
    @Transactional
    public ResponseDto submit(
            UUID surveyId,
            JsonNode answers
    ) {

        // Retrieve the published survey using its UUID.
        Survey survey = surveyService.getPublishedSurvey(surveyId);

        // Read the survey's question schema.
        JsonNode schema = jsonService.read(
                survey.getSchemaJson()
        );

        // Validate answers against the survey schema.
        answerValidator.validate(schema, answers);

        // Create a response entity.
        SurveyResponse response = new SurveyResponse(
                survey,
                jsonService.write(answers)
        );

        // Save the submitted response.
        SurveyResponse savedResponse =
                responseRepository.save(response);

        // Return the response DTO.
        return ResponseDto.from(savedResponse, answers);
    }

    // Retrieve all responses for one survey.
    @Transactional(readOnly = true)
    public List<SurveyResponse> findBySurvey(UUID surveyId) {

        // Verify that the survey exists.
        surveyService.getSurvey(surveyId);

        // Retrieve its responses, newest first.
        return responseRepository
                .findBySurvey_IdOrderBySubmittedAtDesc(surveyId);
    }

    // Export all responses of a closed survey as a CSV file.
    @Transactional(readOnly = true)
    public byte[] exportCsv(UUID surveyId) {

        // Retrieve the survey.
        Survey survey = surveyService.getSurvey(surveyId);

        // Prevent exporting surveys that are not closed.
        if (survey.getStatus() != SurveyStatus.CLOSED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Responses can only be exported after the survey is closed."
            );
        }

        // Read the survey question schema.
        JsonNode schema = jsonService.read(
                survey.getSchemaJson()
        );

        // Extract the questions from the schema.
        JsonNode questions = schema.path("questions");

        if (!questions.isArray()) {
            throw new IllegalStateException(
                    "The survey question schema is invalid."
            );
        }

        // Retrieve all submitted responses.
        List<SurveyResponse> responses =
                responseRepository
                        .findBySurvey_IdOrderBySubmittedAtDesc(surveyId);

        // Build the CSV content.
        StringBuilder csv = new StringBuilder();

        // UTF-8 BOM improves compatibility with Microsoft Excel.
        csv.append('\uFEFF');

        // First column contains the submission date.
        List<String> headers = new ArrayList<>();
        headers.add("Submitted At");

        // Add one column for every survey question.
        for (JsonNode question : questions) {
            headers.add(question.path("title").asText("Untitled Question"));
        }

        appendCsvRow(csv, headers);

        // Each submitted response becomes one CSV row.
        for (SurveyResponse response : responses) {

            List<String> row = new ArrayList<>();

            // Add submission date and time.
            row.add(
                    response.getSubmittedAt() == null
                            ? ""
                            : response.getSubmittedAt().toString()
            );

            // Read the response answers from stored JSON.
            JsonNode answers = jsonService.read(
                    response.getAnswersJson()
            );

            // Add each answer in the same order as the headers.
            for (JsonNode question : questions) {

                String questionId = question.path("id").asText();

                JsonNode answer = answers.path(questionId);

                row.add(convertAnswerToCsv(answer));
            }

            appendCsvRow(csv, row);
        }

        // Return CSV content as UTF-8 bytes.
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    // Convert an answer into readable CSV cell content.
    private String convertAnswerToCsv(JsonNode answer) {

        // Return an empty cell when no answer exists.
        if (answer == null || answer.isNull() || answer.isMissingNode()) {
            return "";
        }

        // Multi-select answers are stored as JSON arrays.
        // Join their values into one cell.
        if (answer.isArray()) {

            List<String> selectedOptions = new ArrayList<>();

            for (JsonNode option : answer) {
                selectedOptions.add(option.asText());
            }

            return String.join("; ", selectedOptions);
        }

        // Handle ordinary text, number, and boolean answers.
        if (answer.isValueNode()) {
            return answer.asText();
        }

        // Preserve unexpected structured values as JSON.
        return answer.toString();
    }

    // Append a CSV row and safely escape every cell.
    private void appendCsvRow(
            StringBuilder csv,
            List<String> values
    ) {

        for (int i = 0; i < values.size(); i++) {

            if (i > 0) {
                csv.append(',');
            }

            csv.append(escapeCsvValue(values.get(i)));
        }

        csv.append("\r\n");
    }

    // Escape commas, quotation marks, and line breaks.
    private String escapeCsvValue(String value) {

        if (value == null) {
            value = "";
        }

        // Double quotation marks inside a CSV cell.
        String escaped = value.replace("\"", "\"\"");

        // Wrap every value in quotation marks.
        return "\"" + escaped + "\"";
    }
}
