
package com.survey.entity;

import com.survey.entity.enums.SurveyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "surveys")
@Getter
@Setter
@AllArgsConstructor
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
    private String schemaJson;

    public Survey() {
    }

    public Survey( String title, String description, String schemaJson) {
//        this.id = UUID.fromString(id);
        this.title = title;
        this.description = description;
        this.schemaJson = schemaJson;
        this.status = SurveyStatus.DRAFT;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateDetails(
            String title,
            String description,
            String schemaJson
    ) {
        this.title = title;
        this.description = description;
        this.schemaJson = schemaJson;
    }

    public void publish() {
        this.status = SurveyStatus.PUBLISHED;
    }

    public void close() {
        this.status = SurveyStatus.CLOSED;
    }
}