
import { useState } from "react";
import { useNavigate } from "react-router-dom";

// import QuestionEditor from "../components/survey/QuestionEditor";
// import { createSurvey } from "../services/surveyService";

import type {
  CreateSurveyRequest,
  SurveyQuestion,
} from "../types/survey";

import "./SurveyBuilder.css";
import { createSurvey } from "../services/surveryService";
import QuestionEditor from "../components/survey/QustionEditor";

function SurveyBuilder() {
  const navigate = useNavigate();

  // Survey information
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  // Store all survey questions
  const [questions, setQuestions] = useState<SurveyQuestion[]>([]);

  // Track saving status and error messages
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Create a new question with default values
  const addQuestion = () => {
    const newQuestion: SurveyQuestion = {
      id: crypto.randomUUID(),
      title: "",
      type: "TEXT",
      required: false,
      order: questions.length + 1,
      options: [],
      condition: null,
    };

    setQuestions((previous) => [...previous, newQuestion]);
  };

  // Update an existing question
  const updateQuestion = (
    questionId: string,
    updatedQuestion: SurveyQuestion
  ) => {
    setQuestions((previous) =>
      previous.map((question) =>
        question.id === questionId ? updatedQuestion : question
      )
    );
  };

  // Remove a question and update the order of remaining questions
  const removeQuestion = (questionId: string) => {
    setQuestions((previous) =>
      previous
        .filter((question) => question.id !== questionId)
        .map((question, index) => ({
          ...question,
          order: index + 1,
        }))
    );
  };

  // Move a question up or down
  const moveQuestion = (
    index: number,
    direction: "up" | "down"
  ) => {
    const newQuestions = [...questions];

    const targetIndex =
      direction === "up" ? index - 1 : index + 1;

    // Prevent moving outside the question list
    if (targetIndex < 0 || targetIndex >= newQuestions.length) {
      return;
    }

    // Swap question positions
    [newQuestions[index], newQuestions[targetIndex]] = [
      newQuestions[targetIndex],
      newQuestions[index],
    ];

    // Update question order
    const reorderedQuestions = newQuestions.map((question, i) => ({
      ...question,
      order: i + 1,
    }));

    setQuestions(reorderedQuestions);
  };

  // Validate and save the survey
  const handleSaveSurvey = async () => {
    setError("");

    // Validate survey title
    if (!title.trim()) {
      setError("Please enter a survey title.");
      return;
    }

    // Require at least one question
    if (questions.length === 0) {
      setError("Please add at least one question.");
      return;
    }

    // Validate question titles and choice options
    for (const question of questions) {
      if (!question.title.trim()) {
        setError("Every question must have a title.");
        return;
      }

      if (
        question.type === "SINGLE_CHOICE" ||
        question.type === "MULTI_SELECT"
      ) {
        const validOptions = question.options.filter(
          (option) => option.trim() !== ""
        );

        if (validOptions.length < 2) {
          setError(
            "Choice questions must have at least two options."
          );
          return;
        }
      }
    }

    // Prepare the request body expected by the backend
    const surveyData: CreateSurveyRequest = {
      title: title.trim(),
      description: description.trim(),
      schema: {
        questions: questions.map((question, index) => ({
          ...question,
          title: question.title.trim(),
          order: index + 1,
        })),
      },
    };

    try {
      setLoading(true);

      // Send the survey to the Spring Boot backend
      await createSurvey(surveyData);

      // Return to Dashboard after successful creation
      navigate("/");
    } catch (err) {
      console.error("Failed to create survey:", err);
      setError(
        "Unable to save the survey. Please check your backend connection and try again."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="survey-builder">

      {/* Page heading */}
      <div className="builder-header">
        <h1>Create Survey</h1>
        <p>Build a survey by adding your own questions.</p>

        <button
          type="button"
          className="cancel-btn"
          onClick={() => navigate("/")}
        >
          Back to Dashboard
        </button>
      </div>

      {/* Survey information */}
      <section className="survey-info">
        <h2>Survey Information</h2>

        {/* Survey title */}
        <div className="form-group">
          <label htmlFor="survey-title">
            Survey Title
          </label>

          <input
            id="survey-title"
            type="text"
            placeholder="Enter survey title"
            value={title}
            onChange={(event) => setTitle(event.target.value)}
          />
        </div>

        {/* Survey description */}
        <div className="form-group">
          <label htmlFor="survey-description">
            Description
          </label>

          <textarea
            id="survey-description"
            placeholder="Describe your survey"
            value={description}
            onChange={(event) =>
              setDescription(event.target.value)
            }
          />
        </div>
      </section>

      {/* Questions section */}
      <section className="questions-section">
        <div className="questions-header">
          <h2>Survey Questions</h2>

          <button
            type="button"
            className="add-question-btn"
            onClick={addQuestion}
          >
            + Add Question
          </button>
        </div>

        <p>{questions.length} question(s)</p>

        {/* Display all question editors */}
        {questions.map((question, index) => (
          <QuestionEditor
            key={question.id}
            question={question}
            index={index}
            totalQuestions={questions.length}
            onChange={(updatedQuestion) =>
              updateQuestion(question.id, updatedQuestion)
            }
            onRemove={() => removeQuestion(question.id)}
            onMove={(direction: "up" | "down") =>
              moveQuestion(index, direction)
            }
          />
        ))}
      </section>

      {/* Display validation or API errors */}
      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {/* Bottom action buttons */}
      <div className="builder-actions">
        <button
          type="button"
          className="cancel-btn"
          onClick={() => navigate("/")}
          disabled={loading}
        >
          Cancel
        </button>

        <button
          type="button"
          className="save-btn"
          onClick={handleSaveSurvey}
          disabled={loading}
        >
          {loading ? "Saving..." : "Save Survey"}
        </button>
      </div>

    </div>
  );
}

export default SurveyBuilder;
