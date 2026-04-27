import { useState, useEffect, useRef } from 'react';
import {
  Search, Trash2, X, AlertTriangle, Plus, ChevronDown, ChevronUp,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const API = import.meta.env.VITE_API_URL ?? '';

// ─── Helpers ──────────────────────────────────────────────────────────────────

const UF_OPTIONS = [
  'AC','AL','AP','AM','BA','CE','DF','ES','GO','MA',
  'MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN',
  'RS','RO','RR','SC','SP','SE','TO',
];

// CST ICMS — Regime Normal (Tabela A + B do Ajuste SINIEF 07/05)
const CST_ICMS_OPTIONS = [
  { value: '00', label: '00 – Tributada integralmente' },
  { value: '10', label: '10 – Tributada e com ST' },
  { value: '20', label: '20 – Com redução de base de cálculo' },
  { value: '30', label: '30 – Isenta/não tributada e com ST' },
  { value: '40', label: '40 – Isenta' },
  { value: '41', label: '41 – Não tributada' },
  { value: '50', label: '50 – Suspensão' },
  { value: '51', label: '51 – Diferimento' },
  { value: '60', label: '60 – ICMS cobrado anteriormente por ST' },
  { value: '70', label: '70 – Com redução de BC e cobrança de ST' },
  { value: '90', label: '90 – Outras' },
];

// CSOSN — Simples Nacional (Resolução CGSN 94/2011)
const CSOSN_OPTIONS = [
  { value: '101', label: '101 – Tributada pelo SN com permissão de crédito' },
  { value: '102', label: '102 – Tributada pelo SN sem permissão de crédito' },
  { value: '103', label: '103 – Isenção do ICMS no SN para faixa de receita bruta' },
  { value: '201', label: '201 – SN com crédito e com cobrança de ST' },
  { value: '202', label: '202 – SN sem crédito e com cobrança de ST' },
  { value: '203', label: '203 – Isenção no SN para faixa de receita bruta e com ST' },
  { value: '300', label: '300 – Imune' },
  { value: '400', label: '400 – Não tributada pelo Simples Nacional' },
  { value: '500', label: '500 – ICMS cobrado anteriormente por ST ou antecipação' },
  { value: '900', label: '900 – Outros' },
];

// CST PIS / CST COFINS — Tabela 4.3.9 do Manual da NF-e/NFC-e
const CST_PIS_COFINS_OPTIONS = [
  { value: '01', label: '01 – Operação tributável (alíquota normal cumulativo/não cumulativo)' },
  { value: '02', label: '02 – Operação tributável (alíquota diferenciada)' },
  { value: '03', label: '03 – Operação tributável (alíquota por unidade de produto)' },
  { value: '04', label: '04 – Operação tributável (monofásica / alíquota zero)' },
  { value: '05', label: '05 – Operação tributável por substituição tributária' },
  { value: '06', label: '06 – Operação tributável (alíquota zero)' },
  { value: '07', label: '07 – Operação isenta da contribuição' },
  { value: '08', label: '08 – Operação sem incidência da contribuição' },
  { value: '09', label: '09 – Operação com suspensão da contribuição' },
  { value: '49', label: '49 – Outras operações de saída' },
  { value: '99', label: '99 – Outras operações' },
];

const REGIMES = [
  { value: 'SIMPLES_NACIONAL',         label: 'Simples Nacional'              },
  { value: 'SIMPLES_EXCESSO_SUBLIMITE',label: 'Simples Nacional – Excesso'    },
  { value: 'REGIME_NORMAL',            label: 'Regime Normal (Lucro Presumido/Real)' },
];

function labelRegime(v) {
  return REGIMES.find(r => r.value === v)?.label ?? v ?? '—';
}

function inputCls(extra = '') {
  return `w-full border border-slate-200 rounded-xl px-3 py-2 text-sm text-slate-700 focus:outline-none focus:border-vulpes-orange focus:ring-2 focus:ring-vulpes-orange/20 transition-all bg-white ${extra}`;
}
function errCls(k, errors) {
  return errors?.[k]
    ? 'w-full border border-red-400 rounded-xl px-3 py-2 text-sm text-slate-700 focus:outline-none focus:border-red-400 focus:ring-2 focus:ring-red-200 transition-all bg-white'
    : inputCls();
}
function labelCls() { return 'block text-sm font-medium text-slate-500 mb-1'; }

function maskPct(v) {
  let s = String(v).replace(/[^\d,]/g, '');
  const first = s.indexOf(',');
  if (first !== -1) s = s.slice(0, first + 1) + s.slice(first + 1).replace(/,/g, '');
  const parts = s.split(',');
  if (parts[1]?.length > 2) s = parts[0] + ',' + parts[1].slice(0, 2);
  return s;
}

function parsePct(v) {
  if (v === '' || v == null) return null;
  const n = Number(String(v).replace(',', '.'));
  return isNaN(n) ? null : n;
}

// Conjuntos de CST/CSOSN para regras de cross-field
const ICMS_ZERO_CST    = ['40','41','50','51','60'];
const ICMS_ZERO_CSOSN  = ['103','300','400','500'];
const REDBC_CST        = ['20','70','90'];
const REDBC_CSOSN      = ['900'];
const ST_ANT_CST       = ['60'];
const ST_ANT_CSOSN     = ['500'];
const PIS_ZERO_CST     = ['04','06','07','08','09'];

function disabledCls(disabled) {
  return disabled ? 'bg-slate-50 opacity-50 cursor-not-allowed' : '';
}

function Field({ k, label, required, errors, hint, children }) {
  return (
    <div>
      <label className={labelCls()}>
        {label}{required && <span className="text-red-400 ml-0.5">*</span>}
        {hint && <span className="ml-1.5 text-slate-400 font-normal text-xs">({hint})</span>}
      </label>
      {children}
      {errors?.[k] && <p className="mt-1 text-red-500 text-xs">{errors[k]}</p>}
    </div>
  );
}

// ─── Modal de Cadastro ────────────────────────────────────────────────────────
function CreateModal({ token, isRestrito, userEmpresaId, empresasOptions, onClose, onSaved }) {
  const [modalEmpresaId, setModalEmpresaId] = useState(isRestrito ? String(userEmpresaId ?? '') : '');
  const [produtos,       setProdutos]       = useState([]);
  const [loadingProd,    setLoadingProd]    = useState(false);

  const EMPTY = {
    idProduto: '', nome: '', uf: '', cfop: '',
    cstIcms: '', csosnIcms: '', aliquotaIcms: '', pFcp: '', pRedBc: '',
    temStAnterior: false,
    cstPis: '', aliquotaPis: '', cstCofins: '', aliquotaCofins: '',
    regimeTributarioEmpresa: '',
  };
  const [form,     setForm]     = useState(EMPTY);
  const [loading,  setLoading]  = useState(false);
  const [errors,   setErrors]   = useState({});
  const [apiError, setApiError] = useState('');

  useEffect(() => {
    if (!modalEmpresaId) { setProdutos([]); return; }
    setLoadingProd(true);
    fetch(`${API}/produtos/empresa/${modalEmpresaId}?tamanho-pagina=200&pagina=0&ativo=true`, {
      headers: { Authorization: `Bearer ${token}` },
    }).then(r => r.json()).then(d => setProdutos(d.content ?? [])).catch(() => setProdutos([])).finally(() => setLoadingProd(false));
  }, [modalEmpresaId, token]);

  // Flags derivadas do estado atual do formulário
  const isSimples = ['SIMPLES_NACIONAL','SIMPLES_EXCESSO_SUBLIMITE'].includes(form.regimeTributarioEmpresa);
  const isNormal  = form.regimeTributarioEmpresa === 'REGIME_NORMAL';
  const cstDisabled          = isSimples;
  const csosnDisabled        = isNormal;
  const aliquotaIcmsDisabled = (form.cstIcms && ICMS_ZERO_CST.includes(form.cstIcms))
                              || (form.csosnIcms && ICMS_ZERO_CSOSN.includes(form.csosnIcms));
  const pRedBcDisabled       = !(form.cstIcms && REDBC_CST.includes(form.cstIcms))
                              && !(form.csosnIcms && REDBC_CSOSN.includes(form.csosnIcms));
  const fcpDisabled          = !!aliquotaIcmsDisabled;
  const stAntDisabled        = !ST_ANT_CST.includes(form.cstIcms) && !ST_ANT_CSOSN.includes(form.csosnIcms);
  const aliquotaPisDisabled  = PIS_ZERO_CST.includes(form.cstPis);
  const aliquotaCofinsDisabled = PIS_ZERO_CST.includes(form.cstCofins);

  function set(k, v) {
    setForm(p => {
      const next = { ...p, [k]: v };
      if (k === 'regimeTributarioEmpresa') {
        const simples = ['SIMPLES_NACIONAL','SIMPLES_EXCESSO_SUBLIMITE'].includes(v);
        if (simples) next.cstIcms = '';
        if (v === 'REGIME_NORMAL') next.csosnIcms = '';
      }
      if (k === 'cstIcms' && ICMS_ZERO_CST.includes(v))   next.aliquotaIcms = '';
      if (k === 'csosnIcms' && ICMS_ZERO_CSOSN.includes(v)) next.aliquotaIcms = '';
      if (k === 'cstPis' && PIS_ZERO_CST.includes(v))     next.aliquotaPis = '0';
      if (k === 'cstCofins' && PIS_ZERO_CST.includes(v))  next.aliquotaCofins = '0';
      if (k === 'cstIcms' && !ST_ANT_CST.includes(v))     next.temStAnterior = false;
      if (k === 'csosnIcms' && !ST_ANT_CSOSN.includes(v)) next.temStAnterior = false;
      return next;
    });
  }

  function validate() {
    const e = {};
    if (!modalEmpresaId)               e.empresa   = 'Selecione uma empresa';
    if (!form.idProduto)               e.idProduto = 'Selecione um produto';
    if (!form.nome?.trim())            e.nome      = 'Nome é obrigatório';
    if (!form.uf?.trim())              e.uf        = 'UF é obrigatória';
    if (!form.cfop?.trim())            e.cfop      = 'CFOP é obrigatório';
    if (!form.regimeTributarioEmpresa) e.regimeTributarioEmpresa = 'Regime tributário é obrigatório';

    // CST ICMS vs CSOSN
    if (form.cstIcms && form.csosnIcms)
      e.cstIcms = 'Preencha apenas CST ICMS (Regime Normal) ou CSOSN (Simples), não ambos';
    if (isSimples && form.cstIcms)
      e.cstIcms = 'Simples Nacional utiliza CSOSN, não CST ICMS';
    if (isNormal && form.csosnIcms)
      e.csosnIcms = 'Regime Normal utiliza CST ICMS, não CSOSN';

    // Alíquota ICMS indevida
    if (aliquotaIcmsDisabled && parsePct(form.aliquotaIcms) > 0)
      e.aliquotaIcms = 'Este CST/CSOSN não admite alíquota de ICMS (operação isenta/suspensa/ST anterior)';

    // Redução de BC indevida
    if (pRedBcDisabled && parsePct(form.pRedBc) > 0)
      e.pRedBc = 'Redução de BC só se aplica a CST 20, 70, 90 ou CSOSN 900';

    // FCP indevido
    if (fcpDisabled && parsePct(form.pFcp) > 0)
      e.pFcp = '% FCP não se aplica a operações sem alíquota de ICMS';

    // PIS/COFINS alíquota indevida
    if (aliquotaPisDisabled && parsePct(form.aliquotaPis) > 0)
      e.aliquotaPis = 'Este CST PIS não admite alíquota (isento/não tributado/suspenso)';
    if (aliquotaCofinsDisabled && parsePct(form.aliquotaCofins) > 0)
      e.aliquotaCofins = 'Este CST COFINS não admite alíquota (isento/não tributado/suspenso)';

    return e;
  }

  async function handleSubmit(ev) {
    ev.preventDefault();
    setApiError('');
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setLoading(true);
    try {
      const body = {
        idProduto:               Number(form.idProduto),
        nome:                    form.nome.trim(),
        uf:                      form.uf.trim().toUpperCase(),
        cfop:                    form.cfop.trim(),
        cstIcms:                 form.cstIcms || null,
        csosnIcms:               form.csosnIcms || null,
        aliquotaIcms:            parsePct(form.aliquotaIcms),
        pFcp:                    parsePct(form.pFcp),
        pRedBc:                  parsePct(form.pRedBc),
        temStAnterior:           form.temStAnterior,
        cstPis:                  form.cstPis || null,
        aliquotaPis:             parsePct(form.aliquotaPis),
        cstCofins:               form.cstCofins || null,
        aliquotaCofins:          parsePct(form.aliquotaCofins),
        regimeTributarioEmpresa: form.regimeTributarioEmpresa,
      };
      const res = await fetch(`${API}/empresa/${modalEmpresaId}/produto-tributacao`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
        body: JSON.stringify(body),
      });
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
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[92vh] flex flex-col">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <div>
            <h2 className="font-bold text-slate-800">Nova Tributação</h2>
            <p className="text-xs text-slate-400 mt-0.5">Campos com <span className="text-red-400">*</span> são obrigatórios</p>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700"><X size={20} /></button>
        </div>

        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6 space-y-6">
          {apiError && (
            <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl px-4 py-3">
              <AlertTriangle size={14} className="mt-0.5 flex-shrink-0" />{apiError}
            </div>
          )}
          {Object.keys(errors).length > 0 && (
            <div className="bg-amber-50 border border-amber-200 rounded-xl px-4 py-3">
              <p className="text-amber-700 text-xs font-semibold mb-1 flex items-center gap-1.5"><AlertTriangle size={13} />Corrija os campos:</p>
              <ul className="list-disc list-inside space-y-0.5">
                {Object.values(errors).map((msg, i) => <li key={i} className="text-amber-700 text-xs">{msg}</li>)}
              </ul>
            </div>
          )}

          {/* Empresa + Produto */}
          <section>
            <p className="text-sm font-semibold text-slate-400 uppercase tracking-wide mb-3">Vínculo</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {!isRestrito && (
                <Field k="empresa" label="Empresa" required errors={errors}>
                  <select value={modalEmpresaId} onChange={e => setModalEmpresaId(e.target.value)} className={errCls('empresa', errors)}>
                    <option value="">Selecione uma empresa…</option>
                    {empresasOptions.map(emp => (
                      <option key={emp.id} value={String(emp.id)}>{emp.razaoSocial}{emp.nomeFantasia ? ` — ${emp.nomeFantasia}` : ''}</option>
                    ))}
                  </select>
                </Field>
              )}
              <Field k="idProduto" label="Produto" required errors={errors}>
                <select value={form.idProduto} onChange={e => set('idProduto', e.target.value)} disabled={!modalEmpresaId || loadingProd} className={errCls('idProduto', errors)}>
                  <option value="">{loadingProd ? 'Carregando…' : modalEmpresaId ? 'Selecione um produto…' : 'Selecione uma empresa primeiro'}</option>
                  {produtos.map(p => (
                    <option key={p.idProduto} value={String(p.idProduto)}>#{p.idProduto} — {p.descricao}</option>
                  ))}
                </select>
              </Field>
            </div>
          </section>

          {/* Identificação */}
          <section>
            <p className="text-sm font-semibold text-slate-400 uppercase tracking-wide mb-3">Identificação</p>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <div className="sm:col-span-2">
                <Field k="nome" label="Nome / Descrição da Tributação" required errors={errors}>
                  <input value={form.nome} onChange={e => set('nome', e.target.value)} className={errCls('nome', errors)} placeholder="Ex: Venda SP – Simples Nacional" />
                </Field>
              </div>
              <Field k="uf" label="UF" required errors={errors}>
                <select value={form.uf} onChange={e => set('uf', e.target.value)} className={errCls('uf', errors)}>
                  <option value="">Selecione…</option>
                  {UF_OPTIONS.map(uf => <option key={uf} value={uf}>{uf}</option>)}
                </select>
              </Field>
              <Field k="cfop" label="CFOP" required errors={errors}>
                <input value={form.cfop} onChange={e => set('cfop', e.target.value)} className={errCls('cfop', errors)} placeholder="5102" />
              </Field>
              <div className="sm:col-span-2">
                <Field k="regimeTributarioEmpresa" label="Regime Tributário" required errors={errors}>
                  <select value={form.regimeTributarioEmpresa} onChange={e => set('regimeTributarioEmpresa', e.target.value)} className={errCls('regimeTributarioEmpresa', errors)}>
                    <option value="">Selecione…</option>
                    {REGIMES.map(r => <option key={r.value} value={r.value}>{r.label}</option>)}
                  </select>
                </Field>
              </div>
            </div>
          </section>

          {/* ICMS */}
          <section>
            <p className="text-sm font-semibold text-slate-400 uppercase tracking-wide mb-3">ICMS</p>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
              <div className="sm:col-span-2">
                <Field k="cstIcms" label="CST ICMS" errors={errors} hint="Regime Normal">
                  <select
                    value={form.cstIcms}
                    onChange={e => set('cstIcms', e.target.value)}
                    disabled={cstDisabled}
                    className={inputCls(disabledCls(cstDisabled))}
                  >
                    <option value="">{cstDisabled ? 'N/A – use CSOSN' : 'Selecione…'}</option>
                    {CST_ICMS_OPTIONS.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
                  </select>
                </Field>
              </div>
              <div className="sm:col-span-2">
                <Field k="csosnIcms" label="CSOSN ICMS" errors={errors} hint="Simples Nacional">
                  <select
                    value={form.csosnIcms}
                    onChange={e => set('csosnIcms', e.target.value)}
                    disabled={csosnDisabled}
                    className={inputCls(disabledCls(csosnDisabled))}
                  >
                    <option value="">{csosnDisabled ? 'N/A – use CST ICMS' : 'Selecione…'}</option>
                    {CSOSN_OPTIONS.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
                  </select>
                </Field>
              </div>
              <Field k="aliquotaIcms" label="Alíquota ICMS (%)" errors={errors}>
                <input
                  value={form.aliquotaIcms}
                  onChange={e => set('aliquotaIcms', maskPct(e.target.value))}
                  disabled={!!aliquotaIcmsDisabled}
                  className={inputCls(disabledCls(aliquotaIcmsDisabled))}
                  placeholder={aliquotaIcmsDisabled ? '—' : '12,00'}
                />
              </Field>
              <Field k="pFcp" label="% FCP" errors={errors}>
                <input
                  value={form.pFcp}
                  onChange={e => set('pFcp', maskPct(e.target.value))}
                  disabled={fcpDisabled}
                  className={inputCls(disabledCls(fcpDisabled))}
                  placeholder={fcpDisabled ? '—' : '2,00'}
                />
              </Field>
              <Field k="pRedBc" label="% Red. BC" errors={errors}>
                <input
                  value={form.pRedBc}
                  onChange={e => set('pRedBc', maskPct(e.target.value))}
                  disabled={pRedBcDisabled}
                  className={inputCls(disabledCls(pRedBcDisabled))}
                  placeholder={pRedBcDisabled ? '—' : '33,33'}
                />
              </Field>
              <div className={`flex items-end pb-2 gap-2 ${stAntDisabled ? 'opacity-40 pointer-events-none' : ''}`}>
                <input
                  type="checkbox" id="temSt"
                  checked={form.temStAnterior}
                  onChange={e => set('temStAnterior', e.target.checked)}
                  disabled={stAntDisabled}
                  className="w-4 h-4 rounded border-slate-300 accent-orange-500"
                />
                <label htmlFor="temSt" className="text-sm text-slate-600 cursor-pointer select-none">
                  ST anterior
                  <span className="block text-xs text-slate-400">CST 60 / CSOSN 500</span>
                </label>
              </div>
            </div>
          </section>

          {/* PIS / COFINS */}
          <section>
            <p className="text-sm font-semibold text-slate-400 uppercase tracking-wide mb-3">PIS / COFINS</p>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
              <div className="sm:col-span-2">
                <Field k="cstPis" label="CST PIS" errors={errors}>
                  <select value={form.cstPis} onChange={e => set('cstPis', e.target.value)} className={inputCls()}>
                    <option value="">Selecione…</option>
                    {CST_PIS_COFINS_OPTIONS.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
                  </select>
                </Field>
              </div>
              <Field k="aliquotaPis" label="Alíquota PIS (%)" errors={errors}>
                <input
                  value={form.aliquotaPis}
                  onChange={e => set('aliquotaPis', maskPct(e.target.value))}
                  disabled={aliquotaPisDisabled}
                  className={inputCls(disabledCls(aliquotaPisDisabled))}
                  placeholder={aliquotaPisDisabled ? '—' : '0,65'}
                />
              </Field>
              <div className="sm:col-span-2">
                <Field k="cstCofins" label="CST COFINS" errors={errors}>
                  <select value={form.cstCofins} onChange={e => set('cstCofins', e.target.value)} className={inputCls()}>
                    <option value="">Selecione…</option>
                    {CST_PIS_COFINS_OPTIONS.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
                  </select>
                </Field>
              </div>
              <Field k="aliquotaCofins" label="Alíquota COFINS (%)" errors={errors}>
                <input
                  value={form.aliquotaCofins}
                  onChange={e => set('aliquotaCofins', maskPct(e.target.value))}
                  disabled={aliquotaCofinsDisabled}
                  className={inputCls(disabledCls(aliquotaCofinsDisabled))}
                  placeholder={aliquotaCofinsDisabled ? '—' : '3,00'}
                />
              </Field>
            </div>
          </section>
        </form>

        <div className="flex items-center justify-end gap-3 px-6 py-4 border-t border-slate-100">
          <button type="button" onClick={onClose} className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50">Cancelar</button>
          <button onClick={handleSubmit} disabled={loading}
            className="flex items-center gap-2 px-5 py-2 rounded-xl text-white text-sm font-semibold disabled:opacity-60 hover:scale-[1.02] transition-all"
            style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}>
            <Plus size={15} />
            {loading ? 'Salvando…' : 'Salvar Tributação'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── Modal de confirmação de exclusão ─────────────────────────────────────────
function DeleteModal({ item, onClose, onConfirm, loading }) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm p-6">
        <div className="flex items-center gap-3 mb-4">
          <div className="w-10 h-10 rounded-xl bg-red-50 flex items-center justify-center">
            <Trash2 size={18} className="text-red-500" />
          </div>
          <h2 className="font-bold text-slate-800">Excluir Tributação</h2>
        </div>
        <p className="text-slate-600 text-sm mb-2">Tem certeza que deseja excluir a tributação abaixo?</p>
        <div className="bg-slate-50 rounded-xl px-4 py-3 mb-5 text-sm">
          <p className="font-semibold text-slate-700">{item.nome ?? `UF: ${item.uf}`}</p>
          <p className="text-slate-500 text-xs mt-0.5">{item.descricaoProduto} — CFOP {item.cfop}</p>
        </div>
        <div className="flex gap-3">
          <button onClick={onClose} className="flex-1 px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50">Cancelar</button>
          <button onClick={onConfirm} disabled={loading}
            className="flex-1 px-4 py-2 rounded-xl bg-red-500 text-white text-sm font-semibold hover:bg-red-600 disabled:opacity-60 transition-colors">
            {loading ? 'Excluindo…' : 'Excluir'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── TributacaoPage ───────────────────────────────────────────────────────────
export default function TributacaoPage() {
  const { token, user } = useAuth();

  const isRestrito   = !user?.hasRole('ADMINISTRADOR', 'SUPORTE');
  const canManage    = user?.hasRole('ADMINISTRADOR', 'SUPORTE', 'EMPRESARIO', 'GERENTE');

  const [empresaId,       setEmpresaId]       = useState(isRestrito ? String(user?.empresaId ?? '') : '');
  const [empresasOptions, setEmpresasOptions] = useState([]);
  const [items,           setItems]           = useState([]);
  const [loading,         setLoading]         = useState(false);
  const [error,           setError]           = useState('');
  const [filterNome,      setFilterNome]      = useState('');
  const [filterProduto,   setFilterProduto]   = useState('');

  const [showCreate,      setShowCreate]      = useState(false);
  const [deleteTarget,    setDeleteTarget]    = useState(null);
  const [deleteLoading,   setDeleteLoading]   = useState(false);
  const [toast,           setToast]           = useState(null);

  function showToast(msg, type = 'success') {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3500);
  }

  // Carrega empresas para ADMIN/SUPORTE
  useEffect(() => {
    if (isRestrito) return;
    fetch(`${API}/empresas?tamanho-pagina=100&pagina=0`, { headers: { Authorization: `Bearer ${token}` } })
      .then(r => r.json()).then(d => setEmpresasOptions(d.content ?? [])).catch(() => {});
  }, [isRestrito, token]);

  async function buscar(eid) {
    const id = eid ?? empresaId;
    setError(''); setLoading(true);
    try {
      const url = id
        ? `${API}/empresa/${id}/produto-tributacao`
        : `${API}/produto-tributacao`;
      const res = await fetch(url, { headers: { Authorization: `Bearer ${token}` } });
      if (!res.ok) throw new Error(`Erro ${res.status}`);
      const data = await res.json();
      setItems(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message); setItems([]);
    } finally {
      setLoading(false);
    }
  }

  // Auto-carrega ao montar para usuários restritos (empresa já fixada)
  useEffect(() => {
    if (empresaId) buscar(empresaId);
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  // Auto-carrega quando ADMIN/SUPORTE seleciona uma empresa
  function handleEmpresaChange(id) {
    setEmpresaId(id);
    setItems([]);
    if (id) buscar(id);
  }

  async function handleDelete() {
    if (!deleteTarget) return;
    setDeleteLoading(true);
    try {
      const res = await fetch(`${API}/empresa/${empresaId}/produto-tributacao/${deleteTarget.id}`, {
        method: 'DELETE', headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error(`Erro ${res.status}`);
      showToast('Tributação excluída com sucesso!');
      setDeleteTarget(null);
      buscar();
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setDeleteLoading(false);
    }
  }

  const filteredItems = items.filter(it => {
    const nomeOk    = !filterNome    || (it.nome ?? '').toLowerCase().includes(filterNome.toLowerCase());
    const produtoOk = !filterProduto || (it.descricaoProduto ?? '').toLowerCase().includes(filterProduto.toLowerCase());
    return nomeOk && produtoOk;
  });

  return (
    <div className="space-y-6">
      {/* Toast */}
      {toast && (
        <div className={`fixed top-5 right-5 z-[100] flex items-center gap-3 px-5 py-3 rounded-xl shadow-xl text-white text-sm font-medium ${toast.type === 'error' ? 'bg-red-500' : 'bg-emerald-500'}`}>
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
          onSaved={() => { setShowCreate(false); showToast('Tributação cadastrada com sucesso!'); buscar(); }}
        />
      )}

      {deleteTarget && (
        <DeleteModal
          item={deleteTarget}
          loading={deleteLoading}
          onClose={() => setDeleteTarget(null)}
          onConfirm={handleDelete}
        />
      )}

      {/* Cabeçalho */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-800 mb-1">Tributação de Produtos</h1>
          <p className="text-slate-500 text-sm">Gerencie as tributações fiscais por produto e UF.</p>
        </div>
        {canManage && (
          <button
            onClick={() => setShowCreate(true)}
            className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-white text-sm font-semibold hover:scale-[1.02] transition-all flex-shrink-0"
            style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}
          >
            <Plus size={16} /> Nova Tributação
          </button>
        )}
      </div>

      {/* Filtros */}
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
        <h2 className="font-semibold text-slate-700 text-sm mb-4">Filtros de pesquisa</h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          {!isRestrito && (
            <div>
              <label className="block text-sm font-medium text-slate-500 mb-1">Empresa</label>
              <select value={empresaId} onChange={e => handleEmpresaChange(e.target.value)} className={inputCls()}>
                <option value="">Selecione uma empresa…</option>
                {empresasOptions.map(emp => (
                  <option key={emp.id} value={String(emp.id)}>
                    {emp.razaoSocial}{emp.nomeFantasia ? ` — ${emp.nomeFantasia}` : ''}
                  </option>
                ))}
              </select>
            </div>
          )}
          <div>
            <label className="block text-sm font-medium text-slate-500 mb-1">Produto</label>
            <input value={filterProduto} onChange={e => setFilterProduto(e.target.value)} className={inputCls()} placeholder="Filtrar por produto…" />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-500 mb-1">Nome da tributação</label>
            <input value={filterNome} onChange={e => setFilterNome(e.target.value)} className={inputCls()} placeholder="Filtrar por nome…" />
          </div>
        </div>

        {error && <p className="mt-3 text-red-500 text-xs flex items-center gap-1"><AlertTriangle size={12} /> {error}</p>}
        <div className="flex justify-end mt-5">
          <button onClick={() => buscar()} disabled={loading}
            className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-white text-sm font-semibold disabled:opacity-60 hover:scale-[1.02] transition-all"
            style={{ background: 'linear-gradient(135deg, #032A47, #FE600C)' }}>
            <Search size={15} />
            {loading ? 'Buscando…' : 'Pesquisar'}
          </button>
        </div>
      </div>

      {/* Tabela */}
      {items.length > 0 && (
        <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
          <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
            <h2 className="font-bold text-slate-800 text-sm">
              Resultados
              <span className="ml-2 text-xs font-normal text-slate-400">{filteredItems.length} tributação(ões)</span>
            </h2>
          </div>

          {filteredItems.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-12 text-slate-400">
              <Search size={32} className="mb-3 opacity-40" />
              <p className="text-sm">Nenhuma tributação encontrada com esses filtros.</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="bg-slate-50 border-b border-slate-100">
                    {['Nome', 'Produto', 'UF', 'CFOP', 'Regime', 'CST ICMS', 'Alíq. ICMS', 'Ações'].map(h => (
                      <th key={h} className="text-left px-4 py-3.5 text-xs font-semibold text-slate-500 whitespace-nowrap first:px-6">{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {filteredItems.map(it => (
                    <tr key={it.id} className="hover:bg-slate-50 transition-colors">
                      <td className="px-6 py-4 text-slate-700 text-sm font-medium">{it.nome ?? '—'}</td>
                      <td className="px-4 py-4 text-slate-600 text-xs max-w-[180px] truncate">{it.descricaoProduto}</td>
                      <td className="px-4 py-4 text-center">
                        <span className="text-xs font-bold bg-slate-100 text-slate-600 px-2 py-0.5 rounded-md">{it.uf}</span>
                      </td>
                      <td className="px-4 py-4 text-slate-600 text-xs font-mono">{it.cfop}</td>
                      <td className="px-4 py-4 text-slate-500 text-xs">{labelRegime(it.regimeTributarioEmpresa)}</td>
                      <td className="px-4 py-4 text-slate-500 text-xs font-mono">{it.cstIcms ?? it.csosnIcms ?? '—'}</td>
                      <td className="px-4 py-4 text-slate-500 text-xs">{it.aliquotaIcms != null ? `${it.aliquotaIcms}%` : '—'}</td>
                      <td className="px-4 py-4 text-right">
                        {canManage && (
                          <button onClick={() => setDeleteTarget(it)}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-red-500 hover:bg-red-50 transition-colors" title="Excluir">
                            <Trash2 size={15} />
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
