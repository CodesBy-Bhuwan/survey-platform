import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import {
  getAllSurveys,
  publishSurvey,
  closeSurvey,
  deleteSurvey,
} from "../services/surveryService";

import type { SurveySummary } from "../types/survey";

import "./Dashboard.css";

function Dashboard() {
  const navigate = useNavigate();

  // Store the surveys returned by the backend.
  const [surveys, setSurveys] = useState<SurveySummary[]>([]);

  // Track loading and error states.
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Load surveys from the backend.
  const loadSurveys = useCallback(async () => {
    try {
      setLoading(true);
      setError("");

      const data = await getAllSurveys();
      setSurveys(data);
    } catch (err) {
      console.error("Failed to fetch surveys:", err);
      setError("Unable to load surveys. Please check your backend connection.");
    } finally {
      setLoading(false);
    }
  }, []);

  // Fetch surveys when the Dashboard first loads.
  useEffect(() => {
    loadSurveys();
  }, [loadSurveys]);

  // Publish a draft survey.
  const handlePublish = async (id: string) => {
    try {
      await publishSurvey(id);
      await loadSurveys();
      alert("Survey published successfully!");
    } catch (err) {
      console.error("Failed to publish survey:", err);
      alert("Unable to publish survey.");
    }
  };

  // Close a published survey.
  const handleClose = async (id: string) => {
    if (!window.confirm("Are you sure you want to close this survey?")) {
      return;
    }

    try {
      await closeSurvey(id);
      await loadSurveys();
      alert("Survey closed successfully!");
    } catch (err) {
      console.error("Failed to close survey:", err);
      alert("Unable to close survey.");
    }
  };

  // Delete a survey.
  const handleDelete = async (id: string) => {
    if (!window.confirm("Are you sure you want to delete this survey?")) {
      return;
    }

    try {
      await deleteSurvey(id);
      await loadSurveys();
      alert("Survey deleted successfully!");
    } catch (err) {
      console.error("Failed to delete survey:", err);
      alert("Unable to delete survey.");
    }
  };

  // Copy the public survey link to the clipboard.
  const handleCopyLink = async (id: string) => {
    const shareLink = `${window.location.origin}/surveys/${id}`;

    try {
      await navigator.clipboard.writeText(shareLink);
      alert("Survey link copied successfully!");
    } catch (err) {
      console.error("Failed to copy survey link:", err);
      alert(`Copy this survey link manually:\n${shareLink}`);
    }
  };

  // Calculate Dashboard statistics.
  const totalSurveys = surveys.length;
  const draftSurveys = surveys.filter(
    (survey) => survey.status === "DRAFT"
  ).length;
  const publishedSurveys = surveys.filter(
    (survey) => survey.status === "PUBLISHED"
  ).length;
  const closedSurveys = surveys.filter(
    (survey) => survey.status === "CLOSED"
  ).length;

  return (
    <main className="dashboard-container">
      {/* Dashboard heading */}
      <header className="dashboard-header">
        <div>
          <h1>Survey Management Dashboard</h1>
          <p>Manage your surveys and view their status.</p>
        </div>

        <button
          type="button"
          className="create-survey-btn"
          onClick={() => navigate("/surveys/create")}
        >
          + Create Survey
        </button>
      </header>

      {/* Dashboard statistics */}
      <section className="dashboard-stats">
        <div className="stat-card">
          <h3>Total Surveys</h3>
          <p>{totalSurveys}</p>
        </div>

        <div className="stat-card">
          <h3>Draft</h3>
          <p>{draftSurveys}</p>
        </div>

        <div className="stat-card">
          <h3>Published</h3>
          <p>{publishedSurveys}</p>
        </div>

        <div className="stat-card">
          <h3>Closed</h3>
          <p>{closedSurveys}</p>
        </div>
      </section>

      {/* Survey list */}
      <section className="surveys-section">
        <div className="surveys-header">
          <h2>All Surveys</h2>

          <button
            type="button"
            className="refresh-btn"
            onClick={loadSurveys}
            disabled={loading}
          >
            {loading ? "Refreshing..." : "Refresh"}
          </button>
        </div>

        {/* Loading message */}
        {loading && <p>Loading surveys...</p>}

        {/* Error message */}
        {!loading && error && (
          <div className="error-message">
            <p>{error}</p>
            <button type="button" onClick={loadSurveys}>
              Try Again
            </button>
          </div>
        )}

        {/* Empty state */}
        {!loading && !error && surveys.length === 0 && (
          <div className="empty-state">
            <h3>No surveys found</h3>
            <p>Create your first survey to get started.</p>
            <button
              type="button"
              className="create-survey-btn"
              onClick={() => navigate("/surveys/create")}
            >
              + Create Survey
            </button>
          </div>
        )}

        {/* Display survey cards */}
        {!loading &&
          !error &&
          surveys.map((survey) => (
            <article className="survey-card" key={survey.id}>
              <div className="survey-card-header">
                <div>
                  <h3>{survey.title}</h3>
                  <p>{survey.description || "No description provided."}</p>
                </div>

                <span
                  className={`survey-status ${survey.status.toLowerCase()}`}
                >
                  {survey.status}
                </span>
              </div>

              {/* Survey ID */}
              <div className="survey-details">
                <p>
                  <strong>Survey ID:</strong> {survey.id}
                </p>

                <p>
                  <strong>Created:</strong>{" "}
                  {survey.createdAt
                    ? new Date(survey.createdAt).toLocaleDateString()
                    : "Not available"}
                </p>
              </div>

              {/* Share link - only available for published surveys */}
              {survey.status === "PUBLISHED" && (
                <div className="share-survey-section">
                  <input
                    type="text"
                    readOnly
                    value={`${window.location.origin}/surveys/${survey.id}`}
                    aria-label="Public survey link"
                  />

                  <button
                    type="button"
                    onClick={() => handleCopyLink(survey.id)}
                  >
                    Copy Survey Link
                  </button>
                </div>
              )}

              {/* Survey actions */}
              
            {/* Survey actions */}
            <div className="survey-actions">
            {survey.status === "DRAFT" && (
                <button
                type="button"
                className="publish-btn"
                onClick={() => handlePublish(survey.id)}
                >
                Publish
                </button>
            )}

            {survey.status === "PUBLISHED" && (
                <>
                <button
                    type="button"
                    className="view-responses-btn"
                    onClick={() => navigate(`/surveys/${survey.id}/responses`)}
                >
                    View Responses
                </button>
                <button
                    type="button"
                    className="analytics-btn"
                    onClick={() => navigate(`/surveys/${survey.id}/analytics`)}
                    >
                    View Analytics
                    </button>

                <button
                    type="button"
                    className="close-btn"
                    onClick={() => handleClose(survey.id)}
                >
                    Close Survey
                </button>
                </>
            )}

            {survey.status === "CLOSED" && (
                <button
                type="button"
                className="view-responses-btn"
                onClick={() => navigate(`/surveys/${survey.id}/responses`)}
                >
                View Responses
                </button>
            )}

            <button
                type="button"
                className="delete-btn"
                onClick={() => handleDelete(survey.id)}
            >
                Delete
            </button>
            </div>

            </article>
          ))}
      </section>
    </main>
  );
}

export default Dashboard;
