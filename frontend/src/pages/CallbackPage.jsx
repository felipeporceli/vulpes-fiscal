import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { exchangeCodeForToken } from '../services/auth';
import { useAuth } from '../context/AuthContext';

export default function CallbackPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState('');

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const code = params.get('code');
    const errorParam = params.get('error');

    if (errorParam) {
      setError(`Erro de autenticação: ${errorParam}`);
      return;
    }

    if (!code) {
      setError('Código de autorização não encontrado.');
      return;
    }

    exchangeCodeForToken(code)
      .then((tokenData) => {
        login(tokenData);
        navigate('/sistema', { replace: true });
      })
      .catch((err) => setError(err.message));
  }, []);

  if (error) {
    return (
      <div
        className="min-h-screen flex flex-col items-center justify-center gap-4 text-center p-6"
        style={{ background: '#032A47' }}
      >
        <p className="text-red-400 font-medium">{error}</p>
        <a href="/login" className="text-vulpes-orange underline text-sm">
          Voltar para o login
        </a>
      </div>
    );
  }

  return (
    <div
      className="min-h-screen flex flex-col items-center justify-center gap-4"
      style={{ background: '#032A47' }}
    >
      <span className="w-8 h-8 border-2 border-white/20 border-t-vulpes-orange rounded-full animate-spin" />
      <p className="text-slate-400 text-sm">Autenticando…</p>
    </div>
  );
}
