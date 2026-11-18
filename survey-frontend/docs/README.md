# Survey Management System --- Frontend Documentation

## 1. Overview

The frontend is the user interface for the Survey Management System. It
is built with **React**, **TypeScript**, and **Vite**.

It provides two main experiences:

1.  **Administrator experience:** create, manage, publish, close, and
    delete surveys; review responses; inspect analytics; and export
    responses from closed surveys.
2.  **Respondent experience:** open a published survey, answer its
    questions, and submit a response.

The frontend communicates with the Spring Boot backend through REST APIs
using Axios.

## 2. Technology Stack

  Technology     Purpose
  -------------- ----------------------------------------------------
  React          Component-based user interface
  TypeScript     Static typing for components, data, and API models
  Vite           Frontend development server and build tooling
  React Router   Client-side page navigation
  Axios          HTTP communication with the backend
  CSS            Page layout, styling, and responsive presentation

## 3. Development Configuration

The Vite development server currently runs at:

``` text
http://localhost:5174
```

The `.env` file contains the backend API base URL:

``` env
VITE_API_BASE_URL=http://localhost:5454/api
```

The Axios client in `src/services/api.ts` uses this environment
variable:

``` ts
import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;
```

Restart the Vite development server after changing environment
variables.

## 4. Frontend Structure

The main application code is organized into pages, components, services,
and types.

``` text
src
├── components
│   └── survey
│       └── QuestionEditor.tsx
├── pages
│   ├── Dashboard.tsx
│   ├── Dashboard.css
│   ├── SurveyBuilder.tsx
│   ├── PublicSurvey.tsx
│   ├── PublicSurvey.css
│   ├── SurveyResponses.tsx
│   ├── SurveyResponses.css
│   ├── SurveyAnalytics.tsx
│   └── SurveyAnalytics.css
├── services
│   ├── api.ts
│   └── surveyService.ts
├── types
│   └── survey.ts
└── App.tsx
```

The exact project may contain additional components and stylesheet
files.

## 5. Application Routing

React Router maps URLs to the application pages.

  --------------------------------------------------------------------------
  Route                      Page                    Purpose
  -------------------------- ----------------------- -----------------------
  `/`                        Dashboard               Administrator survey
                                                     overview

  `/surveys/create`          Survey Builder          Create a new survey

  `/surveys/:id`             Public Survey           Respond to a published
                                                     survey

  `/surveys/:id/responses`   Survey Responses        Review submitted
                                                     responses

  `/surveys/:id/analytics`   Survey Analytics        Review response
                                                     statistics
  --------------------------------------------------------------------------

The survey ID in the URL identifies the survey being viewed.

## 6. Main Pages and Features

### 6.1 Dashboard

File: `src/pages/Dashboard.tsx`

The dashboard is the main administrator page.

Implemented functionality:

-   Retrieve and display surveys from the backend.
-   Display total survey count.
-   Display counts for draft, published, and closed surveys.
-   Navigate to the survey creation page.
-   Publish draft surveys.
-   Close published surveys.
-   Delete surveys.
-   Refresh the survey list.
-   Display a shareable public URL for published surveys.
-   Copy the survey link.
-   Navigate to the response list.
-   Navigate to survey analytics.

Survey actions depend on the survey's current status. For example, a
draft survey can be published, while a published survey can be closed.

The dashboard uses CSS for survey cards, status labels, statistics,
action buttons, sharing information, and responsive layouts.

### 6.2 Survey Builder

Files:

-   `src/pages/SurveyBuilder.tsx`
-   `src/components/survey/QuestionEditor.tsx`

The Survey Builder lets an administrator create a survey containing
multiple questions.

Implemented functionality includes:

-   Enter a survey title and description.
-   Add multiple questions to one survey.
-   Edit question titles and settings.
-   Remove questions.
-   Reorder questions.
-   Mark questions as required or optional.
-   Configure question options where applicable.
-   Submit the survey definition to the backend.

Supported question types:

  Type              Respondent input
  ----------------- ----------------------------
  `TEXT`            Free-text field
  `SINGLE_CHOICE`   Select one option
  `MULTI_SELECT`    Select multiple options
  `RATING`          Numeric rating from 1 to 5

The builder sends the survey definition as a schema to the backend.

### 6.3 Public Survey

Files:

-   `src/pages/PublicSurvey.tsx`
-   `src/pages/PublicSurvey.css`

Respondents use the public survey page to submit their answers.

Implemented functionality:

-   Retrieve a survey using its public survey URL.
-   Display the survey title and description.
-   Render the correct input based on each question type.
-   Support text answers.
-   Support single-choice answers.
-   Support multiple selections.
-   Support rating answers.
-   Check required questions before submission.
-   Submit answers to the public response API.
-   Display a success state after successful submission.

The public page uses the survey ID from the route parameter.

### 6.4 Survey Responses

Files:

-   `src/pages/SurveyResponses.tsx`
-   `src/pages/SurveyResponses.css`

The administrator can open the response page for a survey and review
submitted answers.

Implemented functionality:

-   Retrieve the survey details.
-   Retrieve responses for that survey.
-   Display responses in a readable layout.
-   Display submission dates.
-   Match answer values to their corresponding survey questions.
-   Format text, numeric, and multi-select answers for display.

The page loads survey details and responses from the backend.

### 6.5 Survey Analytics

Files:

-   `src/pages/SurveyAnalytics.tsx`
-   `src/pages/SurveyAnalytics.css`

The analytics page presents a summary of survey submissions and
question-level results.

The analytics data includes:

-   Total responses.
-   Number of answers received for each question.
-   Counts and proportions for single-choice options.
-   Counts and proportions for multi-select options.
-   Average rating for rating questions.
-   Text answers for free-text questions.

The page uses the backend analytics endpoint:

``` http
GET /api/admin/surveys/{id}/analytics
```

The visual presentation includes summary statistics and question-level
breakdowns. Percentages for an individual question should use that
question's answered count as the denominator where appropriate.

## 7. API Service Layer

File: `src/services/surveyService.ts`

The service layer keeps API calls separate from page components.

The frontend service includes operations corresponding to these backend
endpoints:

  Function                           API operation
  ---------------------------------- ------------------------------------
  `getAllSurveys()`                  Retrieve all administrator surveys
  `createSurvey(data)`               Create a survey
  `getSurveyById(id)`                Retrieve survey details
  `updateSurvey(id, data)`           Update survey details
  `publishSurvey(id)`                Publish a survey
  `closeSurvey(id)`                  Close a survey
  `deleteSurvey(id)`                 Delete a survey
  `getPublicSurvey(id)`              Retrieve a public survey
  `submitSurveyResponse(id, data)`   Submit respondent answers
  `getSurveyResponses(id)`           Retrieve survey responses
  `getSurveyAnalytics(id)`           Retrieve survey analytics

The Axios instance is imported from `src/services/api.ts`, so the
backend base URL is configured in one place.

## 8. TypeScript Data Models

File: `src/types/survey.ts`

The frontend defines TypeScript interfaces for the data exchanged with
the backend.

Important types include:

-   `QuestionType` --- supported question types.
-   `SurveyStatus` --- `DRAFT`, `PUBLISHED`, or `CLOSED`.
-   `QuestionCondition` --- question condition metadata.
-   `SurveyQuestion` --- one question in a survey schema.
-   `SurveySchema` --- collection of survey questions.
-   `CreateSurveyRequest` --- payload for creating a survey.
-   `Survey` --- survey details returned by the backend.
-   `SubmitResponseRequest` --- respondent answer payload.
-   `SurveySummary` --- summary information for dashboard listings.
-   `SurveyResponse` --- one submitted response.
-   `QuestionAnalytics` --- analytics for one question.
-   `SurveyAnalytics` --- overall survey analytics.

Keeping these types in a shared file helps maintain consistency across
pages and API services.

## 9. Survey Lifecycle in the Frontend

``` text
Administrator
     |
     v
Dashboard
     |
     v
Create Survey
     |
     v
Survey Builder
     |
     v
Save as DRAFT
     |
     v
Publish Survey
     |
     v
Copy and Share Public Link
     |
     v
Respondent Opens Survey
     |
     v
Respondent Submits Answers
     |
     v
Administrator Views Responses
     |
     v
Administrator Views Analytics
     |
     v
Close Survey
     |
     v
Download CSV
```

The frontend reflects the survey lifecycle and presents actions
according to the survey status.

## 10. CSV Export from the Dashboard

The final export feature adds a download action for closed surveys.

The frontend should call:

``` http
GET /api/admin/surveys/{surveyId}/responses/export
```

The request returns CSV file bytes rather than ordinary JSON.

The frontend download handler should:

1.  Call the export endpoint using Axios with `responseType: "blob"`.
2.  Create a temporary object URL from the returned blob.
3.  Create and click a temporary download link.
4.  Use a filename such as `survey-responses-<survey-id>.csv`.
5.  Release the temporary object URL after the download is initiated.

The Download CSV action should be displayed for closed surveys only. The
backend also enforces the closed-survey rule, so hiding the button is
not the only protection.

## 11. Running the Frontend

Install dependencies:

``` bash
npm install
```

Start the Vite development server:

``` bash
npm run dev
```

Open:

``` text
http://localhost:5174
```

Build the frontend for production:

``` bash
npm run build
```

Preview the production build locally:

``` bash
npm run preview
```

## 12. Frontend Testing Checklist

-   [ ] Open the dashboard.
-   [ ] Confirm surveys load from the backend.
-   [ ] Create a survey with multiple questions.
-   [ ] Add, edit, remove, and reorder questions.
-   [ ] Publish a draft survey.
-   [ ] Copy and open the public survey link.
-   [ ] Submit a survey with each supported question type.
-   [ ] Confirm required-question validation.
-   [ ] Confirm successful submission feedback.
-   [ ] Open the administrator response page.
-   [ ] Verify submitted answers are displayed against the correct
    questions.
-   [ ] Open the analytics page.
-   [ ] Verify response totals and question-level statistics.
-   [ ] Close a survey.
-   [ ] Verify the CSV download action is available for the closed
    survey.
-   [ ] Download and inspect the CSV file.
-   [ ] Check the layout at desktop and mobile widths.
-   [ ] Check loading, empty, and error states.

## 13. Current Scope and Future Considerations

The frontend currently covers survey creation, multi-question editing,
public survey participation, response review, analytics, and the CSV
export workflow.

The following should be considered before a public production release:

-   Add login and administrator authentication when backend
    authorization is implemented.
-   Add responsive and accessibility checks across all pages.
-   Provide clear feedback for network failures and invalid requests.
-   Confirm conditional-question behaviour if it is added to the
    project.
-   Test CSV download in the browsers the application intends to
    support.
-   Configure the production API URL through environment variables.

The frontend should not be treated as the security boundary for
administrator actions. Access control must also be enforced by the
backend.
