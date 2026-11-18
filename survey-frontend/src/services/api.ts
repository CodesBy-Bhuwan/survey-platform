import axios from "axios";

// Create a reusable Axios instance.
// All API requests will use this configuration.
const api = axios.create({

    // Read the backend URL from the Vite environment variable.
    baseURL: import.meta.env.VITE_API_BASE_URL,

    // Maximum time to wait for an API response.
    timeout: 10000,

    // Default request headers.
    headers: {
        "Content-Type": "application/json",
    },
});

export default api;