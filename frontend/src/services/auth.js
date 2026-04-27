// VITE_API_URL vazio → usa proxy do Vite (/oauth2 → localhost:8080) sem CORS.
// Em produção defina a URL completa: VITE_API_URL=https://api.vulpesfiscal.com.br
const API_URL = import.meta.env.VITE_API_URL ?? '';
const CLIENT_ID = import.meta.env.VITE_CLIENT_ID ?? 'meu-client';
const CLIENT_SECRET = import.meta.env.VITE_CLIENT_SECRET ?? '';
const REDIRECT_URI = import.meta.env.VITE_REDIRECT_URI ?? 'http://localhost:5173/callback';

const TOKEN_KEY = 'vf_access_token';
const REFRESH_KEY = 'vf_refresh_token';

// ─── Token storage ─────────────────────────────────────────────────────────────

function isTokenExpired(token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
    return payload.exp * 1000 < Date.now();
  } catch {
    return true;
  }
}

export const storeTokens = ({ access_token, refresh_token }) => {
  sessionStorage.setItem(TOKEN_KEY, access_token);
  if (refresh_token) sessionStorage.setItem(REFRESH_KEY, refresh_token);
};

export const getStoredToken = () => {
  const token = sessionStorage.getItem(TOKEN_KEY);
  if (!token) return null;
  if (isTokenExpired(token)) {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(REFRESH_KEY);
    return null;
  }
  return token;
};

export const removeTokens = () => {
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(REFRESH_KEY);
};

export const getAuthHeaders = () => ({
  Authorization: `Bearer ${getStoredToken()}`,
  'Content-Type': 'application/json',
});

// ─── Resource Owner Password Credentials (ROPC) ────────────────────────────────
// Requires the Spring Authorization Server to have AuthorizationGrantType.PASSWORD
// configured for the registered client. Example (AuthorizationServerConfiguration.java):
//   .authorizationGrantType(AuthorizationGrantType.PASSWORD)

export const loginWithPassword = async (username, password) => {
  const body = new URLSearchParams({
    grant_type: 'password',
    username,
    password,
    scope: 'openid',
  });

  const credentials = btoa(`${CLIENT_ID}:${CLIENT_SECRET}`);

  const response = await fetch(`${API_URL}/oauth2/token`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: `Basic ${credentials}`,
    },
    body: body.toString(),
  });

  if (!response.ok) {
    const err = await response.json().catch(() => ({}));
    throw new Error(err.error_description || 'Credenciais inválidas. Verifique seu e-mail e senha.');
  }

  return response.json();
};

// ─── Authorization Code Flow ───────────────────────────────────────────────────
// Recommended flow for SPAs. Enable PKCE on the RegisteredClient for extra security.

export const initiateAuthorizationCodeFlow = () => {
  const params = new URLSearchParams({
    response_type: 'code',
    client_id: CLIENT_ID,
    redirect_uri: REDIRECT_URI,
    scope: 'openid',
  });
  window.location.href = `${API_URL}/oauth2/authorize?${params.toString()}`;
};

export const exchangeCodeForToken = async (code) => {
  const body = new URLSearchParams({
    grant_type: 'authorization_code',
    code,
    redirect_uri: REDIRECT_URI,
    client_id: CLIENT_ID,
  });

  const credentials = btoa(`${CLIENT_ID}:${CLIENT_SECRET}`);

  const response = await fetch(`${API_URL}/oauth2/token`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: `Basic ${credentials}`,
    },
    body: body.toString(),
  });

  if (!response.ok) {
    const err = await response.json().catch(() => ({}));
    throw new Error(err.error_description || 'Falha ao trocar código por token.');
  }

  return response.json();
};

// ─── Refresh Token ─────────────────────────────────────────────────────────────

export const refreshAccessToken = async () => {
  const refreshToken = sessionStorage.getItem(REFRESH_KEY);
  if (!refreshToken) throw new Error('Sessão expirada.');

  const body = new URLSearchParams({
    grant_type: 'refresh_token',
    refresh_token: refreshToken,
  });

  const credentials = btoa(`${CLIENT_ID}:${CLIENT_SECRET}`);

  const response = await fetch(`${API_URL}/oauth2/token`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: `Basic ${credentials}`,
    },
    body: body.toString(),
  });

  if (!response.ok) {
    removeTokens();
    throw new Error('Sessão expirada. Faça login novamente.');
  }

  return response.json();
};
