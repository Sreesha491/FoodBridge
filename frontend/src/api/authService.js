import axiosClient, { TOKEN_KEY } from './axiosClient';

const USER_KEY = 'foodbridge_user';

/**
 * FoodBridge authentication service.
 *
 * Wraps all auth-related API calls and manages the JWT token + user info
 * in localStorage for session persistence.
 */
const authService = {
  /**
   * Registers a new user account.
   * @param {Object} data - { name, email, password, role, phone?, address? }
   * @returns {Promise<Object>} AuthResponse data (token, user info, etc.)
   */
  register: async (data) => {
    const res = await axiosClient.post('/auth/register', data);
    const authData = res.data.data;
    authService._persist(authData);
    return authData;
  },

  /**
   * Logs in with email and password.
   * @param {string} email
   * @param {string} password
   * @returns {Promise<Object>} AuthResponse data
   */
  login: async (email, password) => {
    const res = await axiosClient.post('/auth/login', { email, password });
    const authData = res.data.data;
    authService._persist(authData);
    return authData;
  },

  /**
   * Refreshes the access token using the stored refresh token.
   * @returns {Promise<Object>} AuthResponse data with new access token
   */
  refreshToken: async () => {
    const refreshToken = localStorage.getItem('foodbridge_refresh_token');
    if (!refreshToken) throw new Error('No refresh token available.');
    const res = await axiosClient.post('/auth/refresh', { refreshToken });
    const authData = res.data.data;
    authService._persist(authData);
    return authData;
  },

  /**
   * Logs out the current user (invalidates refresh token on server).
   * @returns {Promise<void>}
   */
  logout: async () => {
    try {
      await axiosClient.post('/auth/logout');
    } catch (e) {
      // Even if the server call fails, clear local storage
      console.warn('[FoodBridge] Logout API call failed:', e.message);
    } finally {
      authService._clear();
    }
  },

  /**
   * Returns the currently stored user info from localStorage.
   * @returns {Object|null}
   */
  getCurrentUser: () => {
    const raw = localStorage.getItem(USER_KEY);
    try {
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  },

  /**
   * Returns true if a JWT token exists in localStorage.
   * @returns {boolean}
   */
  isAuthenticated: () => !!localStorage.getItem(TOKEN_KEY),

  // ── Private helpers ──────────────────────────────────────────────

  _persist: (authData) => {
    localStorage.setItem(TOKEN_KEY, authData.token);
    localStorage.setItem('foodbridge_refresh_token', authData.refreshToken || '');
    localStorage.setItem(USER_KEY, JSON.stringify({
      userId: authData.userId,
      name: authData.name,
      email: authData.email,
      role: authData.role,
    }));
  },

  _clear: () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem('foodbridge_refresh_token');
    localStorage.removeItem(USER_KEY);
  },
};

export default authService;
