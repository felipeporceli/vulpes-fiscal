import { useState } from 'react';
import { Search, Pencil, Trash2, X, ChevronLeft, ChevronRight, AlertTriangle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const API = import.meta.env.VITE_API_URL ?? '';

const REGIME_OPTIONS = [
  { value: '', label: 'Todos' },
  { value: 'SIMPLES_NACIONAL',       label: 'Simples Nacional'       },
  { value: 'LUCRO_PRESUMIDO',        label: 'Lucro Presumido'        },
  { value: 'LUCRO_REAL',             label: 'Lucro Real'             },
  { value: 'MEI',                    label: 'MEI'                    },
];

const PORTE_OPTIONS = [
  { value: '', label: 'Todos' },
  { value: 'MEI',    label: 'MEI'    },
  { value: 'ME',     label: 'ME'     },
  { value: 'EPP',    label: 'EPP'    },
  { value: 'MEDIO',  label: 'Médio'  },
  { value: 'GRANDE', label: 'Grande' },
];

const STATUS_OPTIONS = [
  { value: '', label: 'Todos' },
  { value: 'ATIVO',     label: 'Ativo'     },
  { value: 'INATIVO',   label: 'Inativo'   },
  { value: 'SUSPENSO',  label: 'Suspenso'  },
];

const AMBIENTE_OPTIONS = [
  { value: 'PRODUCAO',    label: 'Produção'     },
  { value: 'HOMOLOGACAO', label: 'Homologação'  },
];

const statusColor = {
  ATIVO:    'bg-emerald-100 text-emerald-700',
  INATIVO:  'bg-red-100    text-red-600',
  SUSPENSO: 'bg-slate-100  text-slate-500',
};

function formatCnpj(v) {
  if (!v) return '—';
  v = v.replace(/\D/g, '');
  return v.length === 14
    ? v.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5')
    : v;
}

function inputCls(extra = '') {
  return `w-full border border-slate-200 rounded-xl px-3 py-2 text-sm text-slate-700 focus:outline-none focus:border-vulpes-orange focus:ring-2 focus:ring-vulpes-orange/20 transition-all bg-white ${extra}`;
}
function labelCls() {
  return 'block text-xs font-medium text-slate-500 mb-1';
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
    status:             empresa.status              ?? 'ATIVO',
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
          <h2 className="font-bold text-slate-800">Editar Empresa #{empresaId}</h2>
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
          Tem certeza que deseja excluir <strong>{razaoSocial || `#${empresaId}`}</strong>?
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
  const { token } = useAuth();

  const [filters, setFilters] = useState({
    empresaId:          '',
    cnpj:               '',
    razaoSocial:        '',
    inscricaoEstadual:  '',
    regimeTributario:   '',
    status:             '',
    porte:              '',
  });

  const [result,      setResult]      = useState(null);   // Page<DTO>
  const [searchedId,  setSearchedId]  = useState(null);   // ID usado na última busca
  const [page,        setPage]        = useState(0);
  const [loading,     setLoading]     = useState(false);
  const [error,       setError]       = useState('');

  const [editTarget,   setEditTarget]   = useState(null);  // { empresa, empresaId }
  const [deleteTarget, setDeleteTarget] = useState(null);  // { empresaId, razaoSocial }
  const [toast,        setToast]        = useState(null);

  const setF = (k) => (e) => setFilters((p) => ({ ...p, [k]: e.target.value }));

  function showToast(msg, type = 'success') {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3500);
  }

  async function buscar(pg = 0) {
    if (!filters.empresaId) {
      setError('Informe o ID da empresa para pesquisar.');
      return;
    }
    setError('');
    setLoading(true);
    try {
      const params = new URLSearchParams({ pagina: pg, 'tamanho-pagina': 10 });
      if (filters.cnpj)              params.set('cnpj',              filters.cnpj);
      if (filters.razaoSocial)       params.set('razao-social',      filters.razaoSocial);
      if (filters.inscricaoEstadual) params.set('inscricao-estadual', filters.inscricaoEstadual);
      if (filters.regimeTributario)  params.set('regime-tributario', filters.regimeTributario);
      if (filters.status)            params.set('status',             filters.status);
      if (filters.porte)             params.set('porte',              filters.porte);

      const res = await fetch(`${API}/empresas/${filters.empresaId}?${params}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.mensagem || `Erro ${res.status}`);
      }

      const data = await res.json();
      setResult(data);
      setSearchedId(Number(filters.empresaId));
      setPage(pg);
    } catch (err) {
      setError(err.message);
      setResult(null);
    } finally {
      setLoading(false);
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
      <div>
        <h1 className="text-2xl font-extrabold text-slate-800 mb-1">Empresas</h1>
        <p className="text-slate-500 text-sm">Consulte, edite ou exclua empresas cadastradas no sistema.</p>
      </div>

      {/* Filtros */}
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
        <h2 className="font-semibold text-slate-700 text-sm mb-4">Filtros de pesquisa</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div>
            <label className={labelCls()}>
              ID da Empresa <span className="text-red-400">*</span>
            </label>
            <input
              type="number"
              value={filters.empresaId}
              onChange={setF('empresaId')}
              className={inputCls()}
              placeholder="ex: 1"
            />
          </div>
          <div>
            <label className={labelCls()}>CNPJ</label>
            <input value={filters.cnpj} onChange={setF('cnpj')} className={inputCls()} placeholder="00.000.000/0000-00" />
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
                      <th className="text-left px-6 py-3 text-xs font-semibold text-slate-500">Razão Social</th>
                      <th className="text-left px-4 py-3 text-xs font-semibold text-slate-500">Nome Fantasia</th>
                      <th className="text-left px-4 py-3 text-xs font-semibold text-slate-500">CNPJ</th>
                      <th className="text-left px-4 py-3 text-xs font-semibold text-slate-500">Regime</th>
                      <th className="text-left px-4 py-3 text-xs font-semibold text-slate-500">Porte</th>
                      <th className="text-left px-4 py-3 text-xs font-semibold text-slate-500">UF</th>
                      <th className="text-left px-4 py-3 text-xs font-semibold text-slate-500">Status</th>
                      <th className="px-4 py-3 text-xs font-semibold text-slate-500 text-right">Ações</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-50">
                    {items.map((emp, idx) => (
                      <tr key={idx} className="hover:bg-slate-50 transition-colors">
                        <td className="px-6 py-3.5 font-semibold text-slate-800 text-xs">{emp.razaoSocial || '—'}</td>
                        <td className="px-4 py-3.5 text-slate-500 text-xs">{emp.nomeFantasia || '—'}</td>
                        <td className="px-4 py-3.5 text-slate-500 text-xs font-mono">{formatCnpj(emp.cnpj)}</td>
                        <td className="px-4 py-3.5 text-slate-500 text-xs">{emp.regimeTributario || '—'}</td>
                        <td className="px-4 py-3.5 text-slate-500 text-xs">{emp.porte || '—'}</td>
                        <td className="px-4 py-3.5 text-slate-500 text-xs">{emp.uf || '—'}</td>
                        <td className="px-4 py-3.5">
                          <span className={`text-xs font-medium px-2.5 py-0.5 rounded-full ${statusColor[emp.status] || 'bg-slate-100 text-slate-500'}`}>
                            {emp.status || '—'}
                          </span>
                        </td>
                        <td className="px-4 py-3.5 text-right">
                          <div className="flex items-center justify-end gap-2">
                            <button
                              onClick={() => setEditTarget({ empresa: emp, empresaId: searchedId })}
                              className="p-1.5 rounded-lg text-slate-400 hover:text-vulpes-orange hover:bg-vulpes-orange/10 transition-colors"
                              title="Editar"
                            >
                              <Pencil size={14} />
                            </button>
                            <button
                              onClick={() => setDeleteTarget({ empresaId: searchedId, razaoSocial: emp.razaoSocial })}
                              className="p-1.5 rounded-lg text-slate-400 hover:text-red-500 hover:bg-red-50 transition-colors"
                              title="Excluir"
                            >
                              <Trash2 size={14} />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Paginação */}
              {result.totalPages > 1 && (
                <div className="flex items-center justify-between px-6 py-4 border-t border-slate-100">
                  <p className="text-xs text-slate-400">
                    Página {page + 1} de {result.totalPages}
                  </p>
                  <div className="flex gap-2">
                    <button
                      onClick={() => buscar(page - 1)}
                      disabled={page === 0}
                      className="p-2 rounded-lg border border-slate-200 text-slate-500 hover:bg-slate-50 disabled:opacity-40 transition-colors"
                    >
                      <ChevronLeft size={14} />
                    </button>
                    <button
                      onClick={() => buscar(page + 1)}
                      disabled={page >= result.totalPages - 1}
                      className="p-2 rounded-lg border border-slate-200 text-slate-500 hover:bg-slate-50 disabled:opacity-40 transition-colors"
                    >
                      <ChevronRight size={14} />
                    </button>
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
