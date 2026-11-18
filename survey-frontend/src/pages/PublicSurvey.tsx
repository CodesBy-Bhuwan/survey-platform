
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import {
  getPublicSurvey,
  submitSurveyResponse,
} from "../services/surveryService";

import type {
  Survey,
  SurveyQuestion,
  SubmitResponseRequest,
} from "../types/survey";

import "./PublicSurvey.css";

function PublicSurvey() {
  // Read the survey ID from the URL
  const { id } = useParams<{ id: string }>();

  const [survey, setSurvey] = useState<Survey | null>(null);
  const [answers, setAnswers] = useState<
    Record<string, string | number | string[]>
  >({});

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [submitted, setSubmitted] = useState(false);

  // Load the public survey when the page opens
  useEffect(() => {
    const loadSurvey = async () => {
      if (!id) {
        setError("Invalid survey link.");
        setLoading(false);
        return;
      }

      try {
        const data = await getPublicSurvey(id);
        setSurvey(data);
      } catch (err) {
        console.error("Failed to load survey:", err);
        setError("Unable to load this survey. It may be unavailable or closed.");
      } finally {
        setLoading(false);
      }
    };

    loadSurvey();
  }, [id]);

  // Update an answer for a question
  const updateAnswer = (
    questionId: string,
    value: string | number | string[]
  ) => {
    setAnswers((previous) => ({
      ...previous,
      [questionId]: value,
    }));
  };

  // Check required questions before submission
  const validateAnswers = (questions: SurveyQuestion[]) => {
    for (const question of questions) {
      if (!question.required) continue;

      const answer = answers[question.id];

      if (
        answer === undefined ||
        answer === "" ||
        (Array.isArray(answer) && answer.length === 0)
      ) {
        return `Please answer: ${question.title}`;
      }
    }

    return "";
  };

  // Submit all answers to the backend
  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError("");

    if (!survey || !id) return;

    const validationError = validateAnswers(
      survey.schema.questions
    );

    if (validationError) {
      setError(validationError);
      return;
    }

    const responseData: SubmitResponseRequest = {
      answers,
    };

    try {
      setSubmitting(true);
      await submitSurveyResponse(id, responseData);
      setSubmitted(true);
    } catch (err) {
      console.error("Failed to submit response:", err);
      setError("Unable to submit your response. Please try again.");
    } finally {
      setSubmitting(false);
    }
  };

  // Loading state
  if (loading) {
    return <div className="public-survey-message">Loading survey...</div>;
  }

  // Error or unavailable survey
  if (error && !survey) {
    return <div className="public-survey-message error-message">{error}</div>;
  }

  // Successful submission
  if (submitted) {
    return (
      <div className="public-survey-message success-message">
        <h1>Thank you!</h1>
        <p>Your response has been submitted successfully.</p>
      </div>
    );
  }

  if (!survey) return null;

  return (
    <main className="public-survey-container">
      <header className="public-survey-header">
        <h1>{survey.title}</h1>
        <p>{survey.description}</p>
      </header>

      <form onSubmit={handleSubmit}>
        {survey.schema.questions
          .slice()
          .sort((a, b) => a.order - b.order)
          .map((question) => (
            <section className="public-question-card" key={question.id}>
              <label className="public-question-title">
                {question.title}
                {question.required && <span className="required-mark"> *</span>}
              </label>

              {/* Text question */}
              {question.type === "TEXT" && (
                <textarea
                  value={String(answers[question.id] ?? "")}
                  onChange={(event) =>
                    updateAnswer(question.id, event.target.value)
                  }
                  placeholder="Type your answer..."
                  required={question.required}
                />
              )}

              {/* Single choice question */}
              {question.type === "SINGLE_CHOICE" &&
                question.options.map((option) => (
                  <label className="public-option" key={option}>
                    <input
                      type="radio"
                      name={question.id}
                      value={option}
                      checked={answers[question.id] === option}
                      onChange={() => updateAnswer(question.id, option)}
                      required={question.required}
                    />
                    {option}
                  </label>
                ))}

              {/* Multiple select question */}
              {question.type === "MULTI_SELECT" &&
                question.options.map((option) => {
                  const selected = Array.isArray(answers[question.id])
                    ? (answers[question.id] as string[])
                    : [];

                  return (
                    <label className="public-option" key={option}>
                      <input
                        type="checkbox"
                        checked={selected.includes(option)}
                        onChange={(event) => {
                          const updated = event.target.checked
                            ? [...selected, option]
                            : selected.filter((item) => item !== option);

                          updateAnswer(question.id, updated);
                        }}
                      />
                      {option}
                    </label>
                  );
                })}

              {/* Rating question: 1 to 5 */}
              {question.type === "RATING" && (
                <div className="rating-options">
                  {[1, 2, 3, 4, 5].map((rating) => (
                    <label key={rating}>
                      <input
                        type="radio"
                        name={question.id}
                        value={rating}
                        checked={answers[question.id] === rating}
                        onChange={() => updateAnswer(question.id, rating)}
                        required={question.required}
                      />
                      {rating}
                    </label>
                  ))}
                </div>
              )}
            </section>
          ))}

        {error && <p className="public-form-error">{error}</p>}

        <button
          type="submit"
          className="public-submit-button"
          disabled={submitting}
        >
          {submitting ? "Submitting..." : "Submit Response"}
        </button>
      </form>
    </main>
  );
}

export default PublicSurvey;
