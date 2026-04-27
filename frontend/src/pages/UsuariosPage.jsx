import { useState, useEffect } from 'react';
import {
  User, Lock, Phone, Mail, Save, AlertTriangle,
  Search, Edit2, Trash2, Plus, X, CheckCircle,
  ChevronLeft, ChevronRight, Eye, EyeOff,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const API = import.meta.env.VITE_API_URL ?? '';

function inputCls(error) {
  return `w-full border rounded-xl px-3 py-2 text-sm focus:outline-none transition-all bg-white dark:bg-slate-700 dark:text-slate-100 ${
    error
      ? 'border-red-400 focus:border-red-400 focus:ring-2 focus:ring-red-200'
      : 'border-slate-200 dark:border-slate-600 focus:border-vulpes-orange focus:ring-2 focus:ring-vulpes-orange/20'
  }`;
}
function labelCls() { return 'block text-xs font-medium text-slate-500 dark:text-slate-400 mb-1'; }

// ─── Toggle de senha ────────────────────────────────────────────────────────���─
function PasswordInput({ value, onChange, placeholder, error }) {
  const [show, setShow] = useState(false);
  return (
    <div className="relative">
      <input
        type={show ? 'text' : 'password'}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className={`${inputCls(error)} pr-10`}
      />
      <button
        type="button"
        onClick={() => setShow((v) => !v)}
        className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
      >
        {show ? <EyeOff size={15} /> : <Eye size={15} />}
      </button>
    </div>
  );
}

// ─── Meu Perfil ───────────────────────────────────────────────────────────────
function MeuPerfil({ token }) {
  const [perfil,  setPerfil]  = useState(null);
  const [form,    setForm]    = useState({ nome: '', telefone: '', senha: '', confirmar: '' });
  const [loading, setLoading] = useState(true);
  const [saving,  setSaving]  = useState(false);
  const [errors,  setErrors]  = useState({});
  const [toast,   setToast]   = useState(null);

  useEffect(() => {
    fetch(`${API}/usuarios/me`, { headers: { Authorization: `Bearer ${token}` } })
      .then((r) => r.json())
      .then((d) => {
        setPerfil(d);
        setForm({ nome: d.nome ?? '', telefone: d.telefone ?? '', senha: '', confirmar: '' });
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [token]);

  function showToast(msg, type = 'success') {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3500);
  }

  function validate() {
    const e = {};
    if (!form.nome.trim()) e.nome = 'Nome é obrigatório';
    if (form.senha && form.senha !== form.confirmar) e.confirmar = 'As senhas não coincidem';
    if (form.senha && form.senha.length < 6) e.senha = 'Senha deve ter ao menos 6 caracteres';
    return e;
  }

  async function handleSave(ev) {
    ev.preventDefault();
    const e = validate();
    if (Object.keys(e).length) { setErrors(e); return; }
    setSaving(true);
    try {
      const body = {
        nome:     form.nome     || null,
        telefone: form.telefone || null,
        senha:    form.senha    || null,
      };
      const res = await fetch(`${API}/usuarios/me`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
        body: JSON.stringify(body),
      });
      if (!res.ok) throw new Error('Erro ao salvar.');
      setForm((p) => ({ ...p, senha: '', confirmar: '' }));
      setErrors({});
      showToast('Perfil atualizado com sucesso!');
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setSaving(false);
    }
  }

  if (loading) return <div className="flex items-center justify-center py-20 text-slate-400 text-sm animate-pulse">Carregando…</div>;

  return (
    <div className="max-w-lg">
      {toast && (
        <div className={`mb-5 flex items-center gap-2 px-4 py-3 rounded-xl text-sm font-medium ${
          toast.type === 'error' ? 'bg-red-50 text-red-700 border border-red-200' : 'bg-emerald-50 text-emerald-700 border border-emerald-200'
        }`}>
          <CheckCircle size={15} />
          {toast.msg}
        </div>
      )}

      {/* Campos somente leitura */}
      <div className="bg-slate-50 dark:bg-slate-700/50 rounded-2xl p-4 mb-6 space-y-3">
        <p className="text-xs font-semibold text-slate-400 dark:text-slate-500 uppercase tracking-wide mb-1">Dados da conta</p>
        {[
          { icon: Mail,  label: 'E-mail',   value: perfil?.email    ?? '—' },
          { icon: User,  label: 'Username', value: perfil?.username ?? '—' },
        ].map(({ icon: Icon, label, value }) => (
          <div key={label} className="flex items-center gap-3">
            <Icon size={14} className="text-slate-400 flex-shrink-0" />
            <div className="flex items-baseline gap-2 min-w-0">
              <span className="text-xs text-slate-400 dark:text-slate-500 whitespace-nowrap">{label}:</span>
              <span className="text-sm font-medium text-slate-700 dark:text-slate-200 truncate">{value}</span>
            </div>
          </div>
        ))}
      </div>

      <form onSubmit={handleSave} className="space-y-5">
        <div>
          <label className={labelCls()}>Nome completo <span className="text-red-400">*</span></label>
          <div className="relative">
            <User size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              value={form.nome}
              onChange={(e) => { setForm((p) => ({ ...p, nome: e.target.value })); setErrors((p) => ({ ...p, nome: '' })); }}
              className={`${inputCls(errors.nome)} pl-8`}
              placeholder="Seu nome completo"
            />
          </div>
          {errors.nome && <p className="mt-1 text-red-500 text-xs">{errors.nome}</p>}
        </div>

        <div>
          <label className={labelCls()}>Telefone</label>
          <div className="relative">
            <Phone size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              value={form.telefone}
              onChange={(e) => setForm((p) => ({ ...p, telefone: e.target.value }))}
              className={`${inputCls()} pl-8`}
              placeholder="(11) 99999-9999"
            />
          </div>
        </div>

        <div className="border-t border-slate-100 dark:border-slate-700 pt-5">
          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-4">Alterar senha (opcional)</p>
          <div className="space-y-4">
            <div>
              <label className={labelCls()}>Nova senha</label>
              <PasswordInput
                value={form.senha}
                onChange={(e) => { setForm((p) => ({ ...p, senha: e.target.value })); setErrors((p) => ({ ...p, senha: '' })); }}
                placeholder="Mínimo 6 caracteres"
                error={errors.senha}
              />
              {errors.senha && <p className="mt-1 text-red-500 text-xs">{errors.senha}</p>}
            </div>
            <div>
              <label className={labelCls()}>Confirmar nova senha</label>
              <PasswordInput
                value={form.confirmar}
                onChange={(e) => { setForm((p) => ({ ...p, confirmar: e.target.value })); setErrors((p) => ({ ...p, confirmar: '' })); }}
                placeholder="Repita a senha"
                error={errors.confirmar}
              />
              {errors.confirmar && <p className="mt-1 text-red-500 text-xs">{errors.confirmar}</p>}
            </div>
          </div>
        </div>

        <button
          type="submit"
          disabled={saving}
          className="flex items-center gap-2 px-6 py-2.5 rounded-xl text-white text-sm font-semibold disabled:opacity-60 hover:scale-[1.02] transition-all"
          style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}
        >
          <Save size={15} />
          {saving ? 'Salvando…' : 'Salvar alterações'}
        </button>
      </form>
    </div>
  );
}

// ─── Modal de edição de usuário ───────────────────────────────────────────────
function EditUserModal({ usuario, token, onClose, onSaved }) {
  const [form, setForm]     = useState({
    nome:             usuario.nome     ?? '',
    email:            usuario.email    ?? '',
    username:         '',
    telefone:         '',
    senha:            '',
    ativo:            usuario.ativo    ?? true,
    perfilId:         usuario.perfilId ?? '',
  });
  const [saving,  setSaving]  = useState(false);
  const [errors,  setErrors]  = useState({});
  const [apiError, setApiError] = useState('');

  async function handleSave(ev) {
    ev.preventDefault();
    setApiError('');
    setSaving(true);
    try {
      const body = {
        nome:             form.nome     || null,
        email:            form.email    || null,
        username:         form.username || null,
        telefone:         form.telefone || null,
        senha:            form.senha    || null,
        ativo:            form.ativo,
        perfilId:         form.perfilId ? Number(form.perfilId) : null,
      };
      const res = await fetch(
        `${API}/usuarios/empresa/${usuario.empresaId}/estabelecimento/${usuario.estabelecimentoId}/${usuario.id}`,
        { method: 'PUT', headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` }, body: JSON.stringify(body) },
      );
      if (!res.ok) {
        const bd = await res.json().catch(() => ({}));
        throw new Error(bd.erros?.map(e => `${e.campo}: ${e.mensagem}`).join(' | ') || bd.mensagem || `Erro ${res.status}`);
      }
      onSaved();
    } catch (err) {
      setApiError(err.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-2xl w-full max-w-md max-h-[90vh] flex flex-col">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 dark:border-slate-700">
          <h2 className="font-bold text-slate-800 dark:text-slate-100">Editar usuário #{usuario.id}</h2>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700"><X size={20} /></button>
        </div>
        <form onSubmit={handleSave} className="flex-1 overflow-y-auto p-6 space-y-4">
          {apiError && (
            <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl px-4 py-3">
              <AlertTriangle size={14} className="mt-0.5 flex-shrink-0" />{apiError}
            </div>
          )}
          {[
            { key: 'nome',     label: 'Nome',     placeholder: 'Nome completo',    icon: User  },
            { key: 'email',    label: 'E-mail',   placeholder: 'email@exemplo.com', icon: Mail  },
            { key: 'username', label: 'Username', placeholder: 'usuário',           icon: User  },
            { key: 'telefone', label: 'Telefone', placeholder: '(11) 99999-9999',   icon: Phone },
          ].map(({ key, label, placeholder, icon: Icon }) => (
            <div key={key}>
              <label className={labelCls()}>{label}</label>
              <div className="relative">
                <Icon size={13} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                <input
                  value={form[key]}
                  onChange={(e) => setForm((p) => ({ ...p, [key]: e.target.value }))}
                  placeholder={placeholder}
                  className={`${inputCls(errors[key])} pl-8`}
                />
              </div>
            </div>
          ))}
          <div>
            <label className={labelCls()}>Nova senha (opcional)</label>
            <PasswordInput value={form.senha} onChange={(e) => setForm((p) => ({ ...p, senha: e.target.value }))} placeholder="Deixe em branco para manter" />
          </div>
          <div className="flex items-center justify-between py-1">
            <span className={labelCls()}>Usuário ativo</span>
            <button
              type="button"
              onClick={() => setForm((p) => ({ ...p, ativo: !p.ativo }))}
              className={`relative w-11 h-6 rounded-full transition-colors ${form.ativo ? 'bg-emerald-500' : 'bg-slate-300'}`}
            >
              <span className={`absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform ${form.ativo ? 'translate-x-5' : 'translate-x-0'}`} />
            </button>
          </div>
        </form>
        <div className="flex justify-end gap-3 px-6 py-4 border-t border-slate-100 dark:border-slate-700">
          <button onClick={onClose} className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50">Cancelar</button>
          <button
            onClick={handleSave}
            disabled={saving}
            className="flex items-center gap-2 px-5 py-2 rounded-xl text-white text-sm font-semibold disabled:opacity-60"
            style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}
          >
            <Save size={14} />
            {saving ? 'Salvando…' : 'Salvar'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── Gerenciar Usuários (ADMIN/SUPORTE) ───────────────────────────────────────
function GerenciarUsuarios({ token }) {
  const [empresas,       setEmpresas]       = useState([]);
  const [estabelecimentos, setEstabelecimentos] = useState([]);
  const [empresaId,      setEmpresaId]      = useState('');
  const [estabId,        setEstabId]        = useState('');
  const [nome,           setNome]           = useState('');
  const [usuarios,       setUsuarios]       = useState(null);
  const [page,           setPage]           = useState(0);
  const [loading,        setLoading]        = useState(false);
  const [editTarget,     setEditTarget]     = useState(null);
  const [toast,          setToast]          = useState(null);

  function showToast(msg, type = 'success') {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3000);
  }

  useEffect(() => {
    fetch(`${API}/empresas?tamanho-pagina=200&pagina=0`, { headers: { Authorization: `Bearer ${token}` } })
      .then((r) => r.json()).then((d) => setEmpresas(d.content ?? [])).catch(() => {});
  }, [token]);

  useEffect(() => {
    if (!empresaId) { setEstabelecimentos([]); setEstabId(''); return; }
    fetch(`${API}/estabelecimentos/empresa/${empresaId}?tamanho-pagina=100&pagina=0`, { headers: { Authorization: `Bearer ${token}` } })
      .then((r) => r.json()).then((d) => setEstabelecimentos(d.content ?? [])).catch(() => {});
  }, [empresaId, token]);

  async function buscar(pg = 0) {
    if (!empresaId || !estabId) return;
    setLoading(true);
    try {
      const p = new URLSearchParams({ pagina: pg, 'tamanho-pagina': 10 });
      if (nome) p.set('nome', nome);
      const res = await fetch(`${API}/usuarios/empresa/${empresaId}/estabelecimento/${estabId}?${p}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await res.json();
      setUsuarios(data);
      setPage(pg);
    } catch {
      setUsuarios(null);
    } finally {
      setLoading(false);
    }
  }

  async function deletar(u) {
    if (!confirm(`Deletar usuário "${u.nome}"?`)) return;
    try {
      const res = await fetch(`${API}/usuarios/empresa/${u.empresaId}/estabelecimento/${u.estabelecimentoId}/${u.id}`, {
        method: 'DELETE', headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error();
      showToast('Usuário deletado.');
      buscar(page);
    } catch {
      showToast('Erro ao deletar.', 'error');
    }
  }

  const items = usuarios?.content ?? [];

  return (
    <div className="space-y-5">
      {editTarget && (
        <EditUserModal
          usuario={editTarget}
          token={token}
          onClose={() => setEditTarget(null)}
          onSaved={() => { setEditTarget(null); showToast('Usuário atualizado!'); buscar(page); }}
        />
      )}

      {toast && (
        <div className={`fixed top-5 right-5 z-[100] px-5 py-3 rounded-xl shadow-xl text-white text-sm font-medium ${toast.type === 'error' ? 'bg-red-500' : 'bg-emerald-500'}`}>
          {toast.msg}
        </div>
      )}

      {/* Filtros */}
      <div className="bg-white dark:bg-slate-800 rounded-2xl border border-slate-100 dark:border-slate-700 shadow-sm p-5">
        <h3 className="text-sm font-semibold text-slate-700 dark:text-slate-200 mb-4">Filtros</h3>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div>
            <label className={labelCls()}>Empresa <span className="text-red-400">*</span></label>
            <select value={empresaId} onChange={(e) => setEmpresaId(e.target.value)} className={inputCls()}>
              <option value="">Selecione…</option>
              {empresas.map((e) => <option key={e.id} value={String(e.id)}>{e.nomeFantasia || e.razaoSocial}</option>)}
            </select>
          </div>
          <div>
            <label className={labelCls()}>Estabelecimento <span className="text-red-400">*</span></label>
            <select value={estabId} onChange={(e) => setEstabId(e.target.value)} disabled={!empresaId} className={inputCls()}>
              <option value="">Selecione…</option>
              {estabelecimentos.map((e) => <option key={e.id} value={String(e.id)}>{e.nomeFantasia || e.cnpj || `#${e.id}`}</option>)}
            </select>
          </div>
          <div>
            <label className={labelCls()}>Nome</label>
            <input value={nome} onChange={(e) => setNome(e.target.value)} className={inputCls()} placeholder="Filtrar por nome…" />
          </div>
          <div className="flex items-end">
            <button
              onClick={() => buscar(0)}
              disabled={!empresaId || !estabId || loading}
              className="w-full flex items-center justify-center gap-2 px-4 py-2 rounded-xl text-white text-sm font-semibold disabled:opacity-50 hover:scale-[1.02] transition-all"
              style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}
            >
              <Search size={14} />
              {loading ? 'Buscando…' : 'Pesquisar'}
            </button>
          </div>
        </div>
      </div>

      {/* Tabela */}
      {usuarios && (
        <div className="bg-white dark:bg-slate-800 rounded-2xl border border-slate-100 dark:border-slate-700 shadow-sm overflow-hidden">
          <div className="px-6 py-4 border-b border-slate-100 dark:border-slate-700 flex items-center justify-between">
            <h3 className="font-bold text-slate-800 dark:text-slate-100 text-sm">
              Usuários
              <span className="ml-2 text-xs font-normal text-slate-400">{usuarios.totalElements} encontrado(s)</span>
            </h3>
          </div>

          {items.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-12 text-slate-400">
              <User size={32} className="mb-2 opacity-30" />
              <p className="text-sm">Nenhum usuário encontrado.</p>
            </div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="bg-slate-50 dark:bg-slate-700 border-b border-slate-100 dark:border-slate-600">
                      {['ID', 'Nome', 'E-mail', 'Status', 'Ações'].map((h) => (
                        <th key={h} className="px-5 py-3.5 text-left text-xs font-semibold text-slate-500 dark:text-slate-300">{h}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100 dark:divide-slate-700">
                    {items.map((u) => (
                      <tr key={u.id} className="hover:bg-slate-50 dark:hover:bg-slate-700/50 transition-colors">
                        <td className="px-5 py-4 text-xs font-mono text-slate-500 dark:text-slate-400">#{u.id}</td>
                        <td className="px-5 py-4 text-xs font-medium text-slate-700 dark:text-slate-200">{u.nome ?? '—'}</td>
                        <td className="px-5 py-4 text-xs text-slate-500 dark:text-slate-400">{u.email ?? '—'}</td>
                        <td className="px-5 py-4">
                          <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${u.ativo ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-500'}`}>
                            {u.ativo ? 'Ativo' : 'Inativo'}
                          </span>
                        </td>
                        <td className="px-5 py-4">
                          <div className="flex items-center gap-1">
                            <button onClick={() => setEditTarget(u)} className="p-1.5 rounded-lg text-slate-400 hover:text-blue-500 hover:bg-blue-50 transition-colors" title="Editar"><Edit2 size={14} /></button>
                            <button onClick={() => deletar(u)} className="p-1.5 rounded-lg text-slate-400 hover:text-red-500 hover:bg-red-50 transition-colors" title="Deletar"><Trash2 size={14} /></button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              {usuarios.totalPages > 1 && (
                <div className="flex items-center justify-between px-6 py-4 border-t border-slate-100 dark:border-slate-700">
                  <p className="text-xs text-slate-400">{page * 10 + 1}–{Math.min((page + 1) * 10, usuarios.totalElements)} de {usuarios.totalElements}</p>
                  <div className="flex gap-2">
                    <button onClick={() => buscar(page - 1)} disabled={page === 0} className="p-2 rounded-lg border border-slate-200 text-slate-500 hover:bg-slate-50 disabled:opacity-40"><ChevronLeft size={14} /></button>
                    <span className="text-xs text-slate-500 px-1 self-center">{page + 1} / {usuarios.totalPages}</span>
                    <button onClick={() => buscar(page + 1)} disabled={page >= usuarios.totalPages - 1} className="p-2 rounded-lg border border-slate-200 text-slate-500 hover:bg-slate-50 disabled:opacity-40"><ChevronRight size={14} /></button>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      )}
    </div>
  );
}

// ─── UsuariosPage ─────────────────────────────────────────────────────────────
export default function UsuariosPage() {
  const { token, user } = useAuth();
  const isAdmin = user?.hasRole('ADMINISTRADOR', 'SUPORTE');
  const [tab, setTab] = useState('perfil');

  const tabs = [
    { id: 'perfil',   label: 'Meu Perfil' },
    ...(isAdmin ? [{ id: 'usuarios', label: 'Gerenciar Usuários' }] : []),
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-extrabold text-slate-800 dark:text-slate-100 mb-1">
          {tab === 'perfil' ? 'Meu Perfil' : 'Gerenciar Usuários'}
        </h1>
        <p className="text-slate-500 dark:text-slate-400 text-sm">
          {tab === 'perfil' ? 'Atualize suas informações pessoais.' : 'Gerencie os usuários do sistema.'}
        </p>
      </div>

      {/* Tabs */}
      {isAdmin && (
        <div className="flex gap-1 bg-slate-100 dark:bg-slate-700 p-1 rounded-xl w-fit">
          {tabs.map((t) => (
            <button
              key={t.id}
              onClick={() => setTab(t.id)}
              className={`px-5 py-2 rounded-lg text-sm font-medium transition-all ${
                tab === t.id
                  ? 'bg-white dark:bg-slate-600 text-slate-800 dark:text-slate-100 shadow-sm'
                  : 'text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200'
              }`}
            >
              {t.label}
            </button>
          ))}
        </div>
      )}

      {tab === 'perfil'   && <MeuPerfil token={token} />}
      {tab === 'usuarios' && <GerenciarUsuarios token={token} />}
    </div>
  );
}
