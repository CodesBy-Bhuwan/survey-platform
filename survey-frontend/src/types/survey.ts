// Supported survey question types.
export type QuestionType =
    | "TEXT"
    | "SINGLE_CHOICE"
    | "MULTI_SELECT"
    | "RATING";

// Supported survey statuses.
export type SurveyStatus =
    | "DRAFT"
    | "PUBLISHED"
    | "CLOSED";

// Defines the conditional visibility configuration.
export interface QuestionCondition {
    questionId: string;
    operator: "EQUALS" | "NOT_EQUALS";
    value: string | number | boolean;
}

// Represents an individual survey question.
export interface SurveyQuestion {
    id: string;
    title: string;
    type: QuestionType;
    required: boolean;
    order: number;
    options: string[];
    condition?: QuestionCondition | null;
}

// Represents the complete survey schema.
export interface SurveySchema {
    questions: SurveyQuestion[];
}

// Data required when creating a survey.
export interface CreateSurveyRequest {
    title: string;
    description: string;
    schema: SurveySchema;
}

// Represents a survey returned by the backend.
export interface Survey {
    id: string;
    title: string;
    description: string;
    schema: SurveySchema;
    status: SurveyStatus;
    createdAt: string;
    updatedAt: string;
}

// Represents the submitted answers.
export interface SubmitResponseRequest {
    answers: Record<string, string | number | string[]>;
}

// Represents a survey in the admin survey list.
// The list API returns summary information without the full schema.
export interface SurveySummary {
    id: string;
    title: string;
    description: string;
    status: SurveyStatus;
    createdAt: string;
    updatedAt: string;
}

// Represents a submitted survey response.
export interface SurveyResponse {
    id: string;
    surveyId: string;
    answers: Record<string, string | number | string[]>;
    submittedAt: string;
}

// Analytics for an individual question.
export interface QuestionAnalytics {
    questionId: string;
    title: string;
    type: QuestionType;
    answeredCount: number;

    // For SINGLE_CHOICE and MULTI_SELECT questions.
    // Example: { "Yes": 10, "No": 5 }
    optionCounts: Record<string, number>;

    // Used for RATING questions.
    averageRating: number | null;

    // Used for TEXT questions.
    textAnswers: string[];
}

// Complete analytics information for a survey.
export interface SurveyAnalytics {
    surveyId: string;
    totalResponses: number;
    questions: QuestionAnalytics[];
}