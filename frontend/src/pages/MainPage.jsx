import { useState } from 'react';
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
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import EmpresasPage from './EmpresasPage';
import ConsumidoresPage from './ConsumidoresPage';
import EstabelecimentosPage from './EstabelecimentosPage';
import ProdutosPage from './ProdutosPage';
import VendasPage from './VendasPage';
import TributacaoPage from './TributacaoPage';

// ─── Itens de navegação base ──────────────────────────────────────────────────
const BASE_NAV = [
  { id: 'dashboard',       icon: LayoutDashboard, label: 'Dashboard'        },
  { id: 'vendas',          icon: ShoppingCart,    label: 'Vendas'           },
  { id: 'nfce',            icon: FileText,        label: 'NFC-e'            },
  { id: 'consumidores',    icon: Users,           label: 'Consumidores'     },
  { id: 'estabelecimentos',icon: Building2,       label: 'Estabelecimentos' },
  { id: 'configuracoes',   icon: Settings,        label: 'Configurações'    },
];

const PRODUTOS_SUBNAV = [
  { id: 'produtos',    icon: Package,  label: 'Produtos'    },
  { id: 'tributacao',  icon: Receipt,  label: 'Tributação'  },
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
      {/* Logo */}
      <div className="flex items-center gap-3 px-4 py-5 border-b border-white/10">
        <img src="/vulpeslogo.png" alt="Vulpes Fiscal" className="h-14 w-auto flex-shrink-0" />
        {!collapsed && (
          <span className="text-white font-bold text-lg whitespace-nowrap overflow-hidden">
            Vulpes<span className="text-vulpes-orange">Fiscal</span>
          </span>
        )}
      </div>

      {/* Nav */}
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

        {/* Produtos com submenu */}
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

      {/* Logout */}
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
function StatCard({ title, value, change, positive, icon: Icon, gradient }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      className="bg-white rounded-2xl p-5 border border-slate-100 shadow-sm hover:shadow-md transition-shadow"
    >
      <div className="flex items-start justify-between mb-4">
        <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${gradient}`}>
          <Icon size={20} className="text-white" />
        </div>
        <span
          className={`flex items-center gap-1 text-xs font-semibold px-2 py-0.5 rounded-full ${
            positive ? 'bg-emerald-50 text-emerald-600' : 'bg-red-50 text-red-500'
          }`}
        >
          {positive ? <TrendingUp size={11} /> : <TrendingDown size={11} />}
          {change}
        </span>
      </div>
      <p className="text-2xl font-extrabold text-slate-800 mb-0.5">{value}</p>
      <p className="text-slate-500 text-xs">{title}</p>
    </motion.div>
  );
}

// ─── Dashboard (placeholder) ──────────────────────────────────────────────────
const recentActivity = [
  { id: 'NFC-001234', consumer: 'João da Silva',  value: 'R$ 189,90', status: 'Autorizada', time: 'Agora mesmo' },
  { id: 'NFC-001233', consumer: 'Maria Souza',    value: 'R$ 342,00', status: 'Autorizada', time: '5 min atrás' },
  { id: 'NFC-001232', consumer: 'Consumidor',     value: 'R$ 75,50',  status: 'Rejeitada',  time: '12 min atrás' },
  { id: 'NFC-001231', consumer: 'Pedro Lima',     value: 'R$ 920,00', status: 'Autorizada', time: '28 min atrás' },
  { id: 'NFC-001230', consumer: 'Ana Costa',      value: 'R$ 55,00',  status: 'Cancelada',  time: '1h atrás'     },
];

const statusStyle = {
  Autorizada: 'bg-emerald-100 text-emerald-700',
  Rejeitada:  'bg-red-100 text-red-600',
  Cancelada:  'bg-slate-100 text-slate-500',
};

function DashboardContent() {
  const today = new Date().toLocaleDateString('pt-BR', {
    weekday: 'long', day: 'numeric', month: 'long', year: 'numeric',
  });

  return (
    <>
      <div className="mb-8">
        <h1 className="text-2xl font-extrabold text-slate-800 mb-1">Dashboard</h1>
        <p className="text-slate-500 text-sm capitalize">{today}</p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4 mb-8">
        <StatCard title="NFC-e emitidas hoje"  value="128"         change="+12%"  positive icon={FileText}    gradient="bg-gradient-to-br from-vulpes-dark to-vulpes-orange" />
        <StatCard title="Vendas do dia"        value="R$ 14.892"   change="+8,3%" positive icon={ShoppingCart} gradient="bg-gradient-to-br from-emerald-400 to-green-600"    />
        <StatCard title="Produtos ativos"      value="340"         change="+2"    positive icon={Package}      gradient="bg-gradient-to-br from-vulpes-orange to-orange-400"  />
        <StatCard title="Rejeições SEFAZ"      value="3"           change="-1"    positive icon={TrendingDown} gradient="bg-gradient-to-br from-red-400 to-rose-600"          />
      </div>

      <div className="grid lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
          <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
            <h2 className="font-bold text-slate-800 text-sm">NFC-e recentes</h2>
            <button className="flex items-center gap-1 text-vulpes-orange text-xs font-medium">
              Ver todas <ChevronRight size={13} />
            </button>
          </div>
          <div className="divide-y divide-slate-50">
            {recentActivity.map((item) => (
              <div key={item.id} className="flex items-center gap-4 px-6 py-3.5 hover:bg-slate-50 transition-colors">
                <div className="w-8 h-8 rounded-lg bg-vulpes-dark/5 flex items-center justify-center flex-shrink-0">
                  <FileText size={15} className="text-vulpes-medium" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-slate-700 text-xs font-semibold">{item.id}</p>
                  <p className="text-slate-400 text-xs truncate">{item.consumer}</p>
                </div>
                <p className="text-slate-700 text-xs font-bold">{item.value}</p>
                <span className={`text-xs font-medium px-2.5 py-0.5 rounded-full ${statusStyle[item.status] || 'bg-slate-100 text-slate-500'}`}>
                  {item.status}
                </span>
                <p className="text-slate-400 text-xs whitespace-nowrap hidden sm:block">{item.time}</p>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
          <h2 className="font-bold text-slate-800 text-sm mb-5">Ações rápidas</h2>
          <div className="space-y-3">
            {[
              { icon: ShoppingCart, label: 'Nova venda',      desc: 'Registrar e emitir NFC-e',  color: 'text-vulpes-orange bg-vulpes-orange/10' },
              { icon: Users,        label: 'Novo consumidor', desc: 'Cadastrar cliente',           color: 'text-emerald-500 bg-emerald-50'        },
              { icon: Package,      label: 'Novo produto',    desc: 'Adicionar ao catálogo',       color: 'text-orange-400 bg-orange-50'           },
              { icon: FileText,     label: 'Consultar NFC-e', desc: 'Buscar por chave de acesso',  color: 'text-vulpes-medium bg-vulpes-dark/5'    },
            ].map((action) => (
              <button key={action.label} className="w-full flex items-center gap-3 p-3 rounded-xl hover:bg-slate-50 transition-colors text-left group">
                <div className={`w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0 ${action.color}`}>
                  <action.icon size={17} />
                </div>
                <div>
                  <p className="text-slate-700 text-xs font-semibold group-hover:text-vulpes-dark transition-colors">{action.label}</p>
                  <p className="text-slate-400 text-xs">{action.desc}</p>
                </div>
                <ChevronRight size={14} className="ml-auto text-slate-300 group-hover:text-vulpes-orange transition-colors" />
              </button>
            ))}
          </div>
        </div>
      </div>
    </>
  );
}

// ─── Placeholder para páginas ainda não implementadas ────────────────────────
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

// ─── Main page ────────────────────────────────────────────────────────────────
export default function MainPage() {
  const { user } = useAuth();
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [activePage, setActivePage] = useState('dashboard');

  function renderPage() {
    switch (activePage) {
      case 'dashboard':        return <DashboardContent />;
      case 'empresas':         return <EmpresasPage />;
      case 'vendas':           return <VendasPage />;
      case 'nfce':             return <ComingSoon label="NFC-e" />;
      case 'produtos':         return <ProdutosPage />;
      case 'tributacao':       return <TributacaoPage />;
      case 'consumidores':     return <ConsumidoresPage />;
      case 'estabelecimentos': return <EstabelecimentosPage />;
      case 'configuracoes':    return <ComingSoon label="Configurações" />;
      default:                 return <DashboardContent />;
    }
  }

  const roleDisplay  = user?.roles?.[0] ?? '—';
  const usernameDisplay = user?.username ?? '—';
  const initial = usernameDisplay.charAt(0).toUpperCase();

  return (
    <div className="flex h-screen bg-slate-50 overflow-hidden">
      <Sidebar
        collapsed={sidebarCollapsed}
        activePage={activePage}
        onNavigate={setActivePage}
      />

      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Header */}
        <header className="bg-white border-b border-slate-100 px-6 py-3.5 flex items-center gap-4 shadow-sm">
          <button
            onClick={() => setSidebarCollapsed(!sidebarCollapsed)}
            className="text-slate-400 hover:text-slate-700 transition-colors"
          >
            {sidebarCollapsed ? <Menu size={20} /> : <X size={20} />}
          </button>

          <div className="flex items-center gap-3 ml-auto">
            <button className="relative p-2 rounded-xl hover:bg-slate-50 text-slate-400 hover:text-slate-700 transition-colors">
              <Bell size={19} />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-vulpes-orange" />
            </button>

            <div className="flex items-center gap-2.5 pl-3 border-l border-slate-100">
              <div
                className="w-8 h-8 rounded-full flex items-center justify-center text-white text-xs font-bold"
                style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}
              >
                {initial}
              </div>
              <div className="hidden sm:block">
                <p className="text-slate-700 text-xs font-semibold">{usernameDisplay}</p>
                <p className="text-slate-400 text-xs">{roleDisplay}</p>
              </div>
            </div>
          </div>
        </header>

        {/* Conteúdo */}
        <main className="flex-1 overflow-y-auto p-6 lg:p-8">
          {renderPage()}
        </main>
      </div>
    </div>
  );
}
