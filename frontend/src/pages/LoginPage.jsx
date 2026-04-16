import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Eye, EyeOff, ArrowLeft, Shield, Lock, LogIn } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { loginWithPassword } from '../services/auth';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || '/sistema';

  const [form, setForm] = useState({ username: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const tokenData = await loginWithPassword(form.username, form.password);
      login(tokenData);
      navigate(from, { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex" style={{ background: '#032A47' }}>

      {/* ── Painel esquerdo — branding ───────────────────────────────────── */}
      <div
        className="hidden lg:flex lg:w-5/12 xl:w-1/2 flex-col justify-between p-12 relative overflow-hidden"
        style={{ background: 'linear-gradient(160deg, #021E33 0%, #032A47 50%, #08315B 100%)' }}
      >
        {/* Grid decorativo */}
        <div
          className="absolute inset-0 opacity-[0.06]"
          style={{
            backgroundImage:
              'linear-gradient(rgba(254,96,12,0.6) 1px, transparent 1px), linear-gradient(90deg, rgba(254,96,12,0.6) 1px, transparent 1px)',
            backgroundSize: '40px 40px',
          }}
        />

        {/* Glow blobs */}
        <div className="absolute top-1/3 -left-20 w-72 h-72 bg-vulpes-orange/10 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute bottom-1/4 right-0 w-48 h-48 bg-vulpes-medium/30 rounded-full blur-2xl pointer-events-none" />

        {/* Logo topo */}
        <Link to="/" className="flex items-center gap-3 relative z-10">
          <img src="/vulpeslogo.png" alt="Vulpes Fiscal" className="h-10 w-auto" />
          <span className="text-white font-bold text-xl">
            Vulpes<span className="text-vulpes-orange">Fiscal</span>
          </span>
        </Link>

        {/* Centro — logo grande + texto */}
        <motion.div
          initial={{ opacity: 0, x: -30 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.7 }}
          className="relative z-10 flex flex-col items-center text-center"
        >
          <img
            src="/vulpeslogo.png"
            alt="Vulpes Fiscal"
            className="h-48 xl:h-56 w-auto mb-8 animate-float"
            style={{ filter: 'drop-shadow(0 0 32px rgba(254,96,12,0.3))' }}
          />
          <h2 className="text-3xl xl:text-4xl font-extrabold text-white leading-tight mb-4">
            Sua plataforma de<br />
            <span className="text-gradient">emissão fiscal</span>
          </h2>
          <p className="text-slate-400 leading-relaxed max-w-sm text-sm">
            Acesse o emissor de NFC-e mais moderno do Brasil. Rápido, seguro e 100%
            em conformidade com a SEFAZ.
          </p>

          {/* Badges de confiança */}
          <div className="flex gap-3 mt-8 flex-wrap justify-center">
            {[
              { icon: Shield, label: 'Dados criptografados' },
              { icon: Lock,   label: 'OAuth2 seguro'        },
            ].map((b) => (
              <div
                key={b.label}
                className="flex items-center gap-2 bg-white/10 border border-white/15 rounded-full px-3.5 py-1.5 text-slate-300 text-xs"
              >
                <b.icon size={12} className="text-vulpes-orange" />
                {b.label}
              </div>
            ))}
          </div>
        </motion.div>

        {/* Rodapé do painel */}
        <p className="text-slate-600 text-xs relative z-10">
          © {new Date().getFullYear()} Vulpes Fiscal
        </p>
      </div>

      {/* ── Painel direito — formulário ──────────────────────────────────── */}
      <div className="flex-1 flex items-center justify-center p-6 bg-white">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="w-full max-w-md"
        >
          {/* Logo mobile */}
          <Link to="/" className="flex lg:hidden items-center gap-2 mb-8">
            <img src="/vulpeslogo.png" alt="Vulpes Fiscal" className="h-9 w-auto" />
            <span className="font-bold text-vulpes-dark text-lg">
              Vulpes<span className="text-vulpes-orange">Fiscal</span>
            </span>
          </Link>

          {/* Cabeçalho */}
          <div className="mb-8">
            <h1 className="text-3xl font-extrabold text-vulpes-dark mb-2">
              Bem-vindo de volta
            </h1>
            <p className="text-slate-500 text-sm">
              Faça login para acessar o sistema de emissão
            </p>
          </div>

          {/* Mensagem de erro */}
          {error && (
            <motion.div
              initial={{ opacity: 0, y: -8 }}
              animate={{ opacity: 1, y: 0 }}
              className="flex items-start gap-3 bg-red-50 border border-red-200 text-red-700 text-sm px-4 py-3 rounded-xl mb-6"
            >
              <span className="mt-0.5 text-red-400 text-lg leading-none">⚠</span>
              <span>{error}</span>
            </motion.div>
          )}

          {/* Formulário OAuth2 (ROPC) */}
          <form onSubmit={handleSubmit} className="space-y-5">
            {/* Usuário */}
            <div>
              <label className="block text-slate-700 text-sm font-medium mb-1.5">
                E-mail ou usuário
              </label>
              <input
                type="text"
                name="username"
                value={form.username}
                onChange={handleChange}
                required
                autoComplete="username"
                placeholder="seu@email.com.br"
                className="w-full border border-slate-200 rounded-xl px-4 py-3 text-slate-800 placeholder-slate-400 text-sm focus:outline-none focus:border-vulpes-orange focus:ring-2 focus:ring-vulpes-orange/20 transition-all"
              />
            </div>

            {/* Senha */}
            <div>
              <div className="flex items-center justify-between mb-1.5">
                <label className="text-slate-700 text-sm font-medium">Senha</label>
                <a href="#" className="text-vulpes-orange hover:text-vulpes-orange-hover text-xs font-medium transition-colors">
                  Esqueceu a senha?
                </a>
              </div>
              <div className="relative">
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  value={form.password}
                  onChange={handleChange}
                  required
                  autoComplete="current-password"
                  placeholder="••••••••"
                  className="w-full border border-slate-200 rounded-xl px-4 py-3 pr-12 text-slate-800 placeholder-slate-400 text-sm focus:outline-none focus:border-vulpes-orange focus:ring-2 focus:ring-vulpes-orange/20 transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition-colors"
                  aria-label={showPassword ? 'Ocultar senha' : 'Mostrar senha'}
                >
                  {showPassword ? <EyeOff size={17} /> : <Eye size={17} />}
                </button>
              </div>
            </div>

            {/* Botão entrar */}
            <button
              type="submit"
              disabled={loading}
              className="w-full flex items-center justify-center gap-2 py-3.5 rounded-xl font-semibold text-white transition-all duration-200 disabled:opacity-60 disabled:cursor-not-allowed hover:scale-[1.01]"
              style={{
                background: loading
                  ? '#6B92B0'
                  : 'linear-gradient(135deg, #032A47 0%, #FE600C 100%)',
                boxShadow: loading ? 'none' : '0 8px 24px rgba(254,96,12,0.25)',
              }}
            >
              {loading ? (
                <>
                  <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  Entrando…
                </>
              ) : (
                <>
                  <LogIn size={17} />
                  Entrar
                </>
              )}
            </button>
          </form>

          {/* Link voltar */}
          <div className="mt-8 text-center">
            <Link
              to="/"
              className="inline-flex items-center gap-1.5 text-slate-400 hover:text-vulpes-dark text-sm transition-colors"
            >
              <ArrowLeft size={14} />
              Voltar para o início
            </Link>
          </div>
        </motion.div>
      </div>
    </div>
  );
}
