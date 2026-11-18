
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import {
  getSurveyById,
  getSurveyResponses,
} from "../services/surveryService";

import type { Survey, SurveyResponse } from "../types/survey";

import "./SurveyResponses.css";

function SurveyResponses() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [survey, setSurvey] = useState<Survey | null>(null);
  const [responses, setResponses] = useState<SurveyResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  

  // Load the survey information and its submitted responses.
  useEffect(() => {
    const loadData = async () => {
      if (!id) {
        setError("Invalid survey ID.");
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError("");

        const [surveyData, responseData] = await Promise.all([
          getSurveyById(id),
          getSurveyResponses(id),
        ]);

        setSurvey(surveyData);
        setResponses(responseData);
      } catch (err) {
        console.error("Failed to load survey responses:", err);
        setError("Unable to load survey responses. Please try again.");
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [id]);

  // Convert different answer types into readable text.
  const formatAnswer = (answer: string | number | string[] | undefined | null) => {
    if (Array.isArray(answer)) {
      return answer.length > 0 ? answer.join(", ") : "No answer";
    }

    if (answer === "" || answer === undefined || answer === null) {
      return "No answer";
    }

    return String(answer);
  };

  if (loading) {
    return (
      <main className="responses-page">
        <p className="responses-message">Loading responses...</p>
      </main>
    );
  }

  if (error) {
    return (
      <main className="responses-page">
        <button
          className="back-button"
          onClick={() => navigate("/")}
        >
          ← Back to Dashboard
        </button>

        <p className="responses-error">{error}</p>
      </main>
    );
  }

  return (
    <main className="responses-page">
      {/* Page heading */}
      <header className="responses-header">
        <div>
          <button
            type="button"
            className="back-button"
            onClick={() => navigate("/")}
          >
            ← Back to Dashboard
          </button>

          <h1>Survey Responses</h1>
          <p>{survey?.title}</p>
        </div>

        <button
          type="button"
          className="refresh-responses-button"
          onClick={() => window.location.reload()}
        >
          Refresh Responses
        </button>
      </header>

      {/* Response summary */}
      <section className="response-summary">
        <div className="response-summary-card">
          <span>Total Responses</span>
          <strong>{responses.length}</strong>
        </div>

        <div className="response-summary-card">
          <span>Survey Status</span>
          <strong>{survey?.status ?? "Unknown"}</strong>
        </div>
      </section>

      {/* Submitted responses */}
      <section className="responses-list-section">
        <h2>Submitted Responses</h2>

        {responses.length === 0 ? (
          <div className="no-responses">
            <h3>No responses yet</h3>
            <p>
              Responses will appear here when someone submits this survey.
            </p>
          </div>
        ) : (
          responses.map((response, index) => (
            <article className="response-card" key={response.id}>
              <div className="response-card-header">
                <h3>Response #{index + 1}</h3>

                <span>
                  {response.submittedAt
                    ? new Date(response.submittedAt).toLocaleString()
                    : "Submission date unavailable"}
                </span>
              </div>

              <div className="response-answers">
                {survey?.schema.questions
                  .slice()
                  .sort((a, b) => a.order - b.order)
                  .map((question) => (
                    <div
                      className="response-answer"
                      key={question.id}
                    >
                      <h4>{question.title}</h4>

                      <p>
                        {formatAnswer(response.answers?.[question.id])}
                      </p>
                    </div>
                  ))}
              </div>
            </article>
          ))
        )}
      </section>
    </main>
  );
}

export default SurveyResponses;
