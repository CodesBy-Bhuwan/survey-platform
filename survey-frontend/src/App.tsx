import { BrowserRouter, Routes, Route } from "react-router-dom";

import Dashboard from "./pages/Dashboard";
import SurveyBuilder from "./pages/SurveyBuilder";
import PublicSurvey from "./pages/PublicSurvey";
import SurveyResponses from "./pages/SurveyResponse";
import SurveyAnalytics from "./pages/SurveyAnalytics";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Admin dashboard */}
        <Route path="/" element={<Dashboard />} />

        {/* Create a new survey */}
        <Route path="/surveys/create" element={<SurveyBuilder />} />

        {/* Public survey page */}
        <Route path="/surveys/:id" element={<PublicSurvey />} />

        {/* Admin response list */}
        <Route
          path="/surveys/:id/responses"
          element={<SurveyResponses />}
        />

        {/* Admin analytics page */}
        <Route
          path="/surveys/:id/analytics"
          element={<SurveyAnalytics />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;