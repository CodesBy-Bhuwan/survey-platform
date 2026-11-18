
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

    // Response ID is a String containing a UUID.
    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    // Each response belongs to one survey.
    // The referenced Survey entity uses a UUID primary key.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    // Answers are stored as JSON text in SQLite.
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

    // Required by JPA.
    protected SurveyResponse() {
    }

    // Create a response for an existing survey.
    public SurveyResponse(Survey survey, String answersJson) {
        this.id = UUID.randomUUID().toString();
        this.survey = survey;
        this.answersJson = answersJson;
    }

    // Automatically set submission time before inserting.
    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }

    // Getters used by the service and API.
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
