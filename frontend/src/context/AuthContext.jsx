import { createContext, useContext, useState, useCallback } from 'react';
import { getStoredToken, removeTokens, storeTokens } from '../services/auth';

const AuthContext = createContext(null);

function decodeJwt(token) {
  try {
    const payload = token.split('.')[1];
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decoded);
  } catch {
    return null;
  }
}

function parseUser(token) {
  if (!token) return null;
  const claims = decodeJwt(token);
  if (!claims) return null;
  const roles = (claims.roles || []).map((r) => r.replace('ROLE_', ''));
  return {
    username: claims.sub || '—',
    roles,
    empresaId: claims.empresaId ?? null,
    hasRole: (...check) => check.some((r) => roles.includes(r)),
  };
}

export function AuthProvider({ children }) {
  const [token, setToken]   = useState(() => getStoredToken());
  const [user,  setUser]    = useState(() => parseUser(getStoredToken()));

  const login = useCallback((tokenData) => {
    storeTokens(tokenData);
    setToken(tokenData.access_token);
    setUser(parseUser(tokenData.access_token));
  }, []);

  const logout = useCallback(() => {
    removeTokens();
    setToken(null);
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ token, user, isAuthenticated: !!token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
};
