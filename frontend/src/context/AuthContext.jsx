import { createContext, useContext, useState, useCallback, useEffect } from 'react';
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
    username:  claims.sub        || '—',
    userId:    claims.userId     ?? null,
    empresaId: claims.empresaId  ?? null,
    roles,
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

  // Intercepta respostas 401 globalmente: token inválido (backend reiniciado
  // com novas chaves RSA, ou token expirado no servidor) → logout imediato.
  useEffect(() => {
    const originalFetch = window.fetch.bind(window);
    window.fetch = async (...args) => {
      const response = await originalFetch(...args);
      if (response.status === 401) {
        removeTokens();
        setToken(null);
        setUser(null);
      }
      return response;
    };
    return () => { window.fetch = originalFetch; };
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

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
