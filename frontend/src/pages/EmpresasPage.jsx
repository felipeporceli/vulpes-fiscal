import { useState, useEffect, useRef } from 'react';
import { Search, Pencil, Trash2, X, ChevronLeft, ChevronRight, AlertTriangle, ChevronUp, ChevronDown, ChevronsUpDown, Plus, Download, FileText, FileSpreadsheet } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { exportCsv, exportXlsx, EXPORT_MAX_ROWS } from '../utils/exportUtils';

const API = import.meta.env.VITE_API_URL ?? '';

const REGIME_OPTIONS = [
  { value: '', label: 'Todos' },
  { value: 'SIMPLES_NACIONAL',          label: 'Simples Nacional'            },
  { value: 'SIMPLES_EXCESSO_SUBLIMITE', label: 'Simples (Excesso Sublimite)' },
  { value: 'REGIME_NORMAL',             label: 'Regime Normal'               },
];

const PORTE_OPTIONS = [
  { value: '', label: 'Todos' },
  { value: 'ME',             label: 'ME - Microempresa' },
  { value: 'EPP',            label: 'EPP'               },
  { value: 'MEDIO_PORTE',    label: 'Médio Porte'       },
  { value: 'GRANDE_EMPRESA', label: 'Grande Empresa'    },
];

const STATUS_OPTIONS = [
  { value: '', label: 'Todos' },
  { value: 'ATIVA',   label: 'Ativa'   },
  { value: 'INATIVA', label: 'Inativa' },
  { value: 'INAPTA',  label: 'Inapta'  },
  { value: 'BAIXADA', label: 'Baixada' },
];

const AMBIENTE_OPTIONS = [
  { value: 'PRODUCAO',    label: 'Produção'    },
  { value: 'HOMOLOGACAO', label: 'Homologação' },
];

const statusColor = {
  ATIVA:   'bg-emerald-100 text-emerald-700',
  INATIVA: 'bg-red-100     text-red-600',
  INAPTA:  'bg-amber-100   text-amber-700',
  BAIXADA: 'bg-slate-100   text-slate-500',
};

function formatCnpj(v) {
  if (!v) return '—';
  v = v.replace(/\D/g, '');
  return v.length === 14
    ? v.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5')
    : v;
}

function maskCnpj(raw) {
  let v = raw.replace(/\D/g, '').slice(0, 14);
  if (v.length > 12) v = v.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{0,2})/, '$1.$2.$3/$4-$5');
  else if (v.length > 8) v = v.replace(/(\d{2})(\d{3})(\d{3})(\d{0,4})/, '$1.$2.$3/$4');
  else if (v.length > 5) v = v.replace(/(\d{2})(\d{3})(\d{0,3})/, '$1.$2.$3');
  else if (v.length > 2) v = v.replace(/(\d{2})(\d{0,3})/, '$1.$2');
  return v;
}

function maskData(raw) {
  let v = raw.replace(/\D/g, '').slice(0, 8);
  if (v.length > 4) v = v.replace(/(\d{2})(\d{2})(\d{0,4})/, '$1/$2/$3');
  else if (v.length > 2) v = v.replace(/(\d{2})(\d{0,2})/, '$1/$2');
  return v;
}

function isValidDate(str) {
  if (!/^\d{2}\/\d{2}\/\d{4}$/.test(str)) return false;
  const [d, m, y] = str.split('/').map(Number);
  const dt = new Date(y, m - 1, d);
  return dt.getFullYear() === y && dt.getMonth() === m - 1 && dt.getDate() === d;
}

function isValidCnpj(raw) {
  const n = raw.replace(/\D/g, '');
  if (n.length !== 14) return false;
  if (/^(\d)\1+$/.test(n)) return false; // todos os dígitos iguais

  const calcDigit = (base) => {
    let sum = 0;
    let weight = base.length - 7;
    for (let i = 0; i < base.length; i++) {
      sum += parseInt(base[i]) * weight--;
      if (weight < 2) weight = 9;
    }
    const rem = sum % 11;
    return rem < 2 ? 0 : 11 - rem;
  };

  const d1 = calcDigit(n.slice(0, 12));
  if (d1 !== parseInt(n[12])) return false;
  const d2 = calcDigit(n.slice(0, 13));
  return d2 === parseInt(n[13]);
}

// ─── Ícone de ordenação ───────────────────────────────────────────────────────
function SortIcon({ col, sortCol, sortDir }) {
  if (sortCol !== col) return <ChevronsUpDown size={12} className="ml-1 opacity-30 inline-block" />;
  return sortDir === 'asc'
    ? <ChevronUp   size={12} className="ml-1 text-vulpes-orange inline-block" />
    : <ChevronDown size={12} className="ml-1 text-vulpes-orange inline-block" />;
}

function inputCls(extra = '') {
  return `w-full border border-slate-200 rounded-xl px-3 py-2 text-sm text-slate-700 focus:outline-none focus:border-vulpes-orange focus:ring-2 focus:ring-vulpes-orange/20 transition-all bg-white ${extra}`;
}
function labelCls() {
  return 'block text-xs font-medium text-slate-500 mb-1';
}

// ─── Modal de cadastro ────────────────────────────────────────────────────────
function CreateModal({ token, onClose, onSaved }) {
  const EMPTY = {
    razaoSocial:       '',
    nomeFantasia:      '',
    cnpj:              '',
    inscricaoEstadual: '',
    cnae:              '',
    uf:                '',
    regimeTributario:  '',
    status:            'ATIVA',
    porte:             '',
    ambienteSefaz:     'PRODUCAO',
    dataAbertura:      '',
  };
  const [form,    setForm]    = useState(EMPTY);
  const [loading, setLoading] = useState(false);
  const [errors,  setErrors]  = useState({});
  const [apiError, setApiError] = useState('');

  const set = (k) => (e) => setForm((p) => ({ ...p, [k]: e.target.value }));

  function validate() {
    const e = {};
    if (!form.razaoSocial.trim())
      e.razaoSocial = 'Razão Social é obrigatória';
    if (!form.cnpj.trim())
      e.cnpj = 'CNPJ é obrigatório';
    else if (form.cnpj.replace(/\D/g, '').length !== 14)
      e.cnpj = 'CNPJ incompleto — informe os 14 dígitos';
    else if (!isValidCnpj(form.cnpj))
      e.cnpj = 'CNPJ inválido — verifique os dígitos verificadores';
    if (!form.inscricaoEstadual.trim())
      e.inscricaoEstadual = 'Inscrição Estadual é obrigatória';
    if (!form.cnae.trim())
      e.cnae = 'CNAE é obrigatório';
    if (!form.uf.trim())
      e.uf = 'UF é obrigatória';
    else if (form.uf.trim().length !== 2)
      e.uf = 'UF deve ter exatamente 2 letras';
    if (!form.regimeTributario)
      e.regimeTributario = 'Selecione o Regime Tributário';
    if (!form.status)
      e.status = 'Selecione o Status';
    if (!form.porte)
      e.porte = 'Selecione o Porte da empresa';
    if (!form.ambienteSefaz)
      e.ambienteSefaz = 'Selecione o Ambiente SEFAZ';
    if (!form.dataAbertura.trim())
      e.dataAbertura = 'Data de Abertura é obrigatória';
    else if (!isValidDate(form.dataAbertura))
      e.dataAbertura = 'Data inválida — use o formato dd/MM/yyyy';
    else {
      const [d, m, y] = form.dataAbertura.split('/').map(Number);
      if (new Date(y, m - 1, d) >= new Date())
        e.dataAbertura = 'A data de abertura deve ser no passado';
    }
    return e;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setApiError('');
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }

    setLoading(true);
    try {
      const res = await fetch(`${API}/empresas`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          ...form,
          cnpj: form.cnpj.replace(/\D/g, ''),
        }),
      });
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.mensagem || `Erro ${res.status}`);
      }
      onSaved();
    } catch (err) {
      setApiError(err.message);
    } finally {
      setLoading(false);
    }
  }

  const field = (k, label, required, children) => (
    <div>
      <label className={labelCls()}>
        {label}{required && <span className="text-red-400 ml-0.5">*</span>}
      </label>
      {children}
      {errors[k] && <p className="mt-1 text-red-500 text-xs">{errors[k]}</p>}
    </div>
  );

  const errCls = (k) => errors[k]
    ? 'w-full border border-red-400 rounded-xl px-3 py-2 text-sm text-slate-700 focus:outline-none focus:border-red-400 focus:ring-2 focus:ring-red-200 transition-all bg-white'
    : inputCls();

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col">

        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <div>
            <h2 className="font-bold text-slate-800">Adicionar nova empresa</h2>
            <p className="text-xs text-slate-400 mt-0.5">Campos marcados com <span className="text-red-400">*</span> são obrigatórios</p>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700 transition-colors">
            <X size={20} />
          </button>
        </div>

        {/* Body */}
        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6">
          {apiError && (
            <div className="mb-4 flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl px-4 py-3">
              <AlertTriangle size={14} className="mt-0.5 flex-shrink-0" />
              {apiError}
            </div>
          )}
          {Object.keys(errors).length > 0 && (
            <div className="mb-4 bg-amber-50 border border-amber-200 rounded-xl px-4 py-3">
              <p className="text-amber-700 text-xs font-semibold mb-1 flex items-center gap-1.5">
                <AlertTriangle size={13} /> Corrija os campos destacados abaixo:
              </p>
              <ul className="list-disc list-inside space-y-0.5">
                {Object.values(errors).map((msg, i) => (
                  <li key={i} className="text-amber-700 text-xs">{msg}</li>
                ))}
              </ul>
            </div>
          )}

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {field('razaoSocial', 'Razão Social', true,
              <input value={form.razaoSocial} onChange={set('razaoSocial')} className={errCls('razaoSocial')} placeholder="Razão Social Ltda." />
            )}
            {field('nomeFantasia', 'Nome Fantasia', false,
              <input value={form.nomeFantasia} onChange={set('nomeFantasia')} className={inputCls()} placeholder="Nome fantasia (opcional)" />
            )}
            {field('cnpj', 'CNPJ', true,
              <input
                value={form.cnpj}
                onChange={(e) => setForm(p => ({ ...p, cnpj: maskCnpj(e.target.value) }))}
                className={errCls('cnpj')}
                placeholder="00.000.000/0000-00"
              />
            )}
            {field('inscricaoEstadual', 'Inscrição Estadual', true,
              <input value={form.inscricaoEstadual} onChange={set('inscricaoEstadual')} className={errCls('inscricaoEstadual')} />
            )}
            {field('cnae', 'CNAE', true,
              <input value={form.cnae} onChange={set('cnae')} className={errCls('cnae')} placeholder="Ex: 4711-3/01" />
            )}
            {field('uf', 'UF', true,
              <input maxLength={2} value={form.uf} onChange={(e) => { set('uf')(e); }} className={errCls('uf')} placeholder="SP" style={{ textTransform: 'uppercase' }}
                onInput={(e) => e.target.value = e.target.value.toUpperCase()} />
            )}
            {field('regimeTributario', 'Regime Tributário', true,
              <select value={form.regimeTributario} onChange={set('regimeTributario')} className={errCls('regimeTributario')}>
                <option value="">Selecione...</option>
                {REGIME_OPTIONS.filter(o => o.value).map(o => (
                  <option key={o.value} value={o.value}>{o.label}</option>
                ))}
              </select>
            )}
            {field('porte', 'Porte', true,
              <select value={form.porte} onChange={set('porte')} className={errCls('porte')}>
                <option value="">Selecione...</option>
                {PORTE_OPTIONS.filter(o => o.value).map(o => (
                  <option key={o.value} value={o.value}>{o.label}</option>
                ))}
              </select>
            )}
            {field('ambienteSefaz', 'Ambiente SEFAZ', true,
              <select value={form.ambienteSefaz} onChange={set('ambienteSefaz')} className={errCls('ambienteSefaz')}>
                {AMBIENTE_OPTIONS.map(o => (
                  <option key={o.value} value={o.value}>{o.label}</option>
                ))}
              </select>
            )}
            {field('status', 'Status', true,
              <select value={form.status} onChange={set('status')} className={errCls('status')}>
                {STATUS_OPTIONS.filter(o => o.value).map(o => (
                  <option key={o.value} value={o.value}>{o.label}</option>
                ))}
              </select>
            )}
            <div className="sm:col-span-2">
              {field('dataAbertura', 'Data de Abertura', true,
                <input
                  value={form.dataAbertura}
                  onChange={(e) => setForm(p => ({ ...p, dataAbertura: maskData(e.target.value) }))}
                  className={errCls('dataAbertura')}
                  placeholder="dd/MM/yyyy"
                  maxLength={10}
                />
              )}
            </div>
          </div>
        </form>

        {/* Footer */}
        <div className="flex items-center justify-end gap-3 px-6 py-4 border-t border-slate-100">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50 transition-colors"
          >
            Cancelar
          </button>
          <button
            onClick={handleSubmit}
            disabled={loading}
            className="flex items-center gap-2 px-5 py-2 rounded-xl text-white text-sm font-semibold disabled:opacity-60 transition-all hover:scale-[1.02]"
            style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}
          >
            <Plus size={15} />
            {loading ? 'Cadastrando…' : 'Cadastrar empresa'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── Modal de edição ─────────────────────────────────────────────────────────
function EditModal({ empresa, empresaId, token, onClose, onSaved }) {
  const [form, setForm] = useState({
    razaoSocial:        empresa.razaoSocial        ?? '',
    nomeFantasia:       empresa.nomeFantasia        ?? '',
    inscricaoEstadual:  empresa.inscricaoEstadual   ?? '',
    regimeTributario:   empresa.regimeTributario    ?? '',
    porte:              empresa.porte               ?? '',
    ambienteSefaz:      empresa.ambienteSefaz       ?? 'PRODUCAO',
    status:             empresa.status              ?? 'ATIVA',
    dataAbertura:       empresa.dataAbertura        ?? '',
    cnae:               empresa.cnae                ?? '',
    uf:                 empresa.uf                  ?? '',
  });
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState('');

  const set = (k) => (e) => setForm((p) => ({ ...p, [k]: e.target.value }));

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await fetch(`${API}/empresas/${empresaId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          ...form,
          dataAbertura: form.dataAbertura || null,
        }),
      });
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.mensagem || `Erro ${res.status}`);
      }
      onSaved();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <h2 className="font-bold text-slate-800">Editar Empresa</h2>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700 transition-colors">
            <X size={20} />
          </button>
        </div>

        {/* Body */}
        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6">
          {error && (
            <div className="mb-4 flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl px-4 py-3">
              <AlertTriangle size={14} className="mt-0.5 flex-shrink-0" />
              {error}
            </div>
          )}

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className={labelCls()}>Razão Social</label>
              <input value={form.razaoSocial} onChange={set('razaoSocial')} className={inputCls()} />
            </div>
            <div>
              <label className={labelCls()}>Nome Fantasia</label>
              <input value={form.nomeFantasia} onChange={set('nomeFantasia')} className={inputCls()} />
            </div>
            <div>
              <label className={labelCls()}>Inscrição Estadual</label>
              <input value={form.inscricaoEstadual} onChange={set('inscricaoEstadual')} className={inputCls()} />
            </div>
            <div>
              <label className={labelCls()}>CNAE</label>
              <input value={form.cnae} onChange={set('cnae')} className={inputCls()} />
            </div>
            <div>
              <label className={labelCls()}>UF</label>
              <input maxLength={2} value={form.uf} onChange={set('uf')} className={inputCls()} placeholder="SP" />
            </div>
            <div>
              <label className={labelCls()}>Data de Abertura</label>
              <input
                value={form.dataAbertura}
                onChange={set('dataAbertura')}
                className={inputCls()}
                placeholder="dd/MM/yyyy"
              />
            </div>
            <div>
              <label className={labelCls()}>Regime Tributário</label>
              <select value={form.regimeTributario} onChange={set('regimeTributario')} className={inputCls()}>
                {REGIME_OPTIONS.filter(o => o.value).map(o => (
                  <option key={o.value} value={o.value}>{o.label}</option>
                ))}
              </select>
            </div>
            <div>
              <label className={labelCls()}>Porte</label>
              <select value={form.porte} onChange={set('porte')} className={inputCls()}>
                {PORTE_OPTIONS.filter(o => o.value).map(o => (
                  <option key={o.value} value={o.value}>{o.label}</option>
                ))}
              </select>
            </div>
            <div>
              <label className={labelCls()}>Ambiente SEFAZ</label>
              <select value={form.ambienteSefaz} onChange={set('ambienteSefaz')} className={inputCls()}>
                {AMBIENTE_OPTIONS.map(o => (
                  <option key={o.value} value={o.value}>{o.label}</option>
                ))}
              </select>
            </div>
            <div>
              <label className={labelCls()}>Status</label>
              <select value={form.status} onChange={set('status')} className={inputCls()}>
                {STATUS_OPTIONS.filter(o => o.value).map(o => (
                  <option key={o.value} value={o.value}>{o.label}</option>
                ))}
              </select>
            </div>
          </div>
        </form>

        {/* Footer */}
        <div className="flex items-center justify-end gap-3 px-6 py-4 border-t border-slate-100">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50 transition-colors"
          >
            Cancelar
          </button>
          <button
            onClick={handleSubmit}
            disabled={loading}
            className="px-5 py-2 rounded-xl text-white text-sm font-semibold disabled:opacity-60 transition-all hover:scale-[1.02]"
            style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}
          >
            {loading ? 'Salvando…' : 'Salvar alterações'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── Modal de confirmação de exclusão ─────────────────────────────────────────
function DeleteModal({ empresaId, razaoSocial, token, onClose, onDeleted }) {
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState('');

  async function handleDelete() {
    setLoading(true);
    setError('');
    try {
      const res = await fetch(`${API}/empresas/${empresaId}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.mensagem || `Erro ${res.status}`);
      }
      onDeleted();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6">
        <div className="flex items-center gap-3 mb-4">
          <div className="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center flex-shrink-0">
            <AlertTriangle size={20} className="text-red-500" />
          </div>
          <div>
            <h2 className="font-bold text-slate-800 text-sm">Excluir empresa</h2>
            <p className="text-slate-500 text-xs">Esta ação não pode ser desfeita.</p>
          </div>
        </div>

        <p className="text-slate-600 text-sm mb-4">
          Tem certeza que deseja excluir <strong>{razaoSocial || `empresa #${empresaId}`}</strong>?
        </p>

        {error && (
          <div className="mb-4 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl px-4 py-3">
            {error}
          </div>
        )}

        <div className="flex gap-3 justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50 transition-colors"
          >
            Cancelar
          </button>
          <button
            onClick={handleDelete}
            disabled={loading}
            className="px-5 py-2 rounded-xl bg-red-500 hover:bg-red-600 text-white text-sm font-semibold disabled:opacity-60 transition-colors"
          >
            {loading ? 'Excluindo…' : 'Confirmar exclusão'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── EmpresasPage ────────────────────────────────────────────────────────────
export default function EmpresasPage() {
  const { token, user } = useAuth();

  const isRestrito = user?.hasRole('EMPRESARIO', 'GERENTE') && !user?.hasRole('ADMINISTRADOR', 'SUPORTE');

  const [filters, setFilters] = useState({
    empresaId:          isRestrito ? String(user?.empresaId ?? '') : '',
    cnpj:               '',
    razaoSocial:        '',
    inscricaoEstadual:  '',
    regimeTributario:   '',
    status:             '',
    porte:              '',
  });

  const [result,      setResult]      = useState(null);
  const [page,        setPage]        = useState(0);
  const [sortCol,     setSortCol]     = useState(null);   // ex: 'razaoSocial'
  const [sortDir,     setSortDir]     = useState('asc');
  const [loading,     setLoading]     = useState(false);
  const [error,       setError]       = useState('');

  const [showCreate,    setShowCreate]    = useState(false);
  const [editTarget,    setEditTarget]    = useState(null);
  const [deleteTarget,  setDeleteTarget]  = useState(null);
  const [toast,         setToast]         = useState(null);
  const [exportOpen,    setExportOpen]    = useState(false);
  const [exporting,     setExporting]     = useState(false);
  const exportRef = useRef(null);

  const setF = (k) => (e) => setFilters((p) => ({ ...p, [k]: e.target.value }));

  function showToast(msg, type = 'success') {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3500);
  }

  function toggleSort(col) {
    const nextDir = sortCol === col && sortDir === 'asc' ? 'desc' : 'asc';
    setSortCol(col);
    setSortDir(nextDir);
    buscarComSort(0, col, nextDir);
  }

  async function buscarComSort(pg = 0, col = sortCol, dir = sortDir) {
    setError('');
    setLoading(true);
    try {
      const params = new URLSearchParams({ pagina: pg, 'tamanho-pagina': 10 });
      if (filters.empresaId)         params.set('empresa-id',        filters.empresaId);
      if (filters.cnpj)              params.set('cnpj',               filters.cnpj.replace(/\D/g, ''));
      if (filters.razaoSocial)       params.set('razao-social',       filters.razaoSocial);
      if (filters.inscricaoEstadual) params.set('inscricao-estadual', filters.inscricaoEstadual);
      if (filters.regimeTributario)  params.set('regime-tributario',  filters.regimeTributario);
      if (filters.status)            params.set('status',              filters.status);
      if (filters.porte)             params.set('porte',               filters.porte);
      if (col)                       { params.set('ordenar-por', col); params.set('direcao', dir); }

      const res = await fetch(`${API}/empresas?${params}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.mensagem || `Erro ${res.status}`);
      }

      const data = await res.json();
      setResult(data);
      setPage(pg);
    } catch (err) {
      setError(err.message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  }

  const buscar = (pg = 0) => buscarComSort(pg);

  // Para EMPRESARIO/GERENTE: auto-busca ao montar o componente
  useEffect(() => {
    if (isRestrito) buscarComSort(0);
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  // Fecha o dropdown de export ao clicar fora
  useEffect(() => {
    function onClickOutside(e) {
      if (exportRef.current && !exportRef.current.contains(e.target)) {
        setExportOpen(false);
      }
    }
    document.addEventListener('mousedown', onClickOutside);
    return () => document.removeEventListener('mousedown', onClickOutside);
  }, []);

  async function handleExport(format) {
    setExportOpen(false);
    setExporting(true);
    try {
      const total = result?.totalElements ?? 0;
      if (total === 0) { showToast('Nenhum dado para exportar.', 'error'); return; }

      // O backend limita cada request a 100 registros (cap de segurança).
      // Paginamos sequencialmente para coletar todos os registros do filtro atual
      // sem nunca pedir mais do que o backend permite por chamada.
      const PAGE_SIZE   = 100;
      const totalToFetch = Math.min(total, EXPORT_MAX_ROWS);
      const totalPages  = Math.ceil(totalToFetch / PAGE_SIZE);
      const allRows     = [];

      for (let pg = 0; pg < totalPages; pg++) {
        const params = new URLSearchParams({ pagina: pg, 'tamanho-pagina': PAGE_SIZE });
        if (filters.empresaId)         params.set('empresa-id',        filters.empresaId);
        if (filters.cnpj)              params.set('cnpj',               filters.cnpj.replace(/\D/g, ''));
        if (filters.razaoSocial)       params.set('razao-social',       filters.razaoSocial);
        if (filters.inscricaoEstadual) params.set('inscricao-estadual', filters.inscricaoEstadual);
        if (filters.regimeTributario)  params.set('regime-tributario',  filters.regimeTributario);
        if (filters.status)            params.set('status',              filters.status);
        if (filters.porte)             params.set('porte',               filters.porte);
        if (sortCol)                   { params.set('ordenar-por', sortCol); params.set('direcao', sortDir); }

        const res = await fetch(`${API}/empresas?${params}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!res.ok) throw new Error(`Erro ao buscar dados (página ${pg + 1}): ${res.status}`);

        const data = await res.json();
        allRows.push(...(data.content ?? []));

        // Para se a última página retornou menos que PAGE_SIZE (fim dos dados)
        if ((data.content?.length ?? 0) < PAGE_SIZE) break;
      }

      if (allRows.length === 0) {
        showToast('Nenhum dado retornado para exportar.', 'error');
        return;
      }

      if (format === 'csv') {
        exportCsv(allRows, 'empresas');
        showToast(`CSV exportado — ${allRows.length} registro(s)`);
      } else {
        exportXlsx(allRows, 'empresas');
        showToast(`XLSX exportado — ${allRows.length} registro(s)`);
      }
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setExporting(false);
    }
  }

  const items = result?.content ?? [];

  return (
    <div className="space-y-6">
      {/* Toast */}
      {toast && (
        <div
          className={`fixed top-5 right-5 z-[100] flex items-center gap-3 px-5 py-3 rounded-xl shadow-xl text-white text-sm font-medium transition-all ${
            toast.type === 'error' ? 'bg-red-500' : 'bg-emerald-500'
          }`}
        >
          {toast.msg}
        </div>
      )}

      {/* Create Modal */}
      {showCreate && (
        <CreateModal
          token={token}
          onClose={() => setShowCreate(false)}
          onSaved={() => {
            setShowCreate(false);
            showToast('Empresa cadastrada com sucesso!');
            buscar(page);
          }}
        />
      )}

      {/* Edit Modal */}
      {editTarget && (
        <EditModal
          empresa={editTarget.empresa}
          empresaId={editTarget.empresaId}
          token={token}
          onClose={() => setEditTarget(null)}
          onSaved={() => {
            setEditTarget(null);
            showToast('Empresa atualizada com sucesso!');
            buscar(page);
          }}
        />
      )}

      {/* Delete Modal */}
      {deleteTarget && (
        <DeleteModal
          empresaId={deleteTarget.empresaId}
          razaoSocial={deleteTarget.razaoSocial}
          token={token}
          onClose={() => setDeleteTarget(null)}
          onDeleted={() => {
            setDeleteTarget(null);
            showToast('Empresa excluída com sucesso!');
            buscar(page);
          }}
        />
      )}

      {/* Cabeçalho */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-800 mb-1">Empresas</h1>
          <p className="text-slate-500 text-sm">Consulte, edite ou exclua empresas cadastradas no sistema.</p>
        </div>
        {user?.hasRole('ADMINISTRADOR', 'SUPORTE') && (
          <button
            onClick={() => setShowCreate(true)}
            className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-white text-sm font-semibold hover:scale-[1.02] transition-all flex-shrink-0"
            style={{ background: '#1D4ED8' }}
          >
            <Plus size={16} />
            Adicionar nova empresa
          </button>
        )}
      </div>

      {/* Filtros */}
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
        <h2 className="font-semibold text-slate-700 text-sm mb-4">Filtros de pesquisa</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* ID da Empresa — oculto para usuários restritos */}
          {!isRestrito && (
            <div>
              <label className={labelCls()}>ID da Empresa</label>
              <input
                type="number"
                value={filters.empresaId}
                onChange={setF('empresaId')}
                className={inputCls()}
                placeholder="ex: 1"
              />
            </div>
          )}
          <div>
            <label className={labelCls()}>CNPJ</label>
            <input
              value={filters.cnpj}
              onChange={(e) => setFilters(p => ({ ...p, cnpj: maskCnpj(e.target.value) }))}
              className={inputCls()}
              placeholder="00.000.000/0000-00"
            />
          </div>
          <div>
            <label className={labelCls()}>Razão Social</label>
            <input value={filters.razaoSocial} onChange={setF('razaoSocial')} className={inputCls()} placeholder="Nome da empresa" />
          </div>
          <div>
            <label className={labelCls()}>Inscrição Estadual</label>
            <input value={filters.inscricaoEstadual} onChange={setF('inscricaoEstadual')} className={inputCls()} />
          </div>
          <div>
            <label className={labelCls()}>Regime Tributário</label>
            <select value={filters.regimeTributario} onChange={setF('regimeTributario')} className={inputCls()}>
              {REGIME_OPTIONS.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
            </select>
          </div>
          <div>
            <label className={labelCls()}>Status</label>
            <select value={filters.status} onChange={setF('status')} className={inputCls()}>
              {STATUS_OPTIONS.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
            </select>
          </div>
          <div>
            <label className={labelCls()}>Porte</label>
            <select value={filters.porte} onChange={setF('porte')} className={inputCls()}>
              {PORTE_OPTIONS.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
            </select>
          </div>
        </div>

        {error && (
          <p className="mt-3 text-red-500 text-xs flex items-center gap-1">
            <AlertTriangle size={12} /> {error}
          </p>
        )}

        <div className="flex justify-end mt-5">
          <button
            onClick={() => buscar(0)}
            disabled={loading}
            className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-white text-sm font-semibold disabled:opacity-60 hover:scale-[1.02] transition-all"
            style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}
          >
            <Search size={15} />
            {loading ? 'Buscando…' : 'Pesquisar'}
          </button>
        </div>
      </div>

      {/* Tabela de resultados */}
      {result && (
        <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
          <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
            <h2 className="font-bold text-slate-800 text-sm">
              Resultados
              <span className="ml-2 text-xs font-normal text-slate-400">
                {result.totalElements} empresa(s) encontrada(s)
              </span>
            </h2>

            {/* Botão exportar — visível apenas para ADMINISTRADOR e SUPORTE */}
            {user?.hasRole('ADMINISTRADOR', 'SUPORTE') && (
            <div className="relative" ref={exportRef}>
              <button
                onClick={() => setExportOpen(v => !v)}
                disabled={exporting || items.length === 0}
                className="flex items-center gap-2 px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-xs font-medium hover:bg-slate-50 disabled:opacity-40 transition-colors"
              >
                <Download size={14} />
                {exporting ? 'Exportando…' : 'Exportar'}
              </button>

              {exportOpen && (
                <div className="absolute right-0 mt-1 w-48 bg-white rounded-xl border border-slate-100 shadow-lg z-20 overflow-hidden">
                  <button
                    onClick={() => handleExport('csv')}
                    className="w-full flex items-center gap-3 px-4 py-3 text-xs text-slate-700 hover:bg-slate-50 transition-colors"
                  >
                    <FileText size={14} className="text-emerald-500" />
                    Exportar como CSV
                  </button>
                  <button
                    onClick={() => handleExport('xlsx')}
                    className="w-full flex items-center gap-3 px-4 py-3 text-xs text-slate-700 hover:bg-slate-50 transition-colors border-t border-slate-50"
                  >
                    <FileSpreadsheet size={14} className="text-blue-500" />
                    Exportar como XLSX
                  </button>
                </div>
              )}
            </div>
            )}
          </div>

          {items.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-16 text-slate-400">
              <Search size={36} className="mb-3 opacity-40" />
              <p className="text-sm">Nenhuma empresa encontrada com esses filtros.</p>
            </div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="bg-slate-50 border-b border-slate-100">
                      {[
                        { label: 'Razão Social',  col: 'razaoSocial',       px: 'px-6' },
                        { label: 'Nome Fantasia', col: 'nomeFantasia',      px: 'px-4' },
                        { label: 'CNPJ',          col: 'cnpj',              px: 'px-4' },
                        { label: 'Regime',        col: 'regimeTributario',  px: 'px-4' },
                        { label: 'Porte',         col: 'porte',             px: 'px-4' },
                        { label: 'UF',            col: 'uf',                px: 'px-4' },
                        { label: 'Status',        col: 'status',            px: 'px-4' },
                      ].map(({ label, col, px }) => (
                        <th
                          key={col}
                          className={`text-left ${px} py-3.5 text-xs font-semibold text-slate-500 cursor-pointer select-none hover:text-vulpes-orange transition-colors whitespace-nowrap`}
                          onClick={() => toggleSort(col)}
                        >
                          {label}
                          <SortIcon col={col} sortCol={sortCol} sortDir={sortDir} />
                        </th>
                      ))}
                      <th className="px-4 py-3.5 text-xs font-semibold text-slate-500 text-right">Ações</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {items.map((emp) => (
                      <tr key={emp.id} className="hover:bg-slate-50 transition-colors">
                        <td className="px-6 py-4 font-semibold text-slate-800 text-xs">{emp.razaoSocial || '—'}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs">{emp.nomeFantasia || '—'}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs font-mono">{formatCnpj(emp.cnpj)}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs">{emp.regimeTributario || '—'}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs">{emp.porte || '—'}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs">{emp.uf || '—'}</td>
                        <td className="px-4 py-4">
                          <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${statusColor[emp.status] || 'bg-slate-100 text-slate-500'}`}>
                            {emp.status || '—'}
                          </span>
                        </td>
                        <td className="px-4 py-4 text-right">
                          <div className="flex items-center justify-end gap-2">
                            <button
                              onClick={() => setEditTarget({ empresa: emp, empresaId: emp.id })}
                              className="p-1.5 rounded-lg text-slate-400 hover:text-vulpes-orange hover:bg-vulpes-orange/10 transition-colors"
                              title="Editar"
                            >
                              <Pencil size={15} />
                            </button>
                            {!isRestrito && (
                              <button
                                onClick={() => setDeleteTarget({ empresaId: emp.id, razaoSocial: emp.razaoSocial })}
                                className="p-1.5 rounded-lg text-slate-400 hover:text-red-500 hover:bg-red-50 transition-colors"
                                title="Excluir"
                              >
                                <Trash2 size={15} />
                              </button>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Paginação */}
              <div className="flex items-center justify-between px-6 py-4 border-t border-slate-100">
                <p className="text-xs text-slate-400">
                  {result.totalElements === 0
                    ? 'Nenhum registro'
                    : `Exibindo ${page * 10 + 1}–${Math.min((page + 1) * 10, result.totalElements)} de ${result.totalElements} registro(s)`}
                </p>
                {result.totalPages > 1 && (
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => buscar(page - 1)}
                      disabled={page === 0}
                      className="p-2 rounded-lg border border-slate-200 text-slate-500 hover:bg-slate-50 disabled:opacity-40 transition-colors"
                    >
                      <ChevronLeft size={14} />
                    </button>
                    <span className="text-xs text-slate-500 px-1">
                      {page + 1} / {result.totalPages}
                    </span>
                    <button
                      onClick={() => buscar(page + 1)}
                      disabled={page >= result.totalPages - 1}
                      className="p-2 rounded-lg border border-slate-200 text-slate-500 hover:bg-slate-50 disabled:opacity-40 transition-colors"
                    >
                      <ChevronRight size={14} />
                    </button>
                  </div>
                )}
              </div>
            </>
          )}
        </div>
      )}
    </div>
  );
}
