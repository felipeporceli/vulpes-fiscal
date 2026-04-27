import { useState, useEffect, useRef } from 'react';
import { motion } from 'framer-motion';
import {
  LayoutDashboard,
  ShoppingCart,
  Package,
  Users,
  FileText,
  Building2,
  Settings,
  LogOut,
  Bell,
  TrendingUp,
  TrendingDown,
  ChevronRight,
  ChevronDown,
  Menu,
  X,
  Landmark,
  Receipt,
  Moon,
  Sun,
  UserCircle,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import EmpresasPage from './EmpresasPage';
import ConsumidoresPage from './ConsumidoresPage';
import EstabelecimentosPage from './EstabelecimentosPage';
import ProdutosPage from './ProdutosPage';
import VendasPage from './VendasPage';
import TributacaoPage from './TributacaoPage';
import NfcePage from './NfcePage';
import UsuariosPage from './UsuariosPage';

const API = import.meta.env.VITE_API_URL ?? '';

// ─── Itens de navegação base ──────────────────────────────────────────────────
const BASE_NAV = [
  { id: 'dashboard',        icon: LayoutDashboard, label: 'Dashboard'        },
  { id: 'vendas',           icon: ShoppingCart,    label: 'Vendas'           },
  { id: 'nfce',             icon: FileText,        label: 'NFC-e'            },
  { id: 'consumidores',     icon: Users,           label: 'Consumidores'     },
  { id: 'estabelecimentos', icon: Building2,       label: 'Estabelecimentos' },
  { id: 'configuracoes',    icon: Settings,        label: 'Configurações'    },
];

const PRODUTOS_SUBNAV = [
  { id: 'produtos',   icon: Package, label: 'Produtos'   },
  { id: 'tributacao', icon: Receipt, label: 'Tributação' },
];

// ─── Sidebar ──────────────────────────────────────────────────────────────────
function Sidebar({ collapsed, activePage, onNavigate }) {
  const { logout, user } = useAuth();
  const produtosActive = activePage === 'produtos' || activePage === 'tributacao';
  const [produtosOpen, setProdutosOpen] = useState(produtosActive);

  const navItems = [
    ...BASE_NAV,
    ...(user?.hasRole('ADMINISTRADOR', 'SUPORTE', 'EMPRESARIO', 'GERENTE')
      ? [{ id: 'empresas', icon: Landmark, label: 'Empresas' }]
      : []),
  ];

  function handleProdutosClick() {
    if (collapsed) {
      onNavigate('produtos');
    } else {
      setProdutosOpen((v) => !v);
    }
  }

  return (
    <aside
      className={`flex flex-col h-full transition-all duration-300 ${collapsed ? 'w-16' : 'w-60'}`}
      style={{ background: 'linear-gradient(180deg, #021E33 0%, #032A47 50%, #08315B 100%)' }}
    >
      <div className="flex items-center gap-3 px-4 py-5 border-b border-white/10">
        <img src="/vulpeslogo.png" alt="Vulpes Fiscal" className="h-14 w-auto flex-shrink-0" />
        {!collapsed && (
          <span className="text-white font-bold text-lg whitespace-nowrap overflow-hidden">
            Vulpes<span className="text-vulpes-orange">Fiscal</span>
          </span>
        )}
      </div>

      <nav className="flex-1 py-4 px-2 space-y-1 overflow-y-auto">
        {navItems.map((item) => {
          const active = activePage === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onNavigate(item.id)}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 group ${
                active
                  ? 'bg-vulpes-orange/15 text-white'
                  : 'text-slate-400 hover:text-white hover:bg-white/10'
              }`}
            >
              <item.icon
                size={19}
                className={`flex-shrink-0 ${
                  active ? 'text-vulpes-orange' : 'group-hover:text-vulpes-orange transition-colors'
                }`}
              />
              {!collapsed && <span className="truncate">{item.label}</span>}
              {!collapsed && active && (
                <span className="ml-auto w-1.5 h-1.5 rounded-full bg-vulpes-orange" />
              )}
            </button>
          );
        })}

        <div>
          <button
            onClick={handleProdutosClick}
            className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 group ${
              produtosActive
                ? 'bg-vulpes-orange/15 text-white'
                : 'text-slate-400 hover:text-white hover:bg-white/10'
            }`}
          >
            <Package
              size={19}
              className={`flex-shrink-0 ${
                produtosActive ? 'text-vulpes-orange' : 'group-hover:text-vulpes-orange transition-colors'
              }`}
            />
            {!collapsed && <span className="truncate flex-1 text-left">Produtos</span>}
            {!collapsed && (
              <ChevronDown
                size={14}
                className={`flex-shrink-0 transition-transform duration-200 ${produtosOpen ? 'rotate-180' : ''}`}
              />
            )}
          </button>

          {!collapsed && (
            <div
              className="overflow-hidden transition-all duration-250 ease-in-out"
              style={{ maxHeight: produtosOpen ? '120px' : '0px', opacity: produtosOpen ? 1 : 0 }}
            >
              <div className="ml-4 mt-0.5 space-y-0.5 border-l border-white/10 pl-3">
                {PRODUTOS_SUBNAV.map((sub) => {
                  const active = activePage === sub.id;
                  return (
                    <button
                      key={sub.id}
                      onClick={() => onNavigate(sub.id)}
                      className={`w-full flex items-center gap-2.5 px-2 py-2 rounded-lg text-xs font-medium transition-all duration-200 group ${
                        active
                          ? 'bg-vulpes-orange/15 text-white'
                          : 'text-slate-400 hover:text-white hover:bg-white/10'
                      }`}
                    >
                      <sub.icon
                        size={15}
                        className={`flex-shrink-0 ${
                          active ? 'text-vulpes-orange' : 'group-hover:text-vulpes-orange transition-colors'
                        }`}
                      />
                      <span className="truncate">{sub.label}</span>
                      {active && <span className="ml-auto w-1.5 h-1.5 rounded-full bg-vulpes-orange" />}
                    </button>
                  );
                })}
              </div>
            </div>
          )}
        </div>
      </nav>

      <div className="p-2 border-t border-white/10">
        <button
          onClick={logout}
          className="w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-slate-400 hover:text-red-400 hover:bg-red-400/10 text-sm font-medium transition-all duration-200"
        >
          <LogOut size={18} className="flex-shrink-0" />
          {!collapsed && <span>Sair</span>}
        </button>
      </div>
    </aside>
  );
}

// ─── Stat card ────────────────────────────────────────────────────────────────
function StatCard({ title, value, icon: Icon, gradient, badge, badgePositive }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      className="bg-white dark:bg-slate-800 rounded-2xl p-5 border border-slate-100 dark:border-slate-700 shadow-sm hover:shadow-md transition-shadow"
    >
      <div className="flex items-start justify-between mb-4">
        <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${gradient}`}>
          <Icon size={20} className="text-white" />
        </div>
        {badge != null && (
          <span className={`flex items-center gap-1 text-xs font-semibold px-2 py-0.5 rounded-full ${
            badgePositive ? 'bg-emerald-50 text-emerald-600' : 'bg-red-50 text-red-500'
          }`}>
            {badgePositive ? <TrendingUp size={11} /> : <TrendingDown size={11} />}
            {badge}
          </span>
        )}
      </div>
      <p className="text-2xl font-extrabold text-slate-800 dark:text-slate-100 mb-0.5">{value}</p>
      <p className="text-slate-500 dark:text-slate-400 text-xs">{title}</p>
    </motion.div>
  );
}

// ─── Gráfico de barras ────────────────────────────────────────────────────────
function BarChart({ data, loading }) {
  const [hovered, setHovered] = useState(null);

  if (loading) {
    return (
      <div className="flex items-end gap-2 h-36 px-1">
        {Array.from({ length: 7 }).map((_, i) => (
          <div
            key={i}
            className="flex-1 rounded-t-xl bg-slate-100 dark:bg-slate-700 animate-pulse"
            style={{ height: `${35 + i * 7}%` }}
          />
        ))}
      </div>
    );
  }

  if (!data.length) return null;

  const max = Math.max(...data.map((d) => d.count), 1);

  return (
    <div className="space-y-2">
      {/* Barras */}
      <div className="flex items-end gap-2 h-40 px-1 relative">
        {/* Linhas de grade */}
        <div className="absolute inset-0 flex flex-col justify-between pointer-events-none pb-0">
          {[0, 1, 2, 3, 4].map((i) => (
            <div key={i} className="border-t border-slate-100 dark:border-slate-700 w-full" />
          ))}
        </div>

        {data.map((d, i) => {
          const isToday  = i === data.length - 1;
          const isHover  = hovered === i;
          const heightPct = max > 0 ? Math.max((d.count / max) * 100, d.count > 0 ? 8 : 2) : 2;

          return (
            <div
              key={i}
              className="flex-1 flex flex-col items-center justify-end h-full relative group"
              onMouseEnter={() => setHovered(i)}
              onMouseLeave={() => setHovered(null)}
            >
              {/* Tooltip */}
              {isHover && (
                <div className="absolute -top-9 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-xs px-2.5 py-1.5 rounded-lg whitespace-nowrap z-20 shadow-lg">
                  <span className="font-bold">{d.count}</span> NFC-e
                  <div className="absolute top-full left-1/2 -translate-x-1/2 border-4 border-transparent border-t-slate-800" />
                </div>
              )}

              {/* Barra */}
              <div
                className="w-full rounded-t-xl transition-all duration-300 ease-out"
                style={{
                  height: `${heightPct}%`,
                  background: isToday
                    ? 'linear-gradient(180deg, #FF8C42 0%, #FE600C 100%)'
                    : isHover
                      ? 'linear-gradient(180deg, #2563eb 0%, #1d4ed8 100%)'
                      : 'linear-gradient(180deg, #1e4976 0%, #032A47 100%)',
                  opacity: hovered !== null && !isHover && !isToday ? 0.45 : 1,
                  transform: isHover ? 'scaleY(1.04)' : 'scaleY(1)',
                  transformOrigin: 'bottom',
                  boxShadow: isToday
                    ? '0 4px 16px rgba(254,96,12,0.40)'
                    : isHover
                      ? '0 4px 16px rgba(37,99,235,0.30)'
                      : 'none',
                  cursor: 'pointer',
                }}
              />
            </div>
          );
        })}
      </div>

      {/* Rótulos dos dias */}
      <div className="flex gap-2 px-1">
        {data.map((d, i) => {
          const isToday = i === data.length - 1;
          return (
            <div key={i} className="flex-1 flex flex-col items-center gap-0.5">
              <span className={`text-xs capitalize font-medium ${isToday ? 'text-vulpes-orange' : 'text-slate-400 dark:text-slate-500'}`}>
                {d.label}
              </span>
              {d.count > 0 && (
                <span className={`text-xs font-bold ${isToday ? 'text-vulpes-orange' : 'text-slate-500 dark:text-slate-400'}`}>
                  {d.count}
                </span>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}

// ─── Helpers ──────────────────────────────────────────────────────────────────
function fmtMoeda(v) {
  if (!v) return 'R$ 0,00';
  return `R$ ${Number(v).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function fmtDataHora(v) {
  if (!v) return '—';
  return new Date(v).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
}

const statusNfceStyle = {
  AUTORIZADA:  'bg-emerald-100 text-emerald-700',
  REJEITADA:   'bg-red-100     text-red-600',
  CANCELADA:   'bg-slate-100   text-slate-500',
  GERADA:      'bg-blue-100    text-blue-700',
  INUTILIZADA: 'bg-amber-100   text-amber-700',
};

// ─── Dashboard ────────────────────────────────────────────────────────────────
function DashboardContent({ token, user, onNavigate }) {
  const isRestrito = !user?.hasRole('ADMINISTRADOR', 'SUPORTE');
  const empresaId  = user?.empresaId;

  const [filterEmpresaId, setFilterEmpresaId] = useState('');
  const [empresasFiltro,  setEmpresasFiltro]  = useState([]);

  const [stats, setStats] = useState({
    nfceHoje: 0, autorizadas: 0, rejeicoes: 0, vendasHoje: 0,
  });
  const [statsLoading, setStatsLoading]   = useState(true);
  const [recentNfce,   setRecentNfce]     = useState([]);
  const [chartData,    setChartData]      = useState([]);
  const [chartLoading, setChartLoading]   = useState(true);

  // Carrega lista de empresas para o filtro (só ADMIN/SUPORTE)
  useEffect(() => {
    if (isRestrito || !token) return;
    fetch(`${API}/empresas?tamanho-pagina=200&pagina=0`, {
      headers: { Authorization: `Bearer ${token}` },
    }).then((r) => r.json())
      .then((d) => setEmpresasFiltro(d.content ?? []))
      .catch(() => {});
  }, [isRestrito, token]); // eslint-disable-line react-hooks/exhaustive-deps

  useEffect(() => {
    if (!token) return;

    const headers = { Authorization: `Bearer ${token}` };

    // ADMIN/SUPORTE: usa endpoints globais com empresa-id como query param opcional
    // Restrito: usa endpoints /empresa/{id}
    let nfceBase, vendasBase;
    if (isRestrito) {
      nfceBase   = `${API}/nfce/empresa/${empresaId}`;
      vendasBase = `${API}/vendas/empresa/${empresaId}`;
    } else {
      nfceBase   = `${API}/nfce`;
      vendasBase = `${API}/vendas`;
    }

    const today = new Date().toISOString().slice(0, 10);
    const di    = `${today}T00:00:00`;
    const df    = `${today}T23:59:59`;

    // Monta params base incluindo filtro de empresa (para ADMIN/SUPORTE)
    const baseExtra = !isRestrito && filterEmpresaId ? { 'empresa-id': filterEmpresaId } : {};

    function fetchNfce(extra = {}) {
      const p = new URLSearchParams({ pagina: 0, 'tamanho-pagina': 1, ...baseExtra, ...extra });
      return fetch(`${nfceBase}?${p}`, { headers }).then((r) => r.json()).catch(() => ({ totalElements: 0 }));
    }

    // Stats cards
    setStatsLoading(true);
    Promise.all([
      fetchNfce({ 'data-inicio': di, 'data-fim': df }),
      fetchNfce({ 'data-inicio': di, 'data-fim': df, status: 'AUTORIZADA' }),
      fetchNfce({ 'data-inicio': di, 'data-fim': df, status: 'REJEITADA' }),
      (() => {
        const p = new URLSearchParams({ pagina: 0, 'tamanho-pagina': 500, 'data-inicio': di, 'data-fim': df, ...baseExtra });
        return fetch(`${vendasBase}?${p}`, { headers }).then((r) => r.json()).catch(() => ({ content: [] }));
      })(),
    ]).then(([nfce, autor, rejeit, vendas]) => {
      const vendasHoje = (vendas.content ?? []).reduce(
        (s, v) => s + (Number(v.valorFinal) || 0), 0,
      );
      setStats({
        nfceHoje:    nfce.totalElements   ?? 0,
        autorizadas: autor.totalElements  ?? 0,
        rejeicoes:   rejeit.totalElements ?? 0,
        vendasHoje,
      });
    }).finally(() => setStatsLoading(false));

    // NFC-e recentes (últimas 5)
    const pRecent = new URLSearchParams({ pagina: 0, 'tamanho-pagina': 5, ...baseExtra });
    fetch(`${nfceBase}?${pRecent}`, { headers })
      .then((r) => r.json())
      .then((d) => setRecentNfce(d.content ?? []))
      .catch(() => {});

    // Gráfico: últimos 7 dias
    setChartLoading(true);
    const days = Array.from({ length: 7 }, (_, i) => {
      const d = new Date();
      d.setDate(d.getDate() - (6 - i));
      return d;
    });
    Promise.all(
      days.map((d) => {
        const iso = d.toISOString().slice(0, 10);
        const p   = new URLSearchParams({
          pagina: 0, 'tamanho-pagina': 1,
          'data-inicio': `${iso}T00:00:00`,
          'data-fim':    `${iso}T23:59:59`,
          ...baseExtra,
        });
        return fetch(`${nfceBase}?${p}`, { headers })
          .then((r) => r.json())
          .then((data) => ({
            label: d.toLocaleDateString('pt-BR', { weekday: 'short' }).replace('.', ''),
            count: data.totalElements ?? 0,
          }))
          .catch(() => ({
            label: d.toLocaleDateString('pt-BR', { weekday: 'short' }).replace('.', ''),
            count: 0,
          }));
      }),
    ).then((d) => { setChartData(d); setChartLoading(false); })
     .catch(() => setChartLoading(false));

  }, [token, empresaId, filterEmpresaId]); // eslint-disable-line react-hooks/exhaustive-deps

  const today = new Date().toLocaleDateString('pt-BR', {
    weekday: 'long', day: 'numeric', month: 'long', year: 'numeric',
  });

  const authRate = stats.nfceHoje > 0
    ? Math.round((stats.autorizadas / stats.nfceHoje) * 100)
    : null;

  return (
    <>
      {/* ── Cabeçalho + filtro ── */}
      <div className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4 mb-8">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-800 dark:text-slate-100 mb-1">Dashboard</h1>
          <p className="text-slate-500 dark:text-slate-400 text-sm capitalize">{today}</p>
        </div>

        {!isRestrito && empresasFiltro.length > 0 && (
          <div className="flex items-center gap-2">
            <label className="text-xs font-medium text-slate-500 dark:text-slate-400 whitespace-nowrap">Filtrar empresa:</label>
            <select
              value={filterEmpresaId}
              onChange={(e) => setFilterEmpresaId(e.target.value)}
              className="border border-slate-200 dark:border-slate-600 rounded-xl px-3 py-2 text-sm text-slate-700 dark:text-slate-200 bg-white dark:bg-slate-700 focus:outline-none focus:border-vulpes-orange focus:ring-2 focus:ring-vulpes-orange/20 transition-all min-w-[220px]"
            >
              <option value="">Todas as empresas</option>
              {empresasFiltro.map((emp) => (
                <option key={emp.id} value={String(emp.id)}>
                  {emp.nomeFantasia || emp.razaoSocial}
                </option>
              ))}
            </select>
          </div>
        )}
      </div>

      {/* ── Cards de métricas ── */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4 mb-8">
        <StatCard
          title="NFC-e emitidas hoje"
          value={statsLoading ? '—' : stats.nfceHoje}
          icon={FileText}
          gradient="bg-gradient-to-br from-vulpes-dark to-vulpes-orange"
          badge={authRate != null ? `${authRate}% auth.` : undefined}
          badgePositive={authRate != null && authRate >= 80}
        />
        <StatCard
          title="Valor em vendas hoje"
          value={statsLoading ? '—' : fmtMoeda(stats.vendasHoje)}
          icon={ShoppingCart}
          gradient="bg-gradient-to-br from-emerald-400 to-green-600"
        />
        <StatCard
          title="NFC-e autorizadas"
          value={statsLoading ? '—' : stats.autorizadas}
          icon={TrendingUp}
          gradient="bg-gradient-to-br from-blue-400 to-blue-600"
          badge={!statsLoading && stats.autorizadas > 0 ? 'SEFAZ OK' : undefined}
          badgePositive={true}
        />
        <StatCard
          title="Rejeições hoje"
          value={statsLoading ? '—' : stats.rejeicoes}
          icon={TrendingDown}
          gradient="bg-gradient-to-br from-red-400 to-rose-600"
          badge={!statsLoading && stats.rejeicoes > 0 ? 'Atenção' : undefined}
          badgePositive={false}
        />
      </div>

      {/* ── Gráfico + Ações rápidas ── */}
      <div className="grid lg:grid-cols-3 gap-6 mb-6">
        <div className="lg:col-span-2 bg-white dark:bg-slate-800 rounded-2xl border border-slate-100 dark:border-slate-700 shadow-sm p-6">
          <div className="flex items-center justify-between mb-5">
            <div>
              <h2 className="font-bold text-slate-800 dark:text-slate-100 text-sm">NFC-e emitidas</h2>
              <p className="text-xs text-slate-400 dark:text-slate-500 mt-0.5">Últimos 7 dias — passe o mouse nas barras para detalhes</p>
            </div>
            <div className="flex items-center gap-4 text-xs text-slate-400 dark:text-slate-500">
              <span className="flex items-center gap-1.5">
                <span className="w-3 h-3 rounded" style={{ background: 'linear-gradient(180deg,#FF8C42,#FE600C)' }} />
                hoje
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-3 h-3 rounded" style={{ background: 'linear-gradient(180deg,#1e4976,#032A47)', opacity: 0.7 }} />
                outros dias
              </span>
            </div>
          </div>
          <BarChart data={chartData} loading={chartLoading} />
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-2xl border border-slate-100 dark:border-slate-700 shadow-sm p-6">
          <h2 className="font-bold text-slate-800 dark:text-slate-100 text-sm mb-5">Ações rápidas</h2>
          <div className="space-y-3">
            {[
              { icon: ShoppingCart, label: 'Nova venda',      desc: 'Registrar e emitir NFC-e', color: 'text-vulpes-orange bg-vulpes-orange/10', page: 'vendas'       },
              { icon: Users,        label: 'Novo consumidor', desc: 'Cadastrar cliente',          color: 'text-emerald-500 bg-emerald-50',        page: 'consumidores'  },
              { icon: Package,      label: 'Novo produto',    desc: 'Adicionar ao catálogo',      color: 'text-orange-400 bg-orange-50',           page: 'produtos'      },
              { icon: FileText,     label: 'Consultar NFC-e', desc: 'Buscar por chave de acesso', color: 'text-blue-500 bg-blue-50',               page: 'nfce'          },
            ].map((action) => (
              <button
                key={action.label}
                onClick={() => onNavigate(action.page)}
                className="w-full flex items-center gap-3 p-3 rounded-xl hover:bg-slate-50 dark:hover:bg-slate-700/60 transition-colors text-left group"
              >
                <div className={`w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0 ${action.color}`}>
                  <action.icon size={17} />
                </div>
                <div>
                  <p className="text-slate-700 dark:text-slate-200 text-xs font-semibold group-hover:text-vulpes-dark transition-colors">{action.label}</p>
                  <p className="text-slate-400 dark:text-slate-500 text-xs">{action.desc}</p>
                </div>
                <ChevronRight size={14} className="ml-auto text-slate-300 group-hover:text-vulpes-orange transition-colors" />
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* ── NFC-e recentes ── */}
      <div className="bg-white dark:bg-slate-800 rounded-2xl border border-slate-100 dark:border-slate-700 shadow-sm overflow-hidden">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 dark:border-slate-700">
          <h2 className="font-bold text-slate-800 dark:text-slate-100 text-sm">NFC-e recentes</h2>
          <button
            onClick={() => onNavigate('nfce')}
            className="flex items-center gap-1 text-vulpes-orange text-xs font-medium hover:underline"
          >
            Ver todas <ChevronRight size={13} />
          </button>
        </div>

        {recentNfce.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-12 text-slate-400 dark:text-slate-500">
            <FileText size={32} className="mb-2 opacity-30" />
            <p className="text-sm">Nenhuma NFC-e registrada ainda.</p>
          </div>
        ) : (
          <div className="divide-y divide-slate-50 dark:divide-slate-700">
            {recentNfce.map((n) => (
              <div key={n.id} className="flex items-center gap-4 px-6 py-3.5 hover:bg-slate-50 dark:hover:bg-slate-700/40 transition-colors">
                <div className="w-8 h-8 rounded-lg bg-vulpes-dark/5 dark:bg-slate-700 flex items-center justify-center flex-shrink-0">
                  <FileText size={15} className="text-vulpes-medium dark:text-slate-400" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-slate-700 dark:text-slate-200 text-xs font-semibold font-mono">#{n.numero ?? n.id}</p>
                  <p className="text-slate-400 dark:text-slate-500 text-xs truncate">
                    {n.chaveAcesso ? `${n.chaveAcesso.slice(0, 22)}…` : 'Sem chave de acesso'}
                  </p>
                </div>
                <p className="text-slate-700 dark:text-slate-200 text-xs font-bold whitespace-nowrap">{fmtMoeda(n.valorTotal)}</p>
                <span className={`text-xs font-medium px-2.5 py-0.5 rounded-full whitespace-nowrap ${statusNfceStyle[n.statusNfce] ?? 'bg-slate-100 text-slate-500'}`}>
                  {n.statusNfce ?? '—'}
                </span>
                <p className="text-slate-400 dark:text-slate-500 text-xs whitespace-nowrap hidden sm:block">{fmtDataHora(n.dataEmissao)}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  );
}

// ─── Placeholder para páginas ainda não implementadas ─────────────────────────
function ComingSoon({ label }) {
  return (
    <div className="flex flex-col items-center justify-center h-64 text-slate-400">
      <div className="w-14 h-14 rounded-2xl bg-slate-100 flex items-center justify-center mb-4">
        <Settings size={28} className="opacity-40" />
      </div>
      <p className="font-semibold text-slate-600">{label}</p>
      <p className="text-sm mt-1">Em desenvolvimento</p>
    </div>
  );
}

// ─── Settings modal ───────────────────────────────────────────────────────────
function SettingsModal({ onClose }) {
  const { isDark, toggleTheme } = useTheme();

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={onClose} />
      <div className="relative bg-white dark:bg-slate-800 rounded-2xl shadow-2xl w-full max-w-sm mx-4 overflow-hidden" onClick={(e) => e.stopPropagation()}>
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 dark:border-slate-700">
          <div className="flex items-center gap-2">
            <Settings size={17} className="text-vulpes-orange" />
            <h2 className="font-bold text-slate-800 dark:text-slate-100 text-sm">Configurações</h2>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-300 transition-colors">
            <X size={18} />
          </button>
        </div>

        <div className="px-6 py-5 space-y-5">
          <div>
            <p className="text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide mb-3">Aparência</p>
            <div className="flex items-center justify-between p-3.5 rounded-xl bg-slate-50 dark:bg-slate-700/60">
              <div className="flex items-center gap-3">
                {isDark
                  ? <Moon size={17} className="text-blue-400" />
                  : <Sun  size={17} className="text-amber-400" />}
                <div>
                  <p className="text-slate-700 dark:text-slate-200 text-sm font-medium">
                    {isDark ? 'Tema escuro' : 'Tema claro'}
                  </p>
                  <p className="text-slate-400 dark:text-slate-500 text-xs">
                    {isDark ? 'Interface em modo escuro' : 'Interface em modo claro'}
                  </p>
                </div>
              </div>
              <button
                onClick={toggleTheme}
                className={`relative w-11 h-6 rounded-full transition-colors duration-300 focus:outline-none ${
                  isDark ? 'bg-vulpes-orange' : 'bg-slate-200'
                }`}
              >
                <span
                  className={`absolute top-0.5 left-0.5 w-5 h-5 rounded-full bg-white shadow-sm transition-transform duration-300 ${
                    isDark ? 'translate-x-5' : 'translate-x-0'
                  }`}
                />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

// ─── Main page ────────────────────────────────────────────────────────────────
export default function MainPage() {
  const { user, token, logout } = useAuth();
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [activePage, setActivePage] = useState('dashboard');
  const [showUserMenu,  setShowUserMenu]  = useState(false);
  const [showSettings,  setShowSettings]  = useState(false);
  const menuRef = useRef(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    function handleClick(e) {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setShowUserMenu(false);
      }
    }
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, []);

  function renderPage() {
    switch (activePage) {
      case 'dashboard':        return <DashboardContent token={token} user={user} onNavigate={setActivePage} />;
      case 'empresas':         return <EmpresasPage />;
      case 'vendas':           return <VendasPage />;
      case 'nfce':             return <NfcePage />;
      case 'produtos':         return <ProdutosPage />;
      case 'tributacao':       return <TributacaoPage />;
      case 'consumidores':     return <ConsumidoresPage />;
      case 'estabelecimentos': return <EstabelecimentosPage />;
      case 'perfil':           return <UsuariosPage />;
      case 'configuracoes':    return <ComingSoon label="Configurações" />;
      default:                 return <DashboardContent token={token} user={user} onNavigate={setActivePage} />;
    }
  }

  const roleDisplay     = user?.roles?.[0] ?? '—';
  const usernameDisplay = user?.username   ?? '—';
  const initial         = usernameDisplay.charAt(0).toUpperCase();

  return (
    <div className="flex h-screen bg-slate-50 dark:bg-slate-900 overflow-hidden">
      <Sidebar
        collapsed={sidebarCollapsed}
        activePage={activePage}
        onNavigate={setActivePage}
      />

      <div className="flex-1 flex flex-col overflow-hidden">
        <header className="bg-white dark:bg-slate-800 border-b border-slate-100 dark:border-slate-700 px-6 py-3.5 flex items-center gap-4 shadow-sm">
          <button
            onClick={() => setSidebarCollapsed(!sidebarCollapsed)}
            className="text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 transition-colors"
          >
            {sidebarCollapsed ? <Menu size={20} /> : <X size={20} />}
          </button>

          <div className="flex items-center gap-3 ml-auto">
            <button className="relative p-2 rounded-xl hover:bg-slate-50 dark:hover:bg-slate-700 text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 transition-colors">
              <Bell size={19} />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-vulpes-orange" />
            </button>

            {/* User menu trigger */}
            <div ref={menuRef} className="relative pl-3 border-l border-slate-100 dark:border-slate-700">
              <button
                onClick={() => setShowUserMenu((v) => !v)}
                className="flex items-center gap-2.5 rounded-xl px-2 py-1.5 hover:bg-slate-50 dark:hover:bg-slate-700 transition-colors"
              >
                <div
                  className="w-8 h-8 rounded-full flex items-center justify-center text-white text-xs font-bold flex-shrink-0"
                  style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}
                >
                  {initial}
                </div>
                <div className="hidden sm:block text-left">
                  <p className="text-slate-700 dark:text-slate-200 text-xs font-semibold">{usernameDisplay}</p>
                  <p className="text-slate-400 dark:text-slate-500 text-xs">{roleDisplay}</p>
                </div>
                <ChevronDown
                  size={14}
                  className={`hidden sm:block text-slate-400 transition-transform duration-200 ${showUserMenu ? 'rotate-180' : ''}`}
                />
              </button>

              {/* Dropdown menu */}
              {showUserMenu && (
                <div className="absolute right-0 mt-2 w-52 bg-white dark:bg-slate-800 rounded-2xl shadow-xl border border-slate-100 dark:border-slate-700 py-1.5 z-40">
                  {/* User info row */}
                  <div className="px-4 py-2.5 border-b border-slate-100 dark:border-slate-700 mb-1">
                    <p className="text-slate-700 dark:text-slate-200 text-xs font-semibold truncate">{usernameDisplay}</p>
                    <p className="text-slate-400 dark:text-slate-500 text-xs truncate">{roleDisplay}</p>
                  </div>

                  <button
                    onClick={() => { setShowUserMenu(false); setShowSettings(true); }}
                    className="w-full flex items-center gap-3 px-4 py-2.5 text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700/60 text-sm transition-colors"
                  >
                    <Settings size={15} className="flex-shrink-0 text-slate-400" />
                    Configurações
                  </button>

                  <button
                    onClick={() => { setShowUserMenu(false); setActivePage('perfil'); }}
                    className="w-full flex items-center gap-3 px-4 py-2.5 text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700/60 text-sm transition-colors"
                  >
                    <UserCircle size={15} className="flex-shrink-0 text-slate-400" />
                    Editar perfil
                  </button>

                  <div className="border-t border-slate-100 dark:border-slate-700 mt-1 pt-1">
                    <button
                      onClick={() => { setShowUserMenu(false); logout(); }}
                      className="w-full flex items-center gap-3 px-4 py-2.5 text-red-500 hover:bg-red-50 dark:hover:bg-red-500/10 text-sm transition-colors"
                    >
                      <LogOut size={15} className="flex-shrink-0" />
                      Sair
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto p-6 lg:p-8 bg-slate-50 dark:bg-slate-900">
          {renderPage()}
        </main>
      </div>

      {showSettings && <SettingsModal onClose={() => setShowSettings(false)} />}
    </div>
  );
}
