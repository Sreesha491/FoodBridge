import axios from 'axios';

/**
 * Pre-configured Axios client for all FoodBridge API calls.
 *
 * Base URL:  /api  (proxied to http://localhost:8081 by Vite in dev)
 * Timeout:   10 seconds
 *
 * Interceptors:
 *  - Request:  attach Authorization header when JWT token is present
 *  - Response: handle 401 (clear token + redirect to login), normalize errors
 */
const axiosClient = axios.create({
  baseURL: '/api',
  timeout: 10_000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

const TOKEN_KEY = 'foodbridge_token';

// ── Request Interceptor ────────────────────────────────────────────
axiosClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ── Response Interceptor ───────────────────────────────────────────
axiosClient.interceptors.response.use(
  // Success: return the response as-is
  (response) => response,

  // Error: normalize and re-throw
  (error) => {
    const status = error.response?.status;

    if (status === 401) {
      // Token expired or invalid – clear storage and redirect to login
      console.warn('[FoodBridge] Unauthorized – clearing session and redirecting to login.');
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem('foodbridge_user');
      // Redirect only if not already on the login page
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    }

    if (status === 403) {
      console.warn('[FoodBridge] Forbidden – insufficient permissions.');
    }

    if (status >= 500) {
      console.error('[FoodBridge] Server error:', error.response?.data);
    }

    return Promise.reject(error);
  }
);

export { TOKEN_KEY };
export default axiosClient;
