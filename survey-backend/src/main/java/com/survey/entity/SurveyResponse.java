
package com.survey.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "survey_responses",
        indexes = {
                @Index(
                        name = "idx_response_survey_id",
                        columnList = "survey_id"
                )
        }
)
public class SurveyResponse {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @Column(
            name = "answers_json",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String answersJson;

    @Column(
            name = "submitted_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime submittedAt;

    protected SurveyResponse() {
    }

    public SurveyResponse(Survey survey, String answersJson) {
        this.id = UUID.randomUUID().toString();
        this.survey = survey;
        this.answersJson = answersJson;
    }

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Survey getSurvey() {
        return survey;
    }

    public String getAnswersJson() {
        return answersJson;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
}
