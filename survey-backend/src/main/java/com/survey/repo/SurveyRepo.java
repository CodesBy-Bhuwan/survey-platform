
package com.survey.repo;

import com.survey.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SurveyRepo extends JpaRepository<Survey, UUID> {
    List<Survey> id(UUID id);
}
