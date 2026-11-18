// Import the reusable Axios instance.
import api from "./api";

// Import TypeScript interfaces.
import type {
    Survey,
    SurveySummary,
    CreateSurveyRequest,
    SubmitResponseRequest,
    SurveyResponse,
    SurveyAnalytics,
} from "../types/survey";


// ADMIN SURVEY APIs


// Get all surveys.
//
// Backend:
// GET /api/admin/surveys
//
export const getAllSurveys = async (): Promise<SurveySummary[]> => {

    const response = await api.get<SurveySummary[]>("/admin/surveys");

    return response.data;
};


// Create a new survey.
//
// Backend:
// POST /api/admin/surveys
//
export const createSurvey = async (
    surveyData: CreateSurveyRequest
): Promise<Survey> => {

    const response = await api.post<Survey>(
        "/admin/surveys",
        surveyData
    );

    return response.data;
};


// Get one survey by its ID.
//
// Backend:
// GET /api/admin/surveys/{id}
//
export const getSurveyById = async (
    surveyId: string
): Promise<Survey> => {

    const response = await api.get<Survey>(
        `/admin/surveys/${encodeURIComponent(surveyId)}`
    );

    return response.data;
};


// Update an existing survey.
//
// Backend:
// PUT /api/admin/surveys/{id}
//
export const updateSurvey = async (
    surveyId: string,
    surveyData: CreateSurveyRequest
): Promise<Survey> => {

    const response = await api.put<Survey>(
        `/admin/surveys/${encodeURIComponent(surveyId)}`,
        surveyData
    );

    return response.data;
};


// Publish a draft survey.
//
// Backend:
// PATCH /api/admin/surveys/{id}/publish
//
export const publishSurvey = async (
    surveyId: string
): Promise<Survey> => {

    const response = await api.patch<Survey>(
        `/admin/surveys/${encodeURIComponent(surveyId)}/publish`
    );

    return response.data;
};


// Close a published survey.
//
// Backend:
// PATCH /api/admin/surveys/{id}/close
//
export const closeSurvey = async (
    surveyId: string
): Promise<Survey> => {

    const response = await api.patch<Survey>(
        `/admin/surveys/${encodeURIComponent(surveyId)}/close`
    );

    return response.data;
};


// Delete a survey.
//
// Backend:
// DELETE /api/admin/surveys/{id}
//
export const deleteSurvey = async (
    surveyId: string
): Promise<void> => {

    await api.delete(
        `/admin/surveys/${encodeURIComponent(surveyId)}`
    );
};


// PUBLIC SURVEY APIs

// Get a published survey for public access.
//
// Backend:
// GET /api/public/surveys/{id}
//
export const getPublicSurvey = async (
    surveyId: string
): Promise<Survey> => {

    const response = await api.get<Survey>(
        `/public/surveys/${encodeURIComponent(surveyId)}`
    );

    return response.data;
};


// Submit answers to a published survey.
//
// Backend:
// POST /api/public/surveys/{id}/responses
//
export const submitSurveyResponse = async (
    surveyId: string,
    responseData: SubmitResponseRequest
): Promise<SurveyResponse> => {

    const response = await api.post<SurveyResponse>(
        `/public/surveys/${encodeURIComponent(surveyId)}/responses`,
        responseData
    );

    return response.data;
};



// ADMIN RESPONSE APIs


// Get all responses submitted to a specific survey.
//
// Backend:
// GET /api/admin/surveys/{id}/responses
//
// export const getSurveyResponses = async (
//     surveyId: string
// ): Promise<SurveyResponse[]> => {

//     const response = await api.get<SurveyResponse[]>(
//         `/admin/surveys/${encodeURIComponent(surveyId)}/responses`
//     );

//     return response.data;
// };
export const getSurveyResponses = async (id: string) => {
  const response = await api.get(`/admin/surveys/${id}/responses`);
  return response.data;
};


// ANALYTICS API


// Get survey analytics.
//
// Backend:
// GET /api/admin/surveys/{id}/analytics
//
export const getSurveyAnalytics = async (
    surveyId: string
): Promise<SurveyAnalytics> => {

    const response = await api.get<SurveyAnalytics>(
        `/admin/surveys/${encodeURIComponent(surveyId)}/analytics`
    );

    return response.data;
};


