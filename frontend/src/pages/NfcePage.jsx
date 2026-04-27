import { useState, useEffect, useRef } from 'react';
import {
  Search, X, ChevronLeft, ChevronRight,
  AlertTriangle, ChevronUp, ChevronDown, ChevronsUpDown,
  Eye, Download, FileText, FileSpreadsheet, FileCode2,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const API = import.meta.env.VITE_API_URL ?? '';

// ─── Helpers ─────────────────────────────────────────────────────────────────

const STATUS_OPTIONS = [
  { value: 'GERADA',      label: 'Gerada'      },
  { value: 'AUTORIZADA',  label: 'Autorizada'  },
  { value: 'REJEITADA',   label: 'Rejeitada'   },
  { value: 'CANCELADA',   label: 'Cancelada'   },
  { value: 'INUTILIZADA', label: 'Inutilizada' },
];

const statusStyle = {
  GERADA:      'bg-blue-100    text-blue-700',
  AUTORIZADA:  'bg-emerald-100 text-emerald-700',
  REJEITADA:   'bg-red-100     text-red-600',
  CANCELADA:   'bg-slate-100   text-slate-500',
  INUTILIZADA: 'bg-amber-100   text-amber-700',
};

function labelStatus(v) {
  return STATUS_OPTIONS.find(s => s.value === v)?.label ?? v ?? '—';
}

function fmtMoeda(v) {
  if (v == null) return '—';
  return `R$ ${Number(v).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function fmtDataHora(v) {
  if (!v) return '—';
  return new Date(v).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
}

function truncarChave(chave) {
  if (!chave) return '—';
  return chave.length > 20 ? `${chave.slice(0, 10)}…${chave.slice(-10)}` : chave;
}

function SortIcon({ col, sortCol, sortDir }) {
  if (sortCol !== col) return <ChevronsUpDown size={12} className="ml-1 opacity-30 inline-block" />;
  return sortDir === 'asc'
    ? <ChevronUp   size={12} className="ml-1 text-vulpes-orange inline-block" />
    : <ChevronDown size={12} className="ml-1 text-vulpes-orange inline-block" />;
}

function inputCls(extra = '') {
  return `w-full border border-slate-200 rounded-xl px-3 py-2 text-sm text-slate-700 focus:outline-none focus:border-vulpes-orange focus:ring-2 focus:ring-vulpes-orange/20 transition-all bg-white ${extra}`;
}
function labelCls() { return 'block text-xs font-medium text-slate-500 mb-1'; }

// ─── Modal de visualização ────────────────────────────────────────────────────
function ViewModal({ nfce, onClose }) {
  const n = nfce;

  function row(label, value) {
    return (
      <div>
        <p className={labelCls()}>{label}</p>
        <p className="text-sm text-slate-700 bg-slate-50 rounded-xl px-3 py-2 min-h-[38px] break-all">{value ?? '—'}</p>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-xl max-h-[90vh] flex flex-col">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <div>
            <h2 className="font-bold text-slate-800">NFC-e #{n.numero} — Série {n.serie}</h2>
            <p className="text-xs text-slate-400 mt-0.5">Visualização somente leitura</p>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700 transition-colors">
            <X size={20} />
          </button>
        </div>

        <div className="flex-1 overflow-y-auto p-6 space-y-5">
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Identificação</p>
            <div className="grid grid-cols-2 gap-4">
              {row('Número', n.numero)}
              {row('Série', n.serie)}
              {row('Data de Emissão', fmtDataHora(n.dataEmissao))}
              {row('Data de Criação', fmtDataHora(n.dataCriacao))}
            </div>
          </section>

          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Status</p>
            <div className="flex items-center gap-3">
              <span className={`text-sm font-semibold px-3 py-1.5 rounded-full ${statusStyle[n.statusNfce] ?? 'bg-slate-100 text-slate-500'}`}>
                {labelStatus(n.statusNfce)}
              </span>
            </div>
          </section>

          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Valores</p>
            <div className="grid grid-cols-1 gap-4">
              {row('Valor Total', fmtMoeda(n.valorTotal))}
            </div>
          </section>

          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Autorização SEFAZ</p>
            <div className="space-y-4">
              <div>
                <p className={labelCls()}>Chave de Acesso</p>
                <p className="text-xs font-mono text-slate-700 bg-slate-50 rounded-xl px-3 py-2 break-all min-h-[38px]">
                  {n.chaveAcesso ?? '—'}
                </p>
              </div>
              {row('Protocolo de Autorização', n.protocoloAutorizacao)}
            </div>
          </section>
        </div>

        <div className="flex justify-end px-6 py-4 border-t border-slate-100">
          <button onClick={onClose} className="px-5 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50 transition-colors">
            Fechar
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── Exportação CSV ───────────────────────────────────────────────────────────
function exportCsvNfce(rows, baseName) {
  const headers = ['Número','Série','Valor Total','Status','Protocolo','Chave de Acesso','Data Emissão'];
  const escape = (v) => {
    const s = v == null ? '' : String(v);
    if (s.includes(',') || s.includes('"') || s.includes('\n')) return `"${s.replace(/"/g, '""')}"`;
    return s;
  };
  const lines = rows.map(n => [
    n.numero, n.serie, n.valorTotal, n.statusNfce,
    n.protocoloAutorizacao ?? '', n.chaveAcesso ?? '',
    n.dataEmissao ? new Date(n.dataEmissao).toLocaleString('pt-BR') : '',
  ].map(escape).join(','));
  const content = '\uFEFF' + [headers.map(escape).join(','), ...lines].join('\r\n');
  const url = URL.createObjectURL(new Blob([content], { type: 'text/csv;charset=utf-8;' }));
  const a = document.createElement('a');
  a.href = url; a.download = `${baseName}.csv`; a.click();
  URL.revokeObjectURL(url);
}

// ─── NfcePage ─────────────────────────────────────────────────────────────────
export default function NfcePage() {
  const { token, user } = useAuth();

  const isRestrito = !user?.hasRole('ADMINISTRADOR', 'SUPORTE');

  const [empresaId,       setEmpresaId]       = useState(isRestrito ? String(user?.empresaId ?? '') : '');
  const [empresasOptions, setEmpresasOptions] = useState([]);

  const [filters, setFilters] = useState({
    estabelecimentoId: '',
    status:            '',
    chaveAcesso:       '',
    numero:            '',
    dataInicio:        '',
    dataFim:           '',
  });

  const [result,     setResult]     = useState(null);
  const [page,       setPage]       = useState(0);
  const [sortCol,    setSortCol]    = useState(null);
  const [sortDir,    setSortDir]    = useState('desc');
  const [loading,    setLoading]    = useState(false);
  const [error,      setError]      = useState('');
  const [viewTarget, setViewTarget] = useState(null);
  const [toast,      setToast]      = useState(null);
  const [exportOpen, setExportOpen] = useState(false);
  const [exporting,  setExporting]  = useState(false);
  const exportRef = useRef(null);

  const setF = (k) => (e) => setFilters(p => ({ ...p, [k]: e.target.value }));

  useEffect(() => {
    if (isRestrito) return;
    fetch(`${API}/empresas?tamanho-pagina=100&pagina=0`, {
      headers: { Authorization: `Bearer ${token}` },
    }).then(r => r.json()).then(d => setEmpresasOptions(d.content ?? [])).catch(() => {});
  }, [isRestrito, token]); // eslint-disable-line react-hooks/exhaustive-deps

  function showToast(msg, type = 'success') {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3500);
  }

  function isoDateTime(dateStr, endOfDay = false) {
    if (!dateStr) return null;
    return endOfDay ? `${dateStr}T23:59:59` : `${dateStr}T00:00:00`;
  }

  function buildParams(pg) {
    const params = new URLSearchParams({ pagina: pg, 'tamanho-pagina': 10 });
    if (filters.estabelecimentoId) params.set('estabelecimento-id', filters.estabelecimentoId);
    if (filters.status)            params.set('status',             filters.status);
    if (filters.chaveAcesso)       params.set('chave-acesso',       filters.chaveAcesso);
    if (filters.numero)            params.set('numero',             filters.numero);
    const di = isoDateTime(filters.dataInicio);
    const df = isoDateTime(filters.dataFim, true);
    if (di) params.set('data-inicio', di);
    if (df) params.set('data-fim',    df);
    return params;
  }

  async function buscar(pg = 0) {
    setError(''); setLoading(true);
    try {
      const params = buildParams(pg);
      let url;
      if (isRestrito) {
        url = `${API}/nfce/empresa/${user?.empresaId}?${params}`;
      } else {
        if (empresaId) params.set('empresa-id', empresaId);
        url = `${API}/nfce?${params}`;
      }
      const res = await fetch(url, { headers: { Authorization: `Bearer ${token}` } });
      if (!res.ok) {
        const bd = await res.json().catch(() => ({}));
        throw new Error(bd.mensagem || `Erro ${res.status}`);
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

  useEffect(() => {
    if (isRestrito) buscar(0);
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  useEffect(() => {
    function onClickOutside(e) {
      if (exportRef.current && !exportRef.current.contains(e.target)) setExportOpen(false);
    }
    document.addEventListener('mousedown', onClickOutside);
    return () => document.removeEventListener('mousedown', onClickOutside);
  }, []);

  function baixarXml(nfce) {
    showToast(`XML disponível após integração com certificado A1 — NFC-e ${nfce.numero}`, 'error');
  }

  function baixarPdf(nfce) {
    showToast(`PDF disponível após integração com certificado A1 — NFC-e ${nfce.numero}`, 'error');
  }

  async function handleExport(format) {
    setExportOpen(false);
    setExporting(true);
    try {
      const total = result?.totalElements ?? 0;
      if (total === 0) { showToast('Nenhum dado para exportar.', 'error'); return; }

      const PAGE_SIZE = 100;
      const totalPages = Math.ceil(Math.min(total, 10000) / PAGE_SIZE);
      const allRows = [];

      for (let pg = 0; pg < totalPages; pg++) {
        const params = buildParams(pg);
        params.set('tamanho-pagina', PAGE_SIZE);
        let url;
        if (isRestrito) {
          url = `${API}/nfce/empresa/${user?.empresaId}?${params}`;
        } else {
          if (empresaId) params.set('empresa-id', empresaId);
          url = `${API}/nfce?${params}`;
        }
        const res = await fetch(url, { headers: { Authorization: `Bearer ${token}` } });
        if (!res.ok) throw new Error(`Erro ao buscar dados (página ${pg + 1}): ${res.status}`);
        const data = await res.json();
        allRows.push(...(data.content ?? []));
        if ((data.content?.length ?? 0) < PAGE_SIZE) break;
      }

      if (allRows.length === 0) { showToast('Nenhum dado retornado.', 'error'); return; }
      exportCsvNfce(allRows, 'nfce');
      showToast(`CSV exportado — ${allRows.length} registro(s)`);
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
        <div className={`fixed top-5 right-5 z-[100] flex items-center gap-3 px-5 py-3 rounded-xl shadow-xl text-white text-sm font-medium transition-all ${toast.type === 'error' ? 'bg-red-500' : 'bg-emerald-500'}`}>
          {toast.msg}
        </div>
      )}

      {viewTarget && (
        <ViewModal nfce={viewTarget} onClose={() => setViewTarget(null)} />
      )}

      {/* Cabeçalho */}
      <div>
        <h1 className="text-2xl font-extrabold text-slate-800 mb-1">NFC-e</h1>
        <p className="text-slate-500 text-sm">Consulte as notas fiscais de consumidor emitidas.</p>
      </div>

      {/* Filtros */}
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
        <h2 className="font-semibold text-slate-700 text-sm mb-4">Filtros de pesquisa</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">

          {!isRestrito && (
            <div className="sm:col-span-2">
              <label className={labelCls()}>Empresa</label>
              <select value={empresaId} onChange={e => setEmpresaId(e.target.value)} className={inputCls()}>
                <option value="">Todas as empresas</option>
                {empresasOptions.map(emp => (
                  <option key={emp.id} value={String(emp.id)}>
                    {emp.razaoSocial}{emp.nomeFantasia ? ` — ${emp.nomeFantasia}` : ''}
                  </option>
                ))}
              </select>
            </div>
          )}

          <div>
            <label className={labelCls()}>ID do Estabelecimento</label>
            <input type="number" value={filters.estabelecimentoId} onChange={setF('estabelecimentoId')} className={inputCls()} placeholder="Ex: 3" />
          </div>

          <div>
            <label className={labelCls()}>Status</label>
            <select value={filters.status} onChange={setF('status')} className={inputCls()}>
              <option value="">Todos</option>
              {STATUS_OPTIONS.map(s => (
                <option key={s.value} value={s.value}>{s.label}</option>
              ))}
            </select>
          </div>

          <div>
            <label className={labelCls()}>Número</label>
            <input value={filters.numero} onChange={setF('numero')} className={inputCls()} placeholder="Ex: 000000001" />
          </div>

          <div className="sm:col-span-2">
            <label className={labelCls()}>Chave de Acesso</label>
            <input value={filters.chaveAcesso} onChange={setF('chaveAcesso')} className={inputCls()} placeholder="Busca parcial por chave…" />
          </div>

          <div>
            <label className={labelCls()}>Data início</label>
            <input type="date" value={filters.dataInicio} onChange={setF('dataInicio')} className={inputCls()} />
          </div>

          <div>
            <label className={labelCls()}>Data fim</label>
            <input type="date" value={filters.dataFim} onChange={setF('dataFim')} className={inputCls()} />
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

      {/* Tabela */}
      {result && (
        <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
          <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
            <h2 className="font-bold text-slate-800 text-sm">
              Resultados
              <span className="ml-2 text-xs font-normal text-slate-400">
                {result.totalElements} NFC-e encontrada(s)
              </span>
            </h2>

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
                      <FileText size={14} className="text-emerald-500" /> Exportar como CSV
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>

          {items.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-16 text-slate-400">
              <Search size={36} className="mb-3 opacity-40" />
              <p className="text-sm">Nenhuma NFC-e encontrada com esses filtros.</p>
            </div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="bg-slate-50 border-b border-slate-100">
                      {[
                        { label: 'Número',    col: 'numero',      px: 'px-6' },
                        { label: 'Série',     col: 'serie',       px: 'px-4' },
                        { label: 'Valor',     col: 'valorTotal',  px: 'px-4' },
                        { label: 'Status',    col: 'statusNfce',  px: 'px-4' },
                        { label: 'Protocolo', col: 'protocolo',   px: 'px-4' },
                        { label: 'Chave',     col: 'chaveAcesso', px: 'px-4' },
                        { label: 'Emissão',   col: 'dataEmissao', px: 'px-4' },
                      ].map(({ label, col, px }) => (
                        <th key={col} className={`text-left ${px} py-3.5 text-xs font-semibold text-slate-500 whitespace-nowrap`}>
                          {label}
                        </th>
                      ))}
                      <th className="px-4 py-3.5 text-xs font-semibold text-slate-500 text-right">Ações</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {items.map(n => (
                      <tr key={n.id} className="hover:bg-slate-50 transition-colors">
                        <td className="px-6 py-4 font-semibold text-slate-800 text-xs font-mono">{n.numero}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs text-center">{n.serie}</td>
                        <td className="px-4 py-4 text-slate-700 text-xs font-semibold">{fmtMoeda(n.valorTotal)}</td>
                        <td className="px-4 py-4 text-xs">
                          <span className={`font-semibold px-2.5 py-1 rounded-full text-xs ${statusStyle[n.statusNfce] ?? 'bg-slate-100 text-slate-500'}`}>
                            {labelStatus(n.statusNfce)}
                          </span>
                        </td>
                        <td className="px-4 py-4 text-slate-500 text-xs font-mono">
                          {n.protocoloAutorizacao ?? '—'}
                        </td>
                        <td className="px-4 py-4 text-slate-500 text-xs font-mono max-w-[180px] truncate" title={n.chaveAcesso ?? ''}>
                          {truncarChave(n.chaveAcesso)}
                        </td>
                        <td className="px-4 py-4 text-slate-500 text-xs whitespace-nowrap">
                          {fmtDataHora(n.dataEmissao)}
                        </td>
                        <td className="px-4 py-4 text-right">
                          <div className="flex items-center justify-end gap-1">
                            <button
                              onClick={() => setViewTarget(n)}
                              className="p-1.5 rounded-lg text-slate-400 hover:text-blue-500 hover:bg-blue-50 transition-colors"
                              title="Visualizar"
                            >
                              <Eye size={15} />
                            </button>
                            <button
                              onClick={() => baixarXml(n)}
                              className="p-1.5 rounded-lg text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 transition-colors"
                              title="Baixar XML"
                            >
                              <FileCode2 size={15} />
                            </button>
                            <button
                              onClick={() => baixarPdf(n)}
                              className="p-1.5 rounded-lg text-slate-400 hover:text-orange-500 hover:bg-orange-50 transition-colors"
                              title="Baixar PDF (DANFE)"
                            >
                              <FileText size={15} />
                            </button>
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
                    <span className="text-xs text-slate-500 px-1">{page + 1} / {result.totalPages}</span>
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
