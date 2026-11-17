
package com.survey.repo;

import com.survey.entity.SurveyVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SurveyVersionRepo extends JpaRepository<SurveyVersion, UUID> {

    List<SurveyVersion> findBySurveyId(UUID surveyId);

}
