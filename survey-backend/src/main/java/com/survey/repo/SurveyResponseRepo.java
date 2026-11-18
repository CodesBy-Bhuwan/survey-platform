
package com.survey.repo;

import com.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyResponseRepo
        extends JpaRepository<SurveyResponse, String> {

    List<SurveyResponse> findBySurvey_IdOrderBySubmittedAtDesc(
            String surveyId
    );

    long countBySurvey_Id(String surveyId);

    void deleteBySurvey_Id(String surveyId);
}
