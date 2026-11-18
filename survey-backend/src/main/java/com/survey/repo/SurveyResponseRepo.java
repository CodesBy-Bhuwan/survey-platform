
package com.survey.repo;

import com.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SurveyResponseRepo
        extends JpaRepository<SurveyResponse, String> {

    // Find all responses belonging to a particular survey.
    List<SurveyResponse> findBySurvey_IdOrderBySubmittedAtDesc(
            UUID surveyId
    );

    // Count responses for a particular survey.
    long countBySurvey_Id(UUID surveyId);

    // Delete responses belonging to a survey.
    void deleteBySurvey_Id(UUID surveyId);
}
