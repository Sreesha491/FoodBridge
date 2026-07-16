import { createContext, useContext, useState, useCallback } from 'react';
import authService from '@/api/authService';

/**
 * Authentication context for FoodBridge.
 *
 * Provides:
 *  - `user`         – current user object (or null if not logged in)
 *  - `isLoading`    – true while an auth operation is in progress
 *  - `login()`      – authenticate with email + password
 *  - `register()`   – create a new account
 *  - `logout()`     – sign out and clear session
 *  - `isAuthenticated` – boolean shorthand
 */
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => authService.getCurrentUser());
  const [isLoading, setIsLoading] = useState(false);

  const login = useCallback(async (email, password) => {
    setIsLoading(true);
    try {
      const authData = await authService.login(email, password);
      setUser({
        userId: authData.userId,
        name: authData.name,
        email: authData.email,
        role: authData.role,
      });
      return authData;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const register = useCallback(async (data) => {
    setIsLoading(true);
    try {
      const authData = await authService.register(data);
      setUser({
        userId: authData.userId,
        name: authData.name,
        email: authData.email,
        role: authData.role,
      });
      return authData;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const logout = useCallback(async () => {
    setIsLoading(true);
    try {
      await authService.logout();
    } finally {
      setUser(null);
      setIsLoading(false);
    }
  }, []);

  const value = {
    user,
    isLoading,
    isAuthenticated: !!user,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

/**
 * Hook to access the auth context.
 * Must be used inside <AuthProvider>.
 */
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return ctx;
}
