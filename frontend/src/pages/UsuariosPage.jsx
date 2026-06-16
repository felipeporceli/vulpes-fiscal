import { useState, useEffect, useRef } from 'react';
import {
  User, Lock, Phone, Mail, Save, AlertTriangle,
  Search, Edit2, Trash2, X, CheckCircle,
  ChevronLeft, ChevronRight, Eye, EyeOff, UserCog, UserCircle,
  Plus, Download, ChevronDown, FileText, Table,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { exportCsvUsuario, exportXlsxUsuario } from '../utils/exportUtils';
import { maskCpf, maskTelefone } from '../utils/masks';

const API = import.meta.env.VITE_API_URL ?? '';

const ROLES_DISPONIVEIS = [
  { value: 'ROLE_ADMINISTRADOR', label: 'Administrador' },
  { value: 'ROLE_SUPORTE',       label: 'Suporte'       },
  { value: 'ROLE_EMPRESARIO',    label: 'Empresário'    },
  { value: 'ROLE_GERENTE',       label: 'Gerente'       },
  { value: 'ROLE_CAIXA',         label: 'Caixa'         },
  { value: 'ROLE_VENDEDOR',      label: 'Vendedor'      },
];

function inputCls(error) {
  return `w-full border rounded-xl px-3 py-2 text-sm focus:outline-none transition-all bg-white dark:bg-slate-700 dark:text-slate-100 ${
    error
      ? 'border-red-400 focus:border-red-400 focus:ring-2 focus:ring-red-200'
      : 'border-slate-200 dark:border-slate-600 focus:border-vulpes-orange focus:ring-2 focus:ring-vulpes-orange/20'
  }`;
}
function labelCls() { return 'block text-xs font-medium text-slate-500 dark:text-slate-400 mb-1'; }

// ─── Toggle de senha ──────────────────────────────────────────────────────────
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
  const [form,    setForm]    = useState({ nome: '', email: '', telefone: '', senha: '', confirmar: '' });
  const [loading, setLoading] = useState(true);
  const [saving,  setSaving]  = useState(false);
  const [errors,  setErrors]  = useState({});
  const [toast,   setToast]   = useState(null);

  useEffect(() => {
    fetch(`${API}/usuarios/me`, { headers: { Authorization: `Bearer ${token}` } })
      .then((r) => r.json())
      .then((d) => {
        setPerfil(d);
        setForm({ nome: d.nome ?? '', email: d.email ?? '', telefone: d.telefone ?? '', senha: '', confirmar: '' });
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
    if (!form.nome.trim())  e.nome  = 'Nome é obrigatório';
    if (!form.email.trim()) e.email = 'E-mail é obrigatório';
    if (form.senha && form.senha.length < 6)            e.senha    = 'Senha deve ter ao menos 6 caracteres';
    if (form.senha && form.senha !== form.confirmar)    e.confirmar = 'As senhas não coincidem';
    return e;
  }

  async function handleSave(ev) {
    ev.preventDefault();
    const e = validate();
    if (Object.keys(e).length) { setErrors(e); return; }
    setSaving(true);
    try {
      const res = await fetch(`${API}/usuarios/me`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
        body: JSON.stringify({ nome: form.nome || null, email: form.email || null, telefone: form.telefone || null, senha: form.senha || null }),
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
    <div className="max-w-xl">
      {toast && (
        <div className={`mb-6 flex items-center gap-2 px-4 py-3 rounded-xl text-sm font-medium ${
          toast.type === 'error' ? 'bg-red-50 text-red-700 border border-red-200' : 'bg-emerald-50 text-emerald-700 border border-emerald-200'
        }`}>
          <CheckCircle size={15} />{toast.msg}
        </div>
      )}

      <div className="flex items-center gap-3 mb-7 p-4 bg-slate-50 dark:bg-slate-700/50 rounded-2xl border border-slate-100 dark:border-slate-700">
        <div
          className="w-12 h-12 rounded-full flex items-center justify-center text-white text-lg font-bold flex-shrink-0"
          style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}
        >
          {(perfil?.nome ?? perfil?.username ?? '?').charAt(0).toUpperCase()}
        </div>
        <div>
          <p className="text-sm font-semibold text-slate-700 dark:text-slate-200">{perfil?.nome ?? '—'}</p>
          <p className="text-xs text-slate-400 dark:text-slate-500">@{perfil?.username ?? '—'}</p>
        </div>
      </div>

      <form onSubmit={handleSave} className="space-y-5">
        <p className="text-xs font-semibold text-slate-400 dark:text-slate-500 uppercase tracking-wide">Dados pessoais</p>

        <div>
          <label className={labelCls()}>Nome completo <span className="text-red-400">*</span></label>
          <div className="relative">
            <User size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input value={form.nome} onChange={(e) => { setForm((p) => ({ ...p, nome: e.target.value })); setErrors((p) => ({ ...p, nome: '' })); }}
              className={`${inputCls(errors.nome)} pl-8`} placeholder="Seu nome completo" />
          </div>
          {errors.nome && <p className="mt-1 text-red-500 text-xs">{errors.nome}</p>}
        </div>

        <div>
          <label className={labelCls()}>E-mail <span className="text-red-400">*</span></label>
          <div className="relative">
            <Mail size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input type="email" value={form.email} onChange={(e) => { setForm((p) => ({ ...p, email: e.target.value })); setErrors((p) => ({ ...p, email: '' })); }}
              className={`${inputCls(errors.email)} pl-8`} placeholder="seu@email.com" />
          </div>
          {errors.email && <p className="mt-1 text-red-500 text-xs">{errors.email}</p>}
        </div>

        <div>
          <label className={labelCls()}>Telefone</label>
          <div className="relative">
            <Phone size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input value={form.telefone} onChange={(e) => setForm((p) => ({ ...p, telefone: maskTelefone(e.target.value) }))}
              className={`${inputCls()} pl-8`} placeholder="(11) 99999-9999" />
          </div>
        </div>

        <div className="border-t border-slate-100 dark:border-slate-700 pt-5">
          <p className="text-xs font-semibold text-slate-400 dark:text-slate-500 uppercase tracking-wide mb-4">Alterar senha</p>
          <div className="space-y-4">
            <div>
              <label className={labelCls()}>Nova senha <span className="text-slate-400 font-normal">(opcional)</span></label>
              <PasswordInput value={form.senha} onChange={(e) => { setForm((p) => ({ ...p, senha: e.target.value })); setErrors((p) => ({ ...p, senha: '' })); }}
                placeholder="Mínimo 6 caracteres" error={errors.senha} />
              {errors.senha && <p className="mt-1 text-red-500 text-xs">{errors.senha}</p>}
            </div>
            <div>
              <label className={labelCls()}>Confirmar nova senha</label>
              <PasswordInput value={form.confirmar} onChange={(e) => { setForm((p) => ({ ...p, confirmar: e.target.value })); setErrors((p) => ({ ...p, confirmar: '' })); }}
                placeholder="Repita a senha" error={errors.confirmar} />
              {errors.confirmar && <p className="mt-1 text-red-500 text-xs">{errors.confirmar}</p>}
            </div>
          </div>
        </div>

        <div className="pt-1">
          <button type="submit" disabled={saving}
            className="flex items-center gap-2 px-6 py-2.5 rounded-xl text-white text-sm font-semibold disabled:opacity-60 hover:scale-[1.02] transition-all"
            style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}>
            <Save size={15} />
            {saving ? 'Salvando…' : 'Salvar alterações'}
          </button>
        </div>
      </form>
    </div>
  );
}

// ─── Modal de cadastro de usuário ─────────────────────────────────────────────
function NovoUsuarioModal({ token, onClose, onSaved }) {
  const [empresas,         setEmpresas]         = useState([]);
  const [estabelecimentos, setEstabelecimentos] = useState([]);
  const [form, setForm] = useState({
    empresaId: '', estabelecimentoId: '',
    nome: '', email: '', username: '', cpf: '', telefone: '',
    senha: '', confirmar: '',
    role: 'ROLE_VENDEDOR',
    ativo: true,
  });
  const [saving,   setSaving]   = useState(false);
  const [errors,   setErrors]   = useState({});
  const [apiError, setApiError] = useState('');

  useEffect(() => {
    fetch(`${API}/empresas?tamanho-pagina=200&pagina=0`, { headers: { Authorization: `Bearer ${token}` } })
      .then((r) => r.json()).then((d) => setEmpresas(d.content ?? [])).catch(() => {});
  }, [token]);

  useEffect(() => {
    if (!form.empresaId) { setEstabelecimentos([]); setForm((p) => ({ ...p, estabelecimentoId: '' })); return; }
    fetch(`${API}/estabelecimentos/empresa/${form.empresaId}?tamanho-pagina=100&pagina=0`, { headers: { Authorization: `Bearer ${token}` } })
      .then((r) => r.json()).then((d) => setEstabelecimentos(d.content ?? [])).catch(() => {});
  }, [form.empresaId, token]);

  function f(key, val) { setForm((p) => ({ ...p, [key]: val })); setErrors((p) => ({ ...p, [key]: '' })); }

  function validate() {
    const e = {};
    if (!form.empresaId)         e.empresaId         = 'Selecione a empresa';
    if (!form.estabelecimentoId) e.estabelecimentoId = 'Selecione o estabelecimento';
    if (!form.nome.trim())       e.nome              = 'Campo obrigatório';
    if (!form.email.trim())      e.email             = 'Campo obrigatório';
    if (!form.username.trim())   e.username          = 'Campo obrigatório';
    if (!form.senha.trim())      e.senha             = 'Campo obrigatório';
    if (form.senha.length < 6)   e.senha             = 'Mínimo 6 caracteres';
    if (form.senha !== form.confirmar) e.confirmar   = 'As senhas não coincidem';
    return e;
  }

  async function handleSave(ev) {
    ev.preventDefault();
    setApiError('');
    const e = validate();
    if (Object.keys(e).length) { setErrors(e); return; }
    setSaving(true);
    try {
      const body = {
        nome:     form.nome     || null,
        email:    form.email    || null,
        username: form.username || null,
        cpf:      form.cpf      || null,
        telefone: form.telefone || null,
        senha:    form.senha,
        ativo:    form.ativo,
        roles:    [form.role],
      };
      const res = await fetch(
        `${API}/usuarios/empresa/${form.empresaId}/estabelecimento/${form.estabelecimentoId}`,
        { method: 'POST', headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` }, body: JSON.stringify(body) },
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
      <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-2xl w-full max-w-lg max-h-[92vh] flex flex-col">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 dark:border-slate-700">
          <h2 className="font-bold text-slate-800 dark:text-slate-100">Novo usuário</h2>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700"><X size={20} /></button>
        </div>

        <form onSubmit={handleSave} className="flex-1 overflow-y-auto p-6 space-y-4">
          {apiError && (
            <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl px-4 py-3">
              <AlertTriangle size={14} className="mt-0.5 flex-shrink-0" />{apiError}
            </div>
          )}

          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide">Vínculo</p>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className={labelCls()}>Empresa <span className="text-red-400">*</span></label>
              <select value={form.empresaId} onChange={(e) => f('empresaId', e.target.value)} className={inputCls(errors.empresaId)}>
                <option value="">Selecione…</option>
                {empresas.map((e) => <option key={e.id} value={String(e.id)}>{e.nomeFantasia || e.razaoSocial}</option>)}
              </select>
              {errors.empresaId && <p className="mt-1 text-red-500 text-xs">{errors.empresaId}</p>}
            </div>
            <div>
              <label className={labelCls()}>Estabelecimento <span className="text-red-400">*</span></label>
              <select value={form.estabelecimentoId} onChange={(e) => f('estabelecimentoId', e.target.value)} disabled={!form.empresaId} className={inputCls(errors.estabelecimentoId)}>
                <option value="">Selecione…</option>
                {estabelecimentos.map((e) => <option key={e.id} value={String(e.id)}>{e.nomeFantasia || e.cnpj || `#${e.id}`}</option>)}
              </select>
              {errors.estabelecimentoId && <p className="mt-1 text-red-500 text-xs">{errors.estabelecimentoId}</p>}
            </div>
          </div>

          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide pt-1">Dados pessoais</p>

          <div className="grid grid-cols-2 gap-3">
            <div className="col-span-2">
              <label className={labelCls()}>Nome completo <span className="text-red-400">*</span></label>
              <div className="relative">
                <User size={13} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                <input value={form.nome} onChange={(e) => f('nome', e.target.value)} placeholder="Nome completo" className={`${inputCls(errors.nome)} pl-8`} />
              </div>
              {errors.nome && <p className="mt-1 text-red-500 text-xs">{errors.nome}</p>}
            </div>
            <div>
              <label className={labelCls()}>E-mail <span className="text-red-400">*</span></label>
              <div className="relative">
                <Mail size={13} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                <input type="email" value={form.email} onChange={(e) => f('email', e.target.value)} placeholder="email@exemplo.com" className={`${inputCls(errors.email)} pl-8`} />
              </div>
              {errors.email && <p className="mt-1 text-red-500 text-xs">{errors.email}</p>}
            </div>
            <div>
              <label className={labelCls()}>Username <span className="text-red-400">*</span></label>
              <div className="relative">
                <User size={13} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                <input value={form.username} onChange={(e) => f('username', e.target.value)} placeholder="usuário" className={`${inputCls(errors.username)} pl-8`} />
              </div>
              {errors.username && <p className="mt-1 text-red-500 text-xs">{errors.username}</p>}
            </div>
            <div>
              <label className={labelCls()}>CPF</label>
              <input value={form.cpf} onChange={(e) => f('cpf', maskCpf(e.target.value))} placeholder="000.000.000-00" className={inputCls()} />
            </div>
            <div>
              <label className={labelCls()}>Telefone</label>
              <div className="relative">
                <Phone size={13} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                <input value={form.telefone} onChange={(e) => f('telefone', maskTelefone(e.target.value))} placeholder="(11) 99999-9999" className={`${inputCls()} pl-8`} />
              </div>
            </div>
          </div>

          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide pt-1">Acesso</p>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className={labelCls()}>Perfil <span className="text-red-400">*</span></label>
              <select value={form.role} onChange={(e) => setForm((p) => ({ ...p, role: e.target.value }))} className={inputCls()}>
                {ROLES_DISPONIVEIS.map((r) => <option key={r.value} value={r.value}>{r.label}</option>)}
              </select>
            </div>
            <div className="flex flex-col justify-end">
              <div className="flex items-center justify-between p-3 rounded-xl bg-slate-50 dark:bg-slate-700/60">
                <span className="text-xs font-medium text-slate-500 dark:text-slate-400">Usuário ativo</span>
                <button type="button" onClick={() => setForm((p) => ({ ...p, ativo: !p.ativo }))}
                  className={`relative w-11 h-6 rounded-full transition-colors ${form.ativo ? 'bg-emerald-500' : 'bg-slate-300'}`}>
                  <span className={`absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform ${form.ativo ? 'translate-x-5' : 'translate-x-0'}`} />
                </button>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className={labelCls()}>Senha <span className="text-red-400">*</span></label>
              <PasswordInput value={form.senha} onChange={(e) => f('senha', e.target.value)} placeholder="Mínimo 6 caracteres" error={errors.senha} />
              {errors.senha && <p className="mt-1 text-red-500 text-xs">{errors.senha}</p>}
            </div>
            <div>
              <label className={labelCls()}>Confirmar senha <span className="text-red-400">*</span></label>
              <PasswordInput value={form.confirmar} onChange={(e) => f('confirmar', e.target.value)} placeholder="Repita a senha" error={errors.confirmar} />
              {errors.confirmar && <p className="mt-1 text-red-500 text-xs">{errors.confirmar}</p>}
            </div>
          </div>
        </form>

        <div className="flex justify-end gap-3 px-6 py-4 border-t border-slate-100 dark:border-slate-700">
          <button onClick={onClose} className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50">Cancelar</button>
          <button onClick={handleSave} disabled={saving}
            className="flex items-center gap-2 px-5 py-2 rounded-xl text-white text-sm font-semibold disabled:opacity-60"
            style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}>
            <Plus size={14} />
            {saving ? 'Cadastrando…' : 'Cadastrar usuário'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── Modal de edição de usuário ───────────────────────────────────────────────
function EditUserModal({ usuario, token, onClose, onSaved }) {
  const [form, setForm] = useState({
    nome:      usuario.nome      ?? '',
    email:     usuario.email     ?? '',
    username:  usuario.username  ?? '',
    telefone:  usuario.telefone  ?? '',
    senha: '',
    ativo: usuario.ativo ?? true,
  });
  const [saving,   setSaving]   = useState(false);
  const [apiError, setApiError] = useState('');

  async function handleSave(ev) {
    ev.preventDefault();
    setApiError('');
    setSaving(true);
    try {
      const body = {
        nome: form.nome || null, email: form.email || null,
        username: form.username || null, telefone: form.telefone || null,
        senha: form.senha || null, ativo: form.ativo,
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
          <h2 className="font-bold text-slate-800 dark:text-slate-100">Editar usuário</h2>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700"><X size={20} /></button>
        </div>

        <form onSubmit={handleSave} className="flex-1 overflow-y-auto p-6 space-y-4">
          {apiError && (
            <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl px-4 py-3">
              <AlertTriangle size={14} className="mt-0.5 flex-shrink-0" />{apiError}
            </div>
          )}

          {[
            { key: 'nome',     label: 'Nome',     placeholder: 'Nome completo',     icon: User  },
            { key: 'email',    label: 'E-mail',   placeholder: 'email@exemplo.com', icon: Mail  },
            { key: 'username', label: 'Username', placeholder: 'usuário',           icon: User  },
            { key: 'telefone', label: 'Telefone', placeholder: '(11) 99999-9999',   icon: Phone },
          ].map(({ key, label, placeholder, icon: Icon }) => (
            <div key={key}>
              <label className={labelCls()}>{label}</label>
              <div className="relative">
                <Icon size={13} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                <input value={form[key]}
                  onChange={(e) => setForm((p) => ({ ...p, [key]: key === 'telefone' ? maskTelefone(e.target.value) : e.target.value }))}
                  placeholder={placeholder} className={`${inputCls()} pl-8`} />
              </div>
            </div>
          ))}

          <div>
            <label className={labelCls()}>Nova senha <span className="text-slate-400 font-normal">(opcional)</span></label>
            <PasswordInput value={form.senha} onChange={(e) => setForm((p) => ({ ...p, senha: e.target.value }))} placeholder="Deixe em branco para manter" />
          </div>

          <div className="flex items-center justify-between p-3 rounded-xl bg-slate-50 dark:bg-slate-700/60">
            <span className="text-xs font-medium text-slate-500 dark:text-slate-400">Usuário ativo</span>
            <button type="button" onClick={() => setForm((p) => ({ ...p, ativo: !p.ativo }))}
              className={`relative w-11 h-6 rounded-full transition-colors ${form.ativo ? 'bg-emerald-500' : 'bg-slate-300'}`}>
              <span className={`absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform ${form.ativo ? 'translate-x-5' : 'translate-x-0'}`} />
            </button>
          </div>
        </form>

        <div className="flex justify-end gap-3 px-6 py-4 border-t border-slate-100 dark:border-slate-700">
          <button onClick={onClose} className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50">Cancelar</button>
          <button onClick={handleSave} disabled={saving}
            className="flex items-center gap-2 px-5 py-2 rounded-xl text-white text-sm font-semibold disabled:opacity-60"
            style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}>
            <Save size={14} />
            {saving ? 'Salvando…' : 'Salvar'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── Dropdown de exportação ───────────────────────────────────────────────────
function ExportDropdown({ token, filtros }) {
  const [open,      setOpen]      = useState(false);
  const [exporting, setExporting] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    function handler(e) { if (ref.current && !ref.current.contains(e.target)) setOpen(false); }
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  async function fetchAll() {
    const p = new URLSearchParams({ pagina: 0, 'tamanho-pagina': 500, ...filtros });
    const res = await fetch(`${API}/usuarios?${p}`, { headers: { Authorization: `Bearer ${token}` } });
    if (!res.ok) throw new Error('Falha ao buscar dados.');
    const data = await res.json();
    return data.content ?? [];
  }

  async function doExport(fn) {
    setOpen(false);
    setExporting(true);
    try {
      const data = await fetchAll();
      if (!data.length) return;
      fn(data);
    } catch {
      // silent
    } finally {
      setExporting(false);
    }
  }

  return (
    <div ref={ref} className="relative">
      <button
        onClick={() => setOpen((v) => !v)}
        disabled={exporting}
        className="flex items-center gap-2 px-4 py-2 rounded-xl border border-slate-200 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-600 dark:text-slate-200 text-sm font-medium hover:bg-slate-50 dark:hover:bg-slate-600 disabled:opacity-50 transition-all"
      >
        <Download size={15} className={exporting ? 'animate-bounce' : ''} />
        {exporting ? 'Exportando…' : 'Exportar'}
        <ChevronDown size={13} className={`transition-transform ${open ? 'rotate-180' : ''}`} />
      </button>

      {open && (
        <div className="absolute right-0 mt-2 w-44 bg-white dark:bg-slate-800 rounded-xl shadow-xl border border-slate-100 dark:border-slate-700 py-1 z-30">
          <button onClick={() => doExport((d) => exportCsvUsuario(d, 'usuarios'))}
            className="w-full flex items-center gap-3 px-4 py-2.5 text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700 text-sm transition-colors">
            <FileText size={14} className="text-slate-400" />CSV
          </button>
          <button onClick={() => doExport((d) => exportXlsxUsuario(d, 'usuarios'))}
            className="w-full flex items-center gap-3 px-4 py-2.5 text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700 text-sm transition-colors">
            <Table size={14} className="text-emerald-500" />Excel (XLSX)
          </button>
        </div>
      )}
    </div>
  );
}

// ─── Gerenciar Usuários (ADMIN / SUPORTE) ─────────────────────────────────────
function GerenciarUsuarios({ token, canManage, showNovo, setShowNovo }) {
  const [empresas,         setEmpresas]         = useState([]);
  const [estabelecimentos, setEstabelecimentos] = useState([]);
  const [filtroEmpresaId,  setFiltroEmpresaId]  = useState('');
  const [filtroEstabId,    setFiltroEstabId]    = useState('');
  const [filtroNome,       setFiltroNome]       = useState('');
  const [usuarios,         setUsuarios]         = useState(null);
  const [page,             setPage]             = useState(0);
  const [loading,          setLoading]          = useState(false);
  const [editTarget,       setEditTarget]       = useState(null);
  const [toast,            setToast]            = useState(null);

  function showToast(msg, type = 'success') {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3000);
  }

  useEffect(() => {
    if (!canManage) return;
    fetch(`${API}/empresas?tamanho-pagina=200&pagina=0`, { headers: { Authorization: `Bearer ${token}` } })
      .then((r) => r.json()).then((d) => setEmpresas(d.content ?? [])).catch(() => {});
  }, [token, canManage]);

  useEffect(() => {
    if (!filtroEmpresaId) { setEstabelecimentos([]); setFiltroEstabId(''); return; }
    fetch(`${API}/estabelecimentos/empresa/${filtroEmpresaId}?tamanho-pagina=100&pagina=0`, { headers: { Authorization: `Bearer ${token}` } })
      .then((r) => r.json()).then((d) => setEstabelecimentos(d.content ?? [])).catch(() => {});
  }, [filtroEmpresaId, token]);

  // Usuários sem permissão de gestão buscam automaticamente ao abrir
  useEffect(() => {
    if (!canManage) buscar(0);
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  async function buscar(pg = 0) {
    setLoading(true);
    try {
      const p = new URLSearchParams({ pagina: pg, 'tamanho-pagina': 10 });
      if (filtroNome)      p.set('nome',              filtroNome);
      if (filtroEmpresaId) p.set('empresa-id',        filtroEmpresaId);
      if (filtroEstabId)   p.set('estabelecimento-id', filtroEstabId);
      const res = await fetch(`${API}/usuarios?${p}`, { headers: { Authorization: `Bearer ${token}` } });
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
    if (!confirm(`Deletar o usuário "${u.nome}"? Essa ação não pode ser desfeita.`)) return;
    try {
      const res = await fetch(`${API}/usuarios/empresa/${u.empresaId}/estabelecimento/${u.estabelecimentoId}/${u.id}`,
        { method: 'DELETE', headers: { Authorization: `Bearer ${token}` } });
      if (!res.ok) throw new Error();
      showToast('Usuário deletado com sucesso.');
      buscar(page);
    } catch {
      showToast('Erro ao deletar usuário.', 'error');
    }
  }

  const items = usuarios?.content ?? [];

  const filtrosAtivos = {
    ...(filtroNome      ? { nome:               filtroNome      } : {}),
    ...(filtroEmpresaId ? { 'empresa-id':        filtroEmpresaId } : {}),
    ...(filtroEstabId   ? { 'estabelecimento-id': filtroEstabId  } : {}),
  };

  return (
    <div className="space-y-5">
      {showNovo && (
        <NovoUsuarioModal token={token} onClose={() => setShowNovo(false)}
          onSaved={() => { setShowNovo(false); showToast('Usuário cadastrado com sucesso!'); buscar(page); }} />
      )}
      {editTarget && (
        <EditUserModal usuario={editTarget} token={token} onClose={() => setEditTarget(null)}
          onSaved={() => { setEditTarget(null); showToast('Usuário atualizado!'); buscar(page); }} />
      )}
      {toast && (
        <div className={`fixed top-5 right-5 z-[100] px-5 py-3 rounded-xl shadow-xl text-white text-sm font-medium ${
          toast.type === 'error' ? 'bg-red-500' : 'bg-emerald-500'
        }`}>{toast.msg}</div>
      )}

      {/* Filtros */}
      <div className="bg-white dark:bg-slate-800 rounded-2xl border border-slate-100 dark:border-slate-700 shadow-sm p-5">
        <h3 className="text-sm font-semibold text-slate-700 dark:text-slate-200 mb-4">Filtros</h3>
        <div className={`grid grid-cols-1 gap-4 ${canManage ? 'sm:grid-cols-2 lg:grid-cols-4' : 'sm:grid-cols-2'}`}>
          {canManage && (
            <>
              <div>
                <label className={labelCls()}>Empresa</label>
                <select value={filtroEmpresaId} onChange={(e) => setFiltroEmpresaId(e.target.value)} className={inputCls()}>
                  <option value="">Todas</option>
                  {empresas.map((e) => <option key={e.id} value={String(e.id)}>{e.nomeFantasia || e.razaoSocial}</option>)}
                </select>
              </div>
              <div>
                <label className={labelCls()}>Estabelecimento</label>
                <select value={filtroEstabId} onChange={(e) => setFiltroEstabId(e.target.value)} disabled={!filtroEmpresaId} className={inputCls()}>
                  <option value="">Todos</option>
                  {estabelecimentos.map((e) => <option key={e.id} value={String(e.id)}>{e.nomeFantasia || e.cnpj || `#${e.id}`}</option>)}
                </select>
              </div>
            </>
          )}
          <div>
            <label className={labelCls()}>Nome</label>
            <input value={filtroNome} onChange={(e) => setFiltroNome(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && buscar(0)}
              className={inputCls()} placeholder="Filtrar por nome…" />
          </div>
          <div className="flex items-end">
            <button onClick={() => buscar(0)} disabled={loading}
              className="w-full flex items-center justify-center gap-2 px-4 py-2 rounded-xl text-white text-sm font-semibold disabled:opacity-50 hover:scale-[1.02] transition-all"
              style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}>
              <Search size={14} />
              {loading ? 'Buscando…' : 'Pesquisar'}
            </button>
          </div>
        </div>
      </div>

      {/* Tabela */}
      {usuarios && (
        <div className="bg-white dark:bg-slate-800 rounded-2xl border border-slate-100 dark:border-slate-700 shadow-sm overflow-hidden">
          <div className="px-6 py-4 border-b border-slate-100 dark:border-slate-700 flex items-center justify-between gap-3">
            <h3 className="font-bold text-slate-800 dark:text-slate-100 text-sm">
              Resultado
              <span className="ml-2 text-xs font-normal text-slate-400">{usuarios.totalElements} usuário(s)</span>
            </h3>
            <ExportDropdown token={token} filtros={filtrosAtivos} />
          </div>

          {items.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-14 text-slate-400">
              <User size={32} className="mb-2 opacity-30" />
              <p className="text-sm">Nenhum usuário encontrado.</p>
            </div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="bg-slate-50 dark:bg-slate-700 border-b border-slate-100 dark:border-slate-600">
                      {['Nome', 'E-mail', 'Perfil', 'Status', ...(canManage ? ['Ações'] : [])].map((h) => (
                        <th key={h} className="px-5 py-3.5 text-left text-xs font-semibold text-slate-500 dark:text-slate-300">{h}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100 dark:divide-slate-700">
                    {items.map((u) => (
                      <tr key={u.id} className="hover:bg-slate-50 dark:hover:bg-slate-700/50 transition-colors">
                        <td className="px-5 py-4">
                          <div className="flex items-center gap-2.5">
                            <div className="w-7 h-7 rounded-full flex items-center justify-center text-white text-xs font-bold flex-shrink-0"
                              style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}>
                              {(u.nome ?? u.username ?? '?').charAt(0).toUpperCase()}
                            </div>
                            <div>
                              <p className="text-xs font-medium text-slate-700 dark:text-slate-200">{u.nome ?? '—'}</p>
                              <p className="text-xs text-slate-400">@{u.username ?? '—'}</p>
                            </div>
                          </div>
                        </td>
                        <td className="px-5 py-4 text-xs text-slate-500 dark:text-slate-400">{u.email ?? '—'}</td>
                        <td className="px-5 py-4 text-xs text-slate-500 dark:text-slate-400">
                          {u.roles?.[0]?.replace('ROLE_', '') ?? '—'}
                        </td>
                        <td className="px-5 py-4">
                          <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${u.ativo ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-500'}`}>
                            {u.ativo ? 'Ativo' : 'Inativo'}
                          </span>
                        </td>
                        {canManage && (
                          <td className="px-5 py-4">
                            <div className="flex items-center gap-1">
                              <button onClick={() => setEditTarget(u)}
                                className="p-1.5 rounded-lg text-slate-400 hover:text-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/30 transition-colors" title="Editar">
                                <Edit2 size={14} />
                              </button>
                              <button onClick={() => deletar(u)}
                                className="p-1.5 rounded-lg text-slate-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/30 transition-colors" title="Deletar">
                                <Trash2 size={14} />
                              </button>
                            </div>
                          </td>
                        )}
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
  const canManage = user?.hasRole('ADMINISTRADOR', 'SUPORTE');
  const [tab,      setTab]      = useState('usuarios');
  const [showNovo, setShowNovo] = useState(false);

  const tabs = [
    { id: 'usuarios', icon: UserCog,    label: canManage ? 'Gerenciar Usuários' : 'Usuários' },
    { id: 'perfil',   icon: UserCircle, label: 'Meu Perfil' },
  ];

  const titles = {
    usuarios: {
      title: canManage ? 'Gerenciar Usuários' : 'Usuários',
      sub:   canManage ? 'Consulte, cadastre, edite e gerencie os usuários do sistema.' : 'Consulte os usuários da sua empresa.',
    },
    perfil: { title: 'Meu Perfil', sub: 'Visualize e atualize seus dados de acesso.' },
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-800 dark:text-slate-100 mb-1">{titles[tab].title}</h1>
          <p className="text-slate-500 dark:text-slate-400 text-sm">{titles[tab].sub}</p>
        </div>
        {canManage && tab === 'usuarios' && (
          <button
            onClick={() => setShowNovo(true)}
            className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-white text-sm font-semibold hover:scale-[1.02] transition-all flex-shrink-0"
            style={{ background: '#1D4ED8' }}
          >
            <Plus size={16} />
            Novo usuário
          </button>
        )}
      </div>

      <div className="flex gap-1 bg-slate-100 dark:bg-slate-700 p-1 rounded-xl w-fit">
        {tabs.map((t) => (
          <button key={t.id} onClick={() => setTab(t.id)}
            className={`flex items-center gap-2 px-5 py-2 rounded-lg text-sm font-medium transition-all ${
              tab === t.id
                ? 'bg-white dark:bg-slate-600 text-slate-800 dark:text-slate-100 shadow-sm'
                : 'text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200'
            }`}>
            <t.icon size={15} />
            {t.label}
          </button>
        ))}
      </div>

      {tab === 'perfil'   && <MeuPerfil token={token} />}
      {tab === 'usuarios' && <GerenciarUsuarios token={token} canManage={canManage} showNovo={showNovo} setShowNovo={setShowNovo} />}
    </div>
  );
}
