import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import {
  getSurveyAnalytics,
  getSurveyById,
} from "../services/surveryService";

import type {
  Survey,
  SurveyAnalytics as SurveyAnalyticsData,
  QuestionAnalytics,
} from "../types/survey";

import "./SurveyAnalytics.css";

// Format date values returned by the backend.
const formatDate = (date?: string) => {
  if (!date) return "N/A";

  return new Date(date).toLocaleString();
};

// Convert a count into a percentage.
const getPercentage = (count: number, total: number) => {
  if (!total) return 0;

  return Math.round((count / total) * 100);
};

function SurveyAnalytics() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [survey, setSurvey] = useState<Survey | null>(null);
  const [analytics, setAnalytics] =
    useState<SurveyAnalyticsData | null>(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Load survey details and analytics when the page opens.
  useEffect(() => {
    const loadAnalytics = async () => {
      if (!id) {
        setError("Survey ID is missing.");
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError("");

        const [surveyData, analyticsData] = await Promise.all([
          getSurveyById(id),
          getSurveyAnalytics(id),
        ]);

        setSurvey(surveyData);
        setAnalytics(analyticsData);
      } catch (err) {
        console.error("Failed to load survey analytics:", err);
        setError(
          "Unable to load analytics. Please check the backend API."
        );
      } finally {
        setLoading(false);
      }
    };

    loadAnalytics();
  }, [id]);

  // Loading state.
  if (loading) {
    return (
      <div className="analytics-page">
        <p className="analytics-message">Loading analytics...</p>
      </div>
    );
  }

  // Error state.
  if (error) {
    return (
      <div className="analytics-page">
        <button
          className="analytics-back-btn"
          onClick={() => navigate("/")}
        >
          ← Back to Dashboard
        </button>

        <p className="analytics-error">{error}</p>
      </div>
    );
  }

  if (!survey || !analytics) {
    return (
      <div className="analytics-page">
        <p className="analytics-message">
          No analytics data available.
        </p>
      </div>
    );
  }

  // Render the results for each question.
  const renderQuestionResults = (question: QuestionAnalytics) => {
    // Choice and multi-select questions.
    if (
      question.type === "SINGLE_CHOICE" ||
      question.type === "MULTI_SELECT"
    ) {
      const options = Object.entries(question.optionCounts || {});

      return (
        <div className="analytics-options">
          {options.length === 0 ? (
            <p className="analytics-muted">No answers yet.</p>
          ) : (
            options.map(([option, count]) => {
              // Use the number of people who answered this question.
                // This is important for multi-select questions.
                const percentage = getPercentage(
                count,
                question.answeredCount
                );

              return (
                <div className="analytics-option" key={option}>
                  <div className="analytics-option-header">
                    <span>{option}</span>
                    <strong>
                      {count} ({percentage}%)
                    </strong>
                  </div>

                  <div className="analytics-progress-track">
                    <div
                      className="analytics-progress-fill"
                      style={{ width: `${percentage}%` }}
                    />
                  </div>
                </div>
              );
            })
          )}
        </div>
      );
    }

    // Rating questions.
    if (question.type === "RATING") {
      return (
        <div className="analytics-rating">
          <strong>
            {question.averageRating !== null &&
            question.averageRating !== undefined
              ? question.averageRating.toFixed(2)
              : "N/A"}
          </strong>
          <span>Average rating out of 5</span>
        </div>
      );
    }

    // Text questions.
    if (question.type === "TEXT") {
      return (
        <div className="analytics-text-answers">
          {question.textAnswers?.length ? (
            question.textAnswers.map((answer, index) => (
              <div className="analytics-text-answer" key={index}>
                {answer}
              </div>
            ))
          ) : (
            <p className="analytics-muted">
              No written answers yet.
            </p>
          )}
        </div>
      );
    }

    return (
      <p className="analytics-muted">
        Unsupported question type.
      </p>
    );
  };

  return (
    <div className="analytics-page">
      {/* Page heading */}
      <header className="analytics-header">
        <div>
          <button
            className="analytics-back-btn"
            onClick={() => navigate("/")}
          >
            ← Back to Dashboard
          </button>

          <h1>Survey Analytics</h1>
          <p>{survey.title}</p>
        </div>

        <button
          className="analytics-refresh-btn"
          onClick={() => window.location.reload()}
        >
          Refresh
        </button>
      </header>

      {/* Survey information */}
      <section className="analytics-survey-info">
        <h2>{survey.title}</h2>

        <p>
          {survey.description || "No survey description provided."}
        </p>

        <span>
          Created: {formatDate(survey.createdAt)}
        </span>
      </section>

      {/* Summary cards */}
      <section className="analytics-summary">
        <div className="analytics-summary-card">
          <span>Total Responses</span>
          <strong>{analytics.totalResponses}</strong>
        </div>

        <div className="analytics-summary-card">
          <span>Total Questions</span>
          <strong>{analytics.questions?.length || 0}</strong>
        </div>
      </section>

      {/* Question-by-question analytics */}
      <section className="analytics-results">
        <h2>Question Results</h2>

        {analytics.questions?.length ? (
          analytics.questions.map((question, index) => (
            <article
              className="analytics-question-card"
              key={question.questionId}
            >
              <div className="analytics-question-heading">
                <div>
                  <span className="analytics-question-number">
                    Question {index + 1}
                  </span>

                  <h3>{question.title}</h3>
                </div>

                <span className="analytics-question-type">
                  {question.type.replace("_", " ")}
                </span>
              </div>

              <p className="analytics-answered-count">
                Answered by {question.answeredCount} respondent(s)
              </p>

              {renderQuestionResults(question)}
            </article>
          ))
        ) : (
          <p className="analytics-muted">
            No question analytics available.
          </p>
        )}
      </section>
    </div>
  );
}

export default SurveyAnalytics;