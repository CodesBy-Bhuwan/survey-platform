
package com.survey.entity;

import com.survey.entity.enums.SurveyStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "surveys")
@Getter
@Setter
public class Survey {

    // Survey IDs are UUIDs.
    // Hibernate generates the UUID when a new survey is saved.
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SurveyStatus status = SurveyStatus.DRAFT;

    @Column(name = "current_version", nullable = false)
    private Integer currentVersion = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "schema_json", columnDefinition = "TEXT")
    private String schemaJson;

    // Required by JPA.
    public Survey() {
    }

    // Constructor used when creating a new survey.
    // The ID is generated automatically by Hibernate.
    public Survey(String title, String description, String schemaJson) {
        this.title = title;
        this.description = description;
        this.schemaJson = schemaJson;
        this.status = SurveyStatus.DRAFT;
        this.currentVersion = 1;
    }

    // Set creation and update timestamps before inserting.
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = SurveyStatus.DRAFT;
        }

        if (currentVersion == null) {
            currentVersion = 1;
        }
    }

    // Update the modification timestamp before updating.
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Update the survey information.
    public void updateDetails(
            String title,
            String description,
            String schemaJson
    ) {
        this.title = title;
        this.description = description;
        this.schemaJson = schemaJson;
    }

    // Change survey status to PUBLISHED.
    public void publish() {
        this.status = SurveyStatus.PUBLISHED;
    }

    // Change survey status to CLOSED.
    public void close() {
        this.status = SurveyStatus.CLOSED;
    }
}
