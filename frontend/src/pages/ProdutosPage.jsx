import { useState, useEffect, useRef } from 'react';
import {
  Search, Pencil, Trash2, X, ChevronLeft, ChevronRight,
  AlertTriangle, ChevronUp, ChevronDown, ChevronsUpDown,
  Plus, Eye, Download, FileText, FileSpreadsheet,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import {
  exportCsvProduto,
  exportXlsxProduto,
  EXPORT_MAX_ROWS,
} from '../utils/exportUtils';

const API = import.meta.env.VITE_API_URL ?? '';

// ─── Helpers ─────────────────────────────────────────────────────────────────

function SortIcon({ col, sortCol, sortDir }) {
  if (sortCol !== col) return <ChevronsUpDown size={12} className="ml-1 opacity-30 inline-block" />;
  return sortDir === 'asc'
    ? <ChevronUp   size={12} className="ml-1 text-vulpes-orange inline-block" />
    : <ChevronDown size={12} className="ml-1 text-vulpes-orange inline-block" />;
}

function maskPreco(raw) {
  const digits = raw.replace(/\D/g, '');
  if (!digits) return '';
  const padded = digits.replace(/^0+/, '').padStart(3, '0');
  const intPart = padded.slice(0, -2).replace(/\B(?=(\d{3})+(?!\d))/g, '.');
  return `${intPart},${padded.slice(-2)}`;
}

function parsePreco(formatted) {
  if (!formatted) return null;
  return parseFloat(formatted.replace(/\./g, '').replace(',', '.'));
}

function inputCls(extra = '') {
  return `w-full border border-slate-200 rounded-xl px-3 py-2 text-sm text-slate-700 focus:outline-none focus:border-vulpes-orange focus:ring-2 focus:ring-vulpes-orange/20 transition-all bg-white ${extra}`;
}
function errCls(k, errors) {
  return errors?.[k]
    ? 'w-full border border-red-400 rounded-xl px-3 py-2 text-sm text-slate-700 focus:outline-none focus:border-red-400 focus:ring-2 focus:ring-red-200 transition-all bg-white'
    : inputCls();
}
function labelCls() { return 'block text-xs font-medium text-slate-500 mb-1'; }

function Field({ k, label, required, errors, children }) {
  return (
    <div>
      <label className={labelCls()}>
        {label}{required && <span className="text-red-400 ml-0.5">*</span>}
      </label>
      {children}
      {errors?.[k] && <p className="mt-1 text-red-500 text-xs">{errors[k]}</p>}
    </div>
  );
}

// ─── Modal de cadastro ────────────────────────────────────────────────────────
function CreateModal({ token, isRestrito, userEmpresaId, empresasOptions, onClose, onSaved }) {
  const [modalEmpresaId,        setModalEmpresaId]        = useState(isRestrito ? String(userEmpresaId ?? '') : '');
  const [estabelecimentos,      setEstabelecimentos]      = useState([]);
  const [modalEstabelecimentoId,setModalEstabelecimentoId]= useState('');
  const [loadingEst,            setLoadingEst]            = useState(false);

  const EMPTY = {
    idProduto:    '',
    descricao:    '',
    codigoBarras: '',
    ncm:          '',
    cfop:         '',
    unidade:      '',
    preco:        '',
    ativo:        true,
    qtdEstoque:   '',
    cest:         '',
    orig:         '',
  };
  const [form,     setForm]     = useState(EMPTY);
  const [loading,  setLoading]  = useState(false);
  const [errors,   setErrors]   = useState({});
  const [apiError, setApiError] = useState('');

  const set = (k) => (e) => setForm((p) => ({ ...p, [k]: e.target.value }));

  useEffect(() => {
    const eid = modalEmpresaId;
    if (!eid) { setEstabelecimentos([]); setModalEstabelecimentoId(''); return; }
    setLoadingEst(true);
    fetch(`${API}/estabelecimentos/empresa/${eid}?tamanho-pagina=100&pagina=0`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((r) => r.json())
      .then((data) => setEstabelecimentos(data.content ?? []))
      .catch(() => setEstabelecimentos([]))
      .finally(() => setLoadingEst(false));
  }, [modalEmpresaId, token]);

  function validate() {
    const e = {};
    if (!modalEmpresaId)        e.empresa          = 'Selecione uma empresa';
    if (!modalEstabelecimentoId)e.estabelecimento   = 'Selecione um estabelecimento';
    if (!form.idProduto.toString().trim()) e.idProduto = 'ID do Produto é obrigatório';
    else if (isNaN(Number(form.idProduto))) e.idProduto = 'ID do Produto deve ser um número';
    if (!form.descricao.trim()) e.descricao         = 'Descrição é obrigatória';
    if (!form.ncm.toString().trim())   e.ncm        = 'NCM é obrigatório';
    else if (isNaN(Number(form.ncm))) e.ncm         = 'NCM deve ser um número';
    if (!form.cfop.toString().trim())  e.cfop       = 'CFOP é obrigatório';
    else if (isNaN(Number(form.cfop))) e.cfop       = 'CFOP deve ser um número';
    if (!form.unidade.trim())          e.unidade    = 'Unidade é obrigatória';
    if (!form.preco.trim()) e.preco = 'Preço é obrigatório';
    else if (isNaN(parsePreco(form.preco)) || parsePreco(form.preco) < 0) e.preco = 'Preço inválido';
    if (!form.qtdEstoque.toString().trim()) e.qtdEstoque = 'Qtd. Estoque é obrigatória';
    else if (isNaN(Number(form.qtdEstoque))) e.qtdEstoque = 'Qtd. Estoque deve ser um número';
    if (!form.cest.trim()) e.cest                   = 'CEST é obrigatório';
    if (!form.orig.toString().trim()) e.orig        = 'Orig é obrigatório';
    else if (isNaN(Number(form.orig))) e.orig       = 'Orig deve ser um número';
    return e;
  }

  async function handleSubmit(ev) {
    ev.preventDefault();
    setApiError('');
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }

    setLoading(true);
    try {
      const res = await fetch(
        `${API}/produtos/empresa/${modalEmpresaId}/estabelecimento/${modalEstabelecimentoId}`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
          body: JSON.stringify({
            idProduto:    Number(form.idProduto),
            descricao:    form.descricao,
            codigoBarras: form.codigoBarras || null,
            ncm:          Number(form.ncm),
            cfop:         Number(form.cfop),
            unidade:      form.unidade,
            preco:        parsePreco(form.preco),
            ativo:        form.ativo === true || form.ativo === 'true',
            qtdEstoque:   Number(form.qtdEstoque),
            cest:         form.cest,
            orig:         Number(form.orig),
          }),
        }
      );
      if (!res.ok) {
        const bd = await res.json().catch(() => ({}));
        const detalhe = bd.erros?.map(e => `${e.campo}: ${e.mensagem}`).join(' | ');
        throw new Error(detalhe || bd.mensagem || `Erro ${res.status}`);
      }
      onSaved();
    } catch (err) {
      setApiError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <div>
            <h2 className="font-bold text-slate-800">Adicionar novo produto</h2>
            <p className="text-xs text-slate-400 mt-0.5">Campos com <span className="text-red-400">*</span> são obrigatórios</p>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700 transition-colors"><X size={20} /></button>
        </div>

        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6 space-y-5">
          {apiError && (
            <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl px-4 py-3">
              <AlertTriangle size={14} className="mt-0.5 flex-shrink-0" />{apiError}
            </div>
          )}
          {Object.keys(errors).length > 0 && (
            <div className="bg-amber-50 border border-amber-200 rounded-xl px-4 py-3">
              <p className="text-amber-700 text-xs font-semibold mb-1 flex items-center gap-1.5">
                <AlertTriangle size={13} /> Corrija os campos destacados:
              </p>
              <ul className="list-disc list-inside space-y-0.5">
                {Object.values(errors).map((msg, i) => (
                  <li key={i} className="text-amber-700 text-xs">{msg}</li>
                ))}
              </ul>
            </div>
          )}

          {/* Empresa e Estabelecimento */}
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Vínculo</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {!isRestrito && (
                <div className="sm:col-span-2">
                  <Field k="empresa" label="Empresa" required errors={errors}>
                    <select value={modalEmpresaId} onChange={(e) => setModalEmpresaId(e.target.value)} className={errCls('empresa', errors)}>
                      <option value="">Selecione uma empresa…</option>
                      {empresasOptions.map((emp) => (
                        <option key={emp.id} value={String(emp.id)}>
                          {emp.razaoSocial}{emp.nomeFantasia ? ` — ${emp.nomeFantasia}` : ''}
                        </option>
                      ))}
                    </select>
                  </Field>
                </div>
              )}
              <div className={isRestrito ? 'sm:col-span-2' : 'sm:col-span-2'}>
                <Field k="estabelecimento" label="Estabelecimento" required errors={errors}>
                  <select
                    value={modalEstabelecimentoId}
                    onChange={(e) => setModalEstabelecimentoId(e.target.value)}
                    disabled={!modalEmpresaId || loadingEst}
                    className={errCls('estabelecimento', errors)}
                  >
                    <option value="">
                      {loadingEst ? 'Carregando…' : modalEmpresaId ? 'Selecione um estabelecimento…' : 'Selecione uma empresa primeiro'}
                    </option>
                    {estabelecimentos.map((est) => (
                      <option key={est.id} value={String(est.id)}>
                        {est.nomeFantasia || est.cnpj || `#${est.id}`}
                      </option>
                    ))}
                  </select>
                </Field>
              </div>
            </div>
          </section>

          {/* Identificação */}
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Identificação</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Field k="idProduto" label="ID do Produto" required errors={errors}>
                <input type="number" value={form.idProduto} onChange={set('idProduto')} className={errCls('idProduto', errors)} placeholder="Ex: 1001" />
              </Field>
              <div className="sm:col-span-2">
                <Field k="descricao" label="Descrição" required errors={errors}>
                  <input value={form.descricao} onChange={set('descricao')} className={errCls('descricao', errors)} placeholder="Descrição do produto" />
                </Field>
              </div>
              <Field k="codigoBarras" label="Código de Barras" errors={errors}>
                <input value={form.codigoBarras} onChange={set('codigoBarras')} className={inputCls()} placeholder="EAN-13 (opcional)" />
              </Field>
              <Field k="unidade" label="Unidade" required errors={errors}>
                <input value={form.unidade} onChange={set('unidade')} className={errCls('unidade', errors)} placeholder="Ex: UN, KG, CX" />
              </Field>
            </div>
          </section>

          {/* Tributação */}
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Tributação</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Field k="ncm" label="NCM" required errors={errors}>
                <input type="number" value={form.ncm} onChange={set('ncm')} className={errCls('ncm', errors)} placeholder="Ex: 22021000" />
              </Field>
              <Field k="cfop" label="CFOP" required errors={errors}>
                <input type="number" value={form.cfop} onChange={set('cfop')} className={errCls('cfop', errors)} placeholder="Ex: 5102" />
              </Field>
              <Field k="cest" label="CEST" required errors={errors}>
                <input value={form.cest} onChange={set('cest')} className={errCls('cest', errors)} placeholder="Ex: 0300300" />
              </Field>
              <Field k="orig" label="Origem (ICMS)" required errors={errors}>
                <input type="number" value={form.orig} onChange={set('orig')} className={errCls('orig', errors)} placeholder="0 = Nacional" />
              </Field>
            </div>
          </section>

          {/* Estoque e Preço */}
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Estoque e Preço</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Field k="preco" label="Preço (R$)" required errors={errors}>
                <div className="relative">
                  <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-sm pointer-events-none">R$</span>
                  <input
                    inputMode="numeric"
                    value={form.preco}
                    onChange={(e) => setForm((p) => ({ ...p, preco: maskPreco(e.target.value) }))}
                    className={`${errCls('preco', errors)} pl-9`}
                    placeholder="0,00"
                  />
                </div>
              </Field>
              <Field k="qtdEstoque" label="Qtd. Estoque" required errors={errors}>
                <input type="number" value={form.qtdEstoque} onChange={set('qtdEstoque')} className={errCls('qtdEstoque', errors)} placeholder="0" />
              </Field>
              <div className="flex items-center gap-3 pt-4">
                <input
                  type="checkbox"
                  id="ativo-create"
                  checked={form.ativo === true || form.ativo === 'true'}
                  onChange={(e) => setForm((p) => ({ ...p, ativo: e.target.checked }))}
                  className="w-4 h-4 rounded border-slate-300 accent-orange-500 cursor-pointer"
                />
                <label htmlFor="ativo-create" className="text-sm text-slate-700 cursor-pointer select-none">Produto ativo?</label>
              </div>
            </div>
          </section>
        </form>

        <div className="flex items-center justify-end gap-3 px-6 py-4 border-t border-slate-100">
          <button type="button" onClick={onClose} className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50 transition-colors">
            Cancelar
          </button>
          <button
            onClick={handleSubmit}
            disabled={loading}
            className="flex items-center gap-2 px-5 py-2 rounded-xl text-white text-sm font-semibold disabled:opacity-60 transition-all hover:scale-[1.02]"
            style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}
          >
            <Plus size={15} />
            {loading ? 'Cadastrando…' : 'Cadastrar produto'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── Modal de visualização (somente leitura) ──────────────────────────────────
function ViewModal({ produto, onClose }) {
  const p = produto;

  function row(label, value) {
    return (
      <div>
        <p className={labelCls()}>{label}</p>
        <p className="text-sm text-slate-700 bg-slate-50 rounded-xl px-3 py-2 min-h-[38px]">{value ?? '—'}</p>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <div>
            <h2 className="font-bold text-slate-800">Detalhes do Produto</h2>
            <p className="text-xs text-slate-400 mt-0.5">Visualização somente leitura</p>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700 transition-colors"><X size={20} /></button>
        </div>

        <div className="flex-1 overflow-y-auto p-6 space-y-5">
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Identificação</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {row('ID Produto', p.idProduto)}
              {row('Código de Barras', p.codigoBarras)}
              <div className="sm:col-span-2">{row('Descrição', p.descricao)}</div>
              {row('Unidade', p.unidade)}
              <div>
                <p className={labelCls()}>Ativo</p>
                <div className="mt-1">
                  <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${p.ativo ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-600'}`}>
                    {p.ativo ? 'Sim' : 'Não'}
                  </span>
                </div>
              </div>
            </div>
          </section>

          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Tributação</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {row('NCM', p.ncm)}
              {row('CFOP', p.cfop)}
              {row('CEST', p.cest)}
              {row('Orig (ICMS)', p.orig)}
            </div>
          </section>

          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Estoque e Preço</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {row('Preço (R$)', p.preco != null ? Number(p.preco).toFixed(2) : '—')}
              {row('Qtd. Estoque', p.qtdEstoque)}
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

// ─── Modal de edição ──────────────────────────────────────────────────────────
function EditModal({ produto, token, onClose, onSaved }) {
  const [form, setForm] = useState({
    descricao:    produto.descricao    ?? '',
    codigoBarras: produto.codigoBarras ?? '',
    ncm:          produto.ncm != null ? String(produto.ncm) : '',
    cfop:         produto.cfop         ?? '',
    unidade:      produto.unidade      ?? '',
    preco:        produto.preco != null ? maskPreco(String(Math.round(Number(produto.preco) * 100))) : '',
    ativo:        produto.ativo        ?? true,
    qtdEstoque:   produto.qtdEstoque   ?? '',
    cest:         produto.cest         ?? '',
    orig:         produto.orig         ?? '',
  });
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState('');

  const set = (k) => (e) => setForm((p) => ({ ...p, [k]: e.target.value }));

  async function handleSubmit(ev) {
    ev.preventDefault();
    setError('');
    setLoading(true);
    try {
      // AtualizacaoProdutoDTO has @JsonIgnoreProperties(ignoreUnknown = false)
      // Only send declared fields; ncm must be String
      const payload = {
        descricao:    form.descricao    || null,
        codigoBarras: form.codigoBarras || null,
        ncm:          form.ncm          || null,
        cfop:         form.cfop !== ''  ? Number(form.cfop)  : null,
        unidade:      form.unidade      || null,
        preco:        form.preco !== '' ? parsePreco(form.preco) : null,
        ativo:        form.ativo === true || form.ativo === 'true',
        qtdEstoque:   form.qtdEstoque !== '' ? Number(form.qtdEstoque) : null,
        cest:         form.cest         || null,
        orig:         form.orig !== ''  ? Number(form.orig)  : null,
      };
      const res = await fetch(
        `${API}/produtos/empresa/${produto.empresaId}/${produto.idProduto}`,
        {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
          body: JSON.stringify(payload),
        }
      );
      if (!res.ok) {
        const bd = await res.json().catch(() => ({}));
        const detalhe = bd.erros?.map(e => `${e.campo}: ${e.mensagem}`).join(' | ');
        throw new Error(detalhe || bd.mensagem || `Erro ${res.status}`);
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
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <h2 className="font-bold text-slate-800">Editar Produto — #{produto.idProduto}</h2>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700 transition-colors"><X size={20} /></button>
        </div>

        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6 space-y-5">
          {error && (
            <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl px-4 py-3">
              <AlertTriangle size={14} className="mt-0.5 flex-shrink-0" />{error}
            </div>
          )}

          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Identificação</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="sm:col-span-2">
                <label className={labelCls()}>Descrição</label>
                <input value={form.descricao} onChange={set('descricao')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>Código de Barras</label>
                <input value={form.codigoBarras} onChange={set('codigoBarras')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>Unidade</label>
                <input value={form.unidade} onChange={set('unidade')} className={inputCls()} placeholder="UN, KG, CX…" />
              </div>
            </div>
          </section>

          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Tributação</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className={labelCls()}>NCM</label>
                <input value={form.ncm} onChange={set('ncm')} className={inputCls()} placeholder="Ex: 22021000" />
              </div>
              <div>
                <label className={labelCls()}>CFOP</label>
                <input type="number" value={form.cfop} onChange={set('cfop')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>CEST</label>
                <input value={form.cest} onChange={set('cest')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>Origem (ICMS)</label>
                <input type="number" value={form.orig} onChange={set('orig')} className={inputCls()} />
              </div>
            </div>
          </section>

          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Estoque e Preço</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className={labelCls()}>Preço (R$)</label>
                <div className="relative">
                  <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-sm pointer-events-none">R$</span>
                  <input
                    inputMode="numeric"
                    value={form.preco}
                    onChange={(e) => setForm((p) => ({ ...p, preco: maskPreco(e.target.value) }))}
                    className={`${inputCls()} pl-9`}
                    placeholder="0,00"
                  />
                </div>
              </div>
              <div>
                <label className={labelCls()}>Qtd. Estoque</label>
                <input type="number" value={form.qtdEstoque} onChange={set('qtdEstoque')} className={inputCls()} />
              </div>
              <div className="flex items-center gap-3 pt-4">
                <input
                  type="checkbox"
                  id="ativo-edit"
                  checked={form.ativo === true || form.ativo === 'true'}
                  onChange={(e) => setForm((p) => ({ ...p, ativo: e.target.checked }))}
                  className="w-4 h-4 rounded border-slate-300 accent-orange-500 cursor-pointer"
                />
                <label htmlFor="ativo-edit" className="text-sm text-slate-700 cursor-pointer select-none">Produto ativo?</label>
              </div>
            </div>
          </section>
        </form>

        <div className="flex items-center justify-end gap-3 px-6 py-4 border-t border-slate-100">
          <button type="button" onClick={onClose} className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50 transition-colors">
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
function DeleteModal({ produto, token, onClose, onDeleted }) {
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState('');

  async function handleDelete() {
    setLoading(true);
    setError('');
    try {
      const res = await fetch(
        `${API}/produtos/empresa/${produto.empresaId}/${produto.idProduto}`,
        { method: 'DELETE', headers: { Authorization: `Bearer ${token}` } }
      );
      if (!res.ok) {
        const bd = await res.json().catch(() => ({}));
        throw new Error(bd.mensagem || `Erro ${res.status}`);
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
            <h2 className="font-bold text-slate-800 text-sm">Excluir produto</h2>
            <p className="text-slate-500 text-xs">Esta ação não pode ser desfeita.</p>
          </div>
        </div>
        <p className="text-slate-600 text-sm mb-4">
          Tem certeza que deseja excluir <strong>{produto.descricao || `produto #${produto.idProduto}`}</strong>?
        </p>
        {error && (
          <div className="mb-4 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl px-4 py-3">
            {error}
          </div>
        )}
        <div className="flex gap-3 justify-end">
          <button onClick={onClose} className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50 transition-colors">
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

// ─── ProdutosPage ─────────────────────────────────────────────────────────────
export default function ProdutosPage() {
  const { token, user } = useAuth();

  const isRestrito = !user?.hasRole('ADMINISTRADOR', 'SUPORTE');
  const canEdit    = user?.hasRole('ADMINISTRADOR', 'SUPORTE', 'EMPRESARIO', 'GERENTE', 'CAIXA');
  const canDelete  = user?.hasRole('ADMINISTRADOR', 'SUPORTE', 'EMPRESARIO', 'GERENTE', 'CAIXA');
  const canAdd     = true; // all roles per backend @PreAuthorize

  const [empresaId,       setEmpresaId]       = useState(isRestrito ? String(user?.empresaId ?? '') : '');
  const [empresasOptions, setEmpresasOptions] = useState([]);

  const [filters, setFilters] = useState({
    descricao:    '',
    codigoBarras: '',
    ncm:          '',
    idProduto:    '',
    ativo:        '',
  });

  const [result,      setResult]      = useState(null);
  const [page,        setPage]        = useState(0);
  const [sortCol,     setSortCol]     = useState(null);
  const [sortDir,     setSortDir]     = useState('asc');
  const [loading,     setLoading]     = useState(false);
  const [error,       setError]       = useState('');

  const [showCreate,   setShowCreate]   = useState(false);
  const [editTarget,   setEditTarget]   = useState(null);
  const [viewTarget,   setViewTarget]   = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [toast,        setToast]        = useState(null);
  const [exportOpen,   setExportOpen]   = useState(false);
  const [exporting,    setExporting]    = useState(false);
  const exportRef = useRef(null);

  const setF = (k) => (e) => setFilters((p) => ({ ...p, [k]: e.target.value }));

  useEffect(() => {
    if (isRestrito) return;
    fetch(`${API}/empresas?tamanho-pagina=100&pagina=0`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((r) => r.json())
      .then((data) => setEmpresasOptions(data.content ?? []))
      .catch(() => {});
  }, [isRestrito, token]); // eslint-disable-line react-hooks/exhaustive-deps

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

  function buildParams(pg, col, dir) {
    const params = new URLSearchParams({ pagina: pg, 'tamanho-pagina': 10 });
    if (filters.descricao)    params.set('descricao',        filters.descricao);
    if (filters.codigoBarras) params.set('codigo-de-barras', filters.codigoBarras);
    if (filters.ncm)          params.set('ncm',              filters.ncm);
    if (filters.idProduto)    params.set('id-produto',       filters.idProduto);
    if (filters.ativo !== '') params.set('ativo',            filters.ativo);
    if (col)                  { params.set('ordenar-por', col); params.set('direcao', dir); }
    return params;
  }

  async function buscarComSort(pg = 0, col = sortCol, dir = sortDir) {
    setError('');
    setLoading(true);
    try {
      const params = buildParams(pg, col, dir);
      let url;
      if (isRestrito) {
        url = `${API}/produtos/empresa/${user?.empresaId}?${params}`;
      } else {
        if (empresaId) params.set('empresa-id', empresaId);
        url = `${API}/produtos?${params}`;
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

  const buscar = (pg = 0) => buscarComSort(pg);

  useEffect(() => {
    if (isRestrito) buscarComSort(0);
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  useEffect(() => {
    function onClickOutside(e) {
      if (exportRef.current && !exportRef.current.contains(e.target)) setExportOpen(false);
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

      const PAGE_SIZE    = 100;
      const totalToFetch = Math.min(total, EXPORT_MAX_ROWS);
      const totalPages   = Math.ceil(totalToFetch / PAGE_SIZE);
      const allRows      = [];

      for (let pg = 0; pg < totalPages; pg++) {
        const params = buildParams(pg, sortCol, sortDir);
        params.set('tamanho-pagina', PAGE_SIZE);
        let url;
        if (isRestrito) {
          url = `${API}/produtos/empresa/${user?.empresaId}?${params}`;
        } else {
          if (empresaId) params.set('empresa-id', empresaId);
          url = `${API}/produtos?${params}`;
        }

        const res = await fetch(url, { headers: { Authorization: `Bearer ${token}` } });
        if (!res.ok) throw new Error(`Erro ao buscar dados (página ${pg + 1}): ${res.status}`);

        const data = await res.json();
        allRows.push(...(data.content ?? []));
        if ((data.content?.length ?? 0) < PAGE_SIZE) break;
      }

      if (allRows.length === 0) { showToast('Nenhum dado retornado para exportar.', 'error'); return; }

      if (format === 'csv') {
        exportCsvProduto(allRows, 'produtos');
        showToast(`CSV exportado — ${allRows.length} registro(s)`);
      } else {
        exportXlsxProduto(allRows, 'produtos');
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
        <div className={`fixed top-5 right-5 z-[100] flex items-center gap-3 px-5 py-3 rounded-xl shadow-xl text-white text-sm font-medium transition-all ${toast.type === 'error' ? 'bg-red-500' : 'bg-emerald-500'}`}>
          {toast.msg}
        </div>
      )}

      {showCreate && (
        <CreateModal
          token={token}
          isRestrito={isRestrito}
          userEmpresaId={user?.empresaId}
          empresasOptions={empresasOptions}
          onClose={() => setShowCreate(false)}
          onSaved={() => { setShowCreate(false); showToast('Produto cadastrado com sucesso!'); buscar(page); }}
        />
      )}

      {editTarget && (
        <EditModal
          produto={editTarget}
          token={token}
          onClose={() => setEditTarget(null)}
          onSaved={() => { setEditTarget(null); showToast('Produto atualizado com sucesso!'); buscar(page); }}
        />
      )}

      {viewTarget && (
        <ViewModal
          produto={viewTarget}
          onClose={() => setViewTarget(null)}
        />
      )}

      {deleteTarget && (
        <DeleteModal
          produto={deleteTarget}
          token={token}
          onClose={() => setDeleteTarget(null)}
          onDeleted={() => { setDeleteTarget(null); showToast('Produto excluído com sucesso!'); buscar(page); }}
        />
      )}

      {/* Cabeçalho */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-800 mb-1">Produtos</h1>
          <p className="text-slate-500 text-sm">Consulte e gerencie os produtos cadastrados no sistema.</p>
        </div>
        {canAdd && (
          <button
            onClick={() => setShowCreate(true)}
            className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-white text-sm font-semibold hover:scale-[1.02] transition-all flex-shrink-0"
            style={{ background: '#1D4ED8' }}
          >
            <Plus size={16} />
            Adicionar produto
          </button>
        )}
      </div>

      {/* Filtros */}
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
        <h2 className="font-semibold text-slate-700 text-sm mb-4">Filtros de pesquisa</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {!isRestrito && (
            <div className="sm:col-span-2">
              <label className={labelCls()}>Empresa</label>
              <select value={empresaId} onChange={(e) => setEmpresaId(e.target.value)} className={inputCls()}>
                <option value="">Todas as empresas</option>
                {empresasOptions.map((emp) => (
                  <option key={emp.id} value={String(emp.id)}>
                    {emp.razaoSocial}{emp.nomeFantasia ? ` — ${emp.nomeFantasia}` : ''}
                  </option>
                ))}
              </select>
            </div>
          )}
          <div>
            <label className={labelCls()}>Descrição</label>
            <input value={filters.descricao} onChange={setF('descricao')} className={inputCls()} placeholder="Nome do produto" />
          </div>
          <div>
            <label className={labelCls()}>Código de Barras</label>
            <input value={filters.codigoBarras} onChange={setF('codigoBarras')} className={inputCls()} placeholder="EAN-13…" />
          </div>
          <div>
            <label className={labelCls()}>NCM</label>
            <input type="number" value={filters.ncm} onChange={setF('ncm')} className={inputCls()} placeholder="Ex: 22021000" />
          </div>
          <div>
            <label className={labelCls()}>ID Produto</label>
            <input type="number" value={filters.idProduto} onChange={setF('idProduto')} className={inputCls()} placeholder="Ex: 1001" />
          </div>
          <div>
            <label className={labelCls()}>Status</label>
            <select value={filters.ativo} onChange={setF('ativo')} className={inputCls()}>
              <option value="">Todos</option>
              <option value="true">Ativo</option>
              <option value="false">Inativo</option>
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

      {/* Tabela */}
      {result && (
        <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
          <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
            <h2 className="font-bold text-slate-800 text-sm">
              Resultados
              <span className="ml-2 text-xs font-normal text-slate-400">
                {result.totalElements} produto(s) encontrado(s)
              </span>
            </h2>

            {user?.hasRole('ADMINISTRADOR', 'SUPORTE') && (
              <div className="relative" ref={exportRef}>
                <button
                  onClick={() => setExportOpen((v) => !v)}
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
              <p className="text-sm">Nenhum produto encontrado com esses filtros.</p>
            </div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="bg-slate-50 border-b border-slate-100">
                      {[
                        { label: 'ID',        col: 'idProduto',  px: 'px-6' },
                        { label: 'Descrição', col: 'descricao',  px: 'px-4' },
                        { label: 'NCM',       col: 'ncm',        px: 'px-4' },
                        { label: 'Unidade',   col: 'unidade',    px: 'px-4' },
                        { label: 'Preço',     col: 'preco',      px: 'px-4' },
                        { label: 'Estoque',   col: 'qtdEstoque', px: 'px-4' },
                        { label: 'Ativo',     col: 'ativo',      px: 'px-4' },
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
                    {items.map((prod) => (
                      <tr key={`${prod.empresaId}-${prod.idProduto}`} className="hover:bg-slate-50 transition-colors">
                        <td className="px-6 py-4 font-semibold text-slate-800 text-xs font-mono">{prod.idProduto}</td>
                        <td className="px-4 py-4 text-slate-700 text-xs max-w-[220px] truncate">{prod.descricao || '—'}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs font-mono">{prod.ncm || '—'}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs">{prod.unidade || '—'}</td>
                        <td className="px-4 py-4 text-slate-700 text-xs font-semibold">
                          {prod.preco != null ? `R$ ${Number(prod.preco).toFixed(2)}` : '—'}
                        </td>
                        <td className="px-4 py-4 text-slate-500 text-xs">{prod.qtdEstoque ?? '—'}</td>
                        <td className="px-4 py-4 text-xs">
                          <span className={`font-semibold px-2.5 py-1 rounded-full text-xs ${prod.ativo ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-600'}`}>
                            {prod.ativo ? 'Sim' : 'Não'}
                          </span>
                        </td>
                        <td className="px-4 py-4 text-right">
                          <div className="flex items-center justify-end gap-2">
                            {canEdit ? (
                              <button
                                onClick={() => setEditTarget(prod)}
                                className="p-1.5 rounded-lg text-slate-400 hover:text-vulpes-orange hover:bg-vulpes-orange/10 transition-colors"
                                title="Editar"
                              >
                                <Pencil size={15} />
                              </button>
                            ) : (
                              <button
                                onClick={() => setViewTarget(prod)}
                                className="p-1.5 rounded-lg text-slate-400 hover:text-blue-500 hover:bg-blue-50 transition-colors"
                                title="Visualizar"
                              >
                                <Eye size={15} />
                              </button>
                            )}
                            {canDelete && (
                              <button
                                onClick={() => setDeleteTarget(prod)}
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
