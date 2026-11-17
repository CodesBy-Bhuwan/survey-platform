
package com.survey.repo;

import com.survey.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SurveyRepo extends JpaRepository<Survey, UUID> {
}
