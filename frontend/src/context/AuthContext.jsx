import { createContext, useContext, useState, useCallback } from 'react';
import { getStoredToken, removeTokens, storeTokens } from '../services/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => getStoredToken());
  const [user, setUser] = useState(null);

  const login = useCallback((tokenData) => {
    storeTokens(tokenData);
    setToken(tokenData.access_token);
  }, []);

  const logout = useCallback(() => {
    removeTokens();
    setToken(null);
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        isAuthenticated: !!token,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
};
