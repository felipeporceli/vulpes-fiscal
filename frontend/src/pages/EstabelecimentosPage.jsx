import { useState, useEffect, useRef } from 'react';
import {
  Search, Pencil, Trash2, X, ChevronLeft, ChevronRight,
  AlertTriangle, ChevronUp, ChevronDown, ChevronsUpDown,
  Plus, Eye, Download, FileText, FileSpreadsheet,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import {
  exportCsvEstabelecimento,
  exportXlsxEstabelecimento,
  EXPORT_MAX_ROWS,
} from '../utils/exportUtils';

const API = import.meta.env.VITE_API_URL ?? '';

// ─── Helpers ─────────────────────────────────────────────────────────────────

function formatCnpj(v) {
  if (!v) return '—';
  const d = v.replace(/\D/g, '');
  return d.length === 14
    ? d.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5')
    : v;
}

function maskCnpj(raw) {
  let v = raw.replace(/\D/g, '').slice(0, 14);
  if (v.length > 12)     v = v.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{0,2})/, '$1.$2.$3/$4-$5');
  else if (v.length > 8) v = v.replace(/(\d{2})(\d{3})(\d{3})(\d{0,4})/, '$1.$2.$3/$4');
  else if (v.length > 5) v = v.replace(/(\d{2})(\d{3})(\d{0,3})/, '$1.$2.$3');
  else if (v.length > 2) v = v.replace(/(\d{2})(\d{0,3})/, '$1.$2');
  return v;
}

function isValidCnpj(raw) {
  const n = raw.replace(/\D/g, '');
  if (n.length !== 14) return false;
  if (/^(\d)\1+$/.test(n)) return false;
  const calc = (base) => {
    let s = 0, w = base.length - 7;
    for (let i = 0; i < base.length; i++) { s += parseInt(base[i]) * w--; if (w < 2) w = 9; }
    const r = s % 11; return r < 2 ? 0 : 11 - r;
  };
  return calc(n.slice(0, 12)) === parseInt(n[12]) && calc(n.slice(0, 13)) === parseInt(n[13]);
}

function maskCep(raw) {
  let v = raw.replace(/\D/g, '').slice(0, 8);
  if (v.length > 5) v = v.replace(/(\d{5})(\d{0,3})/, '$1-$2');
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

const STATUS_OPTIONS = [
  { value: '',        label: 'Todos'   },
  { value: 'ATIVA',   label: 'Ativa'   },
  { value: 'INATIVA', label: 'Inativa' },
  { value: 'INAPTA',  label: 'Inapta'  },
  { value: 'BAIXADA', label: 'Baixada' },
];

const statusColor = {
  ATIVA:   'bg-emerald-100 text-emerald-700',
  INATIVA: 'bg-red-100     text-red-600',
  INAPTA:  'bg-amber-100   text-amber-700',
  BAIXADA: 'bg-slate-100   text-slate-500',
};

function SortIcon({ col, sortCol, sortDir }) {
  if (sortCol !== col) return <ChevronsUpDown size={12} className="ml-1 opacity-30 inline-block" />;
  return sortDir === 'asc'
    ? <ChevronUp   size={12} className="ml-1 text-vulpes-orange inline-block" />
    : <ChevronDown size={12} className="ml-1 text-vulpes-orange inline-block" />;
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
  const [modalEmpresaId, setModalEmpresaId] = useState(
    isRestrito ? String(userEmpresaId ?? '') : ''
  );

  const EMPTY = {
    nomeFantasia:      '',
    cnpj:              '',
    inscricaoEstadual: '',
    inscricaoMunicipal:'',
    logradouro:        '',
    numero:            '',
    complemento:       '',
    bairro:            '',
    cidade:            '',
    municipioId:       '',
    cep:               '',
    paisId:            '1058',
    estado:            '',
    pais:              'Brasil',
    codUf:             '',
    status:            'ATIVA',
    matriz:            false,
    telefone:          '',
    email:             '',
    dataAbertura:      '',
  };
  const [form,     setForm]     = useState(EMPTY);
  const [loading,  setLoading]  = useState(false);
  const [errors,   setErrors]   = useState({});
  const [apiError, setApiError] = useState('');

  const set = (k) => (e) => setForm((p) => ({ ...p, [k]: e.target.value }));

  function validate() {
    const e = {};
    if (!modalEmpresaId && !isRestrito) e.empresa = 'Selecione uma empresa';
    if (!form.cnpj.trim())
      e.cnpj = 'CNPJ é obrigatório';
    else if (form.cnpj.replace(/\D/g, '').length !== 14)
      e.cnpj = 'CNPJ incompleto';
    else if (!isValidCnpj(form.cnpj))
      e.cnpj = 'CNPJ inválido';
    if (!form.inscricaoEstadual.trim()) e.inscricaoEstadual = 'Inscrição Estadual é obrigatória';
    if (!form.logradouro.trim())        e.logradouro        = 'Logradouro é obrigatório';
    if (!form.numero.trim())            e.numero            = 'Número é obrigatório';
    if (!form.complemento.trim())       e.complemento       = 'Complemento é obrigatório';
    if (!form.bairro.trim())            e.bairro            = 'Bairro é obrigatório';
    if (!form.cidade.trim())            e.cidade            = 'Cidade é obrigatória';
    if (!form.municipioId.trim())       e.municipioId       = 'Código do município é obrigatório';
    if (!form.cep.trim() || form.cep.replace(/\D/g, '').length !== 8)
      e.cep = 'CEP inválido — informe os 8 dígitos';
    if (!form.estado.trim() || form.estado.trim().length !== 2)
      e.estado = 'Estado (UF) deve ter 2 letras';
    if (!form.codUf.trim())             e.codUf  = 'Código UF é obrigatório';
    if (!form.status)                   e.status = 'Selecione o status';
    if (!form.dataAbertura.trim())
      e.dataAbertura = 'Data de Abertura é obrigatória';
    else if (!isValidDate(form.dataAbertura))
      e.dataAbertura = 'Data inválida — use dd/MM/yyyy';
    else {
      const [d, m, y] = form.dataAbertura.split('/').map(Number);
      if (new Date(y, m - 1, d) >= new Date())
        e.dataAbertura = 'A data de abertura deve ser no passado';
    }
    return e;
  }

  async function handleSubmit(ev) {
    ev.preventDefault();
    setApiError('');
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }

    setLoading(true);
    try {
      const res = await fetch(`${API}/estabelecimentos/empresa/${modalEmpresaId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
        body: JSON.stringify({
          ...form,
          cnpj:   form.cnpj.replace(/\D/g, ''),
          cep:    form.cep.replace(/\D/g, ''),
          estado: form.estado.toUpperCase(),
          matriz: form.matriz === true || form.matriz === 'true',
        }),
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
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <div>
            <h2 className="font-bold text-slate-800">Adicionar novo estabelecimento</h2>
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

          {/* Empresa — apenas para admin/suporte */}
          {!isRestrito && (
            <section>
              <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Empresa</p>
              <Field k="empresa" label="Empresa" required errors={errors}>
                <select
                  value={modalEmpresaId}
                  onChange={(e) => setModalEmpresaId(e.target.value)}
                  className={errCls('empresa', errors)}
                >
                  <option value="">Selecione uma empresa…</option>
                  {empresasOptions.map((emp) => (
                    <option key={emp.id} value={String(emp.id)}>
                      {emp.razaoSocial}{emp.nomeFantasia ? ` — ${emp.nomeFantasia}` : ''}
                    </option>
                  ))}
                </select>
              </Field>
            </section>
          )}

          {/* Identificação */}
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Identificação</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="sm:col-span-2">
                <Field k="nomeFantasia" label="Nome Fantasia" errors={errors}>
                  <input value={form.nomeFantasia} onChange={set('nomeFantasia')} className={inputCls()} placeholder="Nome fantasia (opcional)" />
                </Field>
              </div>
              <Field k="cnpj" label="CNPJ" required errors={errors}>
                <input
                  value={form.cnpj}
                  onChange={(e) => setForm((p) => ({ ...p, cnpj: maskCnpj(e.target.value) }))}
                  className={errCls('cnpj', errors)}
                  placeholder="00.000.000/0000-00"
                />
              </Field>
              <Field k="inscricaoEstadual" label="Inscrição Estadual" required errors={errors}>
                <input value={form.inscricaoEstadual} onChange={set('inscricaoEstadual')} className={errCls('inscricaoEstadual', errors)} />
              </Field>
              <Field k="inscricaoMunicipal" label="Inscrição Municipal" errors={errors}>
                <input value={form.inscricaoMunicipal} onChange={set('inscricaoMunicipal')} className={inputCls()} />
              </Field>
              <Field k="status" label="Status" required errors={errors}>
                <select value={form.status} onChange={set('status')} className={errCls('status', errors)}>
                  {STATUS_OPTIONS.filter(o => o.value).map(o => (
                    <option key={o.value} value={o.value}>{o.label}</option>
                  ))}
                </select>
              </Field>
              <Field k="telefone" label="Telefone" errors={errors}>
                <input value={form.telefone} onChange={set('telefone')} className={inputCls()} placeholder="(11) 99999-9999" />
              </Field>
              <Field k="email" label="E-mail" errors={errors}>
                <input type="email" value={form.email} onChange={set('email')} className={inputCls()} placeholder="email@exemplo.com" />
              </Field>
              <div className="flex items-center gap-3 pt-5">
                <input
                  type="checkbox"
                  id="matriz-create"
                  checked={form.matriz}
                  onChange={(e) => setForm((p) => ({ ...p, matriz: e.target.checked }))}
                  className="w-4 h-4 rounded border-slate-300 accent-orange-500 cursor-pointer"
                />
                <label htmlFor="matriz-create" className="text-sm text-slate-700 cursor-pointer select-none">
                  Estabelecimento matriz?
                </label>
              </div>
            </div>
          </section>

          {/* Endereço */}
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Endereço</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="sm:col-span-2">
                <Field k="logradouro" label="Logradouro" required errors={errors}>
                  <input value={form.logradouro} onChange={set('logradouro')} className={errCls('logradouro', errors)} placeholder="Rua, Av., etc." />
                </Field>
              </div>
              <Field k="numero" label="Número" required errors={errors}>
                <input value={form.numero} onChange={set('numero')} className={errCls('numero', errors)} />
              </Field>
              <Field k="complemento" label="Complemento" required errors={errors}>
                <input value={form.complemento} onChange={set('complemento')} className={errCls('complemento', errors)} placeholder="Apto, Bloco…" />
              </Field>
              <Field k="bairro" label="Bairro" required errors={errors}>
                <input value={form.bairro} onChange={set('bairro')} className={errCls('bairro', errors)} />
              </Field>
              <Field k="cidade" label="Cidade" required errors={errors}>
                <input value={form.cidade} onChange={set('cidade')} className={errCls('cidade', errors)} />
              </Field>
              <Field k="municipioId" label="Código do Município" required errors={errors}>
                <input value={form.municipioId} onChange={set('municipioId')} className={errCls('municipioId', errors)} placeholder="Ex: 3550308" />
              </Field>
              <Field k="estado" label="Estado (UF)" required errors={errors}>
                <input
                  maxLength={2}
                  value={form.estado}
                  onChange={set('estado')}
                  className={errCls('estado', errors)}
                  placeholder="SP"
                  style={{ textTransform: 'uppercase' }}
                  onInput={(e) => { e.target.value = e.target.value.toUpperCase(); }}
                />
              </Field>
              <Field k="codUf" label="Código UF" required errors={errors}>
                <input value={form.codUf} onChange={set('codUf')} className={errCls('codUf', errors)} placeholder="Ex: 35" />
              </Field>
              <Field k="cep" label="CEP" required errors={errors}>
                <input
                  value={form.cep}
                  onChange={(e) => setForm((p) => ({ ...p, cep: maskCep(e.target.value) }))}
                  className={errCls('cep', errors)}
                  placeholder="00000-000"
                />
              </Field>
              <Field k="paisId" label="Código do País" required errors={errors}>
                <input value={form.paisId} onChange={set('paisId')} className={inputCls()} placeholder="1058" />
              </Field>
              <Field k="pais" label="País" required errors={errors}>
                <input value={form.pais} onChange={set('pais')} className={inputCls()} />
              </Field>
            </div>
          </section>

          {/* Datas */}
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Datas</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Field k="dataAbertura" label="Data de Abertura" required errors={errors}>
                <input
                  value={form.dataAbertura}
                  onChange={(e) => setForm((p) => ({ ...p, dataAbertura: maskData(e.target.value) }))}
                  className={errCls('dataAbertura', errors)}
                  placeholder="dd/MM/yyyy"
                  maxLength={10}
                />
              </Field>
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
            {loading ? 'Cadastrando…' : 'Cadastrar estabelecimento'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── Modal de visualização (somente leitura) ──────────────────────────────────
function ViewModal({ estabelecimento, onClose }) {
  const est = estabelecimento;

  function row(label, value) {
    return (
      <div>
        <p className={labelCls()}>{label}</p>
        <p className="text-sm text-slate-700 bg-slate-50 rounded-xl px-3 py-2 min-h-[38px]">{value || '—'}</p>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <div>
            <h2 className="font-bold text-slate-800">Detalhes do Estabelecimento</h2>
            <p className="text-xs text-slate-400 mt-0.5">Visualização somente leitura</p>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700 transition-colors"><X size={20} /></button>
        </div>

        <div className="flex-1 overflow-y-auto p-6 space-y-5">
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Identificação</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="sm:col-span-2">{row('Nome Fantasia', est.nomeFantasia)}</div>
              {row('CNPJ', formatCnpj(est.cnpj))}
              {row('Inscrição Estadual', est.inscricaoEstadual)}
              {row('Inscrição Municipal', est.inscricaoMunicipal)}
              {row('Telefone', est.telefone)}
              {row('E-mail', est.email)}
              <div>
                <p className={labelCls()}>Status</p>
                <div className="mt-1">
                  <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${statusColor[est.status] || 'bg-slate-100 text-slate-500'}`}>
                    {est.status || '—'}
                  </span>
                </div>
              </div>
              <div>
                <p className={labelCls()}>Tipo</p>
                <div className="mt-1">
                  <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${est.matriz ? 'bg-blue-100 text-blue-700' : 'bg-slate-100 text-slate-500'}`}>
                    {est.matriz ? 'Matriz' : 'Filial'}
                  </span>
                </div>
              </div>
            </div>
          </section>

          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Endereço</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="sm:col-span-2">{row('Logradouro', est.logradouro)}</div>
              {row('Número', est.numero)}
              {row('Complemento', est.complemento)}
              {row('Bairro', est.bairro)}
              {row('Cidade', est.cidade)}
              {row('Estado (UF)', est.estado)}
              {row('CEP', est.cep)}
              {row('País', est.pais)}
            </div>
          </section>

          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Datas</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {row('Data de Abertura', est.dataAbertura)}
              {row('Data de Fechamento', est.dataFechamento)}
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

// ─── Modal de edição ─────────────────────────────────────────────────────────
function EditModal({ estabelecimento, empresaId, token, onClose, onSaved }) {
  const [form, setForm] = useState({
    nomeFantasia:      estabelecimento.nomeFantasia       ?? '',
    inscricaoEstadual: estabelecimento.inscricaoEstadual  ?? '',
    inscricaoMunicipal:estabelecimento.inscricaoMunicipal ?? '',
    logradouro:        estabelecimento.logradouro         ?? '',
    numero:            estabelecimento.numero             ?? '',
    complemento:       estabelecimento.complemento        ?? '',
    bairro:            estabelecimento.bairro             ?? '',
    cidade:            estabelecimento.cidade             ?? '',
    estado:            estabelecimento.estado             ?? '',
    cep:               estabelecimento.cep                ?? '',
    status:            estabelecimento.status             ?? 'ATIVA',
    matriz:            estabelecimento.matriz             ?? false,
    telefone:          estabelecimento.telefone           ?? '',
    email:             estabelecimento.email              ?? '',
    dataAbertura:      estabelecimento.dataAbertura       ?? '',
    dataFechamento:    estabelecimento.dataFechamento     ?? '',
  });
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState('');

  const set = (k) => (e) => setForm((p) => ({ ...p, [k]: e.target.value }));

  async function handleSubmit(ev) {
    ev.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await fetch(
        `${API}/estabelecimentos/empresa/${empresaId}/${estabelecimento.id}`,
        {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
          body: JSON.stringify({
            ...form,
            cep:            form.cep ? form.cep.replace(/\D/g, '') : null,
            matriz:         form.matriz === true || form.matriz === 'true',
            dataAbertura:   form.dataAbertura   || null,
            dataFechamento: form.dataFechamento || null,
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
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <h2 className="font-bold text-slate-800">Editar Estabelecimento</h2>
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
                <label className={labelCls()}>Nome Fantasia</label>
                <input value={form.nomeFantasia} onChange={set('nomeFantasia')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>Inscrição Estadual</label>
                <input value={form.inscricaoEstadual} onChange={set('inscricaoEstadual')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>Inscrição Municipal</label>
                <input value={form.inscricaoMunicipal} onChange={set('inscricaoMunicipal')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>Telefone</label>
                <input value={form.telefone} onChange={set('telefone')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>E-mail</label>
                <input type="email" value={form.email} onChange={set('email')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>Status</label>
                <select value={form.status} onChange={set('status')} className={inputCls()}>
                  {STATUS_OPTIONS.filter(o => o.value).map(o => (
                    <option key={o.value} value={o.value}>{o.label}</option>
                  ))}
                </select>
              </div>
              <div className="flex items-center gap-3 pt-5">
                <input
                  type="checkbox"
                  id="matriz-edit"
                  checked={form.matriz === true || form.matriz === 'true'}
                  onChange={(e) => setForm((p) => ({ ...p, matriz: e.target.checked }))}
                  className="w-4 h-4 rounded border-slate-300 accent-orange-500 cursor-pointer"
                />
                <label htmlFor="matriz-edit" className="text-sm text-slate-700 cursor-pointer select-none">
                  Estabelecimento matriz?
                </label>
              </div>
            </div>
          </section>

          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Endereço</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="sm:col-span-2">
                <label className={labelCls()}>Logradouro</label>
                <input value={form.logradouro} onChange={set('logradouro')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>Número</label>
                <input value={form.numero} onChange={set('numero')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>Complemento</label>
                <input value={form.complemento} onChange={set('complemento')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>Bairro</label>
                <input value={form.bairro} onChange={set('bairro')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>Cidade</label>
                <input value={form.cidade} onChange={set('cidade')} className={inputCls()} />
              </div>
              <div>
                <label className={labelCls()}>Estado (UF)</label>
                <input
                  maxLength={2}
                  value={form.estado}
                  onChange={set('estado')}
                  className={inputCls()}
                  placeholder="SP"
                  style={{ textTransform: 'uppercase' }}
                  onInput={(e) => { e.target.value = e.target.value.toUpperCase(); }}
                />
              </div>
              <div>
                <label className={labelCls()}>CEP</label>
                <input
                  value={form.cep}
                  onChange={(e) => setForm((p) => ({ ...p, cep: maskCep(e.target.value) }))}
                  className={inputCls()}
                  placeholder="00000-000"
                />
              </div>
            </div>
          </section>

          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Datas</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className={labelCls()}>Data de Abertura</label>
                <input
                  value={form.dataAbertura}
                  onChange={(e) => setForm((p) => ({ ...p, dataAbertura: maskData(e.target.value) }))}
                  className={inputCls()}
                  placeholder="dd/MM/yyyy"
                  maxLength={10}
                />
              </div>
              <div>
                <label className={labelCls()}>Data de Fechamento</label>
                <input
                  value={form.dataFechamento}
                  onChange={(e) => setForm((p) => ({ ...p, dataFechamento: maskData(e.target.value) }))}
                  className={inputCls()}
                  placeholder="dd/MM/yyyy"
                  maxLength={10}
                />
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
function DeleteModal({ estabelecimentoId, empresaId, nomeFantasia, cnpj, token, onClose, onDeleted }) {
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState('');

  async function handleDelete() {
    setLoading(true);
    setError('');
    try {
      const res = await fetch(`${API}/estabelecimentos/empresa/${empresaId}/${estabelecimentoId}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${token}` },
      });
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
            <h2 className="font-bold text-slate-800 text-sm">Excluir estabelecimento</h2>
            <p className="text-slate-500 text-xs">Esta ação não pode ser desfeita.</p>
          </div>
        </div>
        <p className="text-slate-600 text-sm mb-4">
          Tem certeza que deseja excluir{' '}
          <strong>{nomeFantasia || formatCnpj(cnpj) || `estabelecimento #${estabelecimentoId}`}</strong>?
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

// ─── EstabelecimentosPage ─────────────────────────────────────────────────────
export default function EstabelecimentosPage() {
  const { token, user } = useAuth();

  const isRestrito = !user?.hasRole('ADMINISTRADOR', 'SUPORTE');
  const canEdit    = user?.hasRole('ADMINISTRADOR', 'SUPORTE', 'EMPRESARIO');
  const canDelete  = user?.hasRole('ADMINISTRADOR', 'SUPORTE', 'EMPRESARIO');
  const canAdd     = user?.hasRole('ADMINISTRADOR', 'SUPORTE', 'EMPRESARIO', 'GERENTE');

  const [empresaId,       setEmpresaId]       = useState(isRestrito ? String(user?.empresaId ?? '') : '');
  const [empresasOptions, setEmpresasOptions] = useState([]);

  const [filters, setFilters] = useState({
    cnpj:         '',
    nomeFantasia: '',
    cidade:       '',
    estado:       '',
    status:       '',
    matriz:       '',
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
    if (filters.cnpj)          params.set('cnpj',         filters.cnpj.replace(/\D/g, ''));
    if (filters.nomeFantasia)  params.set('nome-fantasia', filters.nomeFantasia);
    if (filters.cidade)        params.set('cidade',        filters.cidade);
    if (filters.estado)        params.set('estado',        filters.estado);
    if (filters.status)        params.set('status',        filters.status);
    if (filters.matriz !== '') params.set('matriz',        filters.matriz);
    if (col)                   { params.set('ordenar-por', col); params.set('direcao', dir); }
    return params;
  }

  async function buscarComSort(pg = 0, col = sortCol, dir = sortDir) {
    setError('');
    setLoading(true);
    try {
      const params = buildParams(pg, col, dir);
      let url;
      if (isRestrito) {
        url = `${API}/estabelecimentos/empresa/${user?.empresaId}?${params}`;
      } else {
        if (empresaId) params.set('empresa-id', empresaId);
        url = `${API}/estabelecimentos?${params}`;
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
          url = `${API}/estabelecimentos/empresa/${user?.empresaId}?${params}`;
        } else {
          if (empresaId) params.set('empresa-id', empresaId);
          url = `${API}/estabelecimentos?${params}`;
        }

        const res = await fetch(url, { headers: { Authorization: `Bearer ${token}` } });
        if (!res.ok) throw new Error(`Erro ao buscar dados (página ${pg + 1}): ${res.status}`);

        const data = await res.json();
        allRows.push(...(data.content ?? []));
        if ((data.content?.length ?? 0) < PAGE_SIZE) break;
      }

      if (allRows.length === 0) { showToast('Nenhum dado retornado para exportar.', 'error'); return; }

      if (format === 'csv') {
        exportCsvEstabelecimento(allRows, 'estabelecimentos');
        showToast(`CSV exportado — ${allRows.length} registro(s)`);
      } else {
        exportXlsxEstabelecimento(allRows, 'estabelecimentos');
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
          onSaved={() => { setShowCreate(false); showToast('Estabelecimento cadastrado com sucesso!'); buscar(page); }}
        />
      )}

      {editTarget && (
        <EditModal
          estabelecimento={editTarget}
          empresaId={String(editTarget.empresaId ?? (isRestrito ? user?.empresaId : ''))}
          token={token}
          onClose={() => setEditTarget(null)}
          onSaved={() => { setEditTarget(null); showToast('Estabelecimento atualizado com sucesso!'); buscar(page); }}
        />
      )}

      {viewTarget && (
        <ViewModal
          estabelecimento={viewTarget}
          onClose={() => setViewTarget(null)}
        />
      )}

      {deleteTarget && (
        <DeleteModal
          estabelecimentoId={deleteTarget.id}
          empresaId={String(deleteTarget.empresaId ?? (isRestrito ? user?.empresaId : ''))}
          nomeFantasia={deleteTarget.nomeFantasia}
          cnpj={deleteTarget.cnpj}
          token={token}
          onClose={() => setDeleteTarget(null)}
          onDeleted={() => { setDeleteTarget(null); showToast('Estabelecimento excluído com sucesso!'); buscar(page); }}
        />
      )}

      {/* Cabeçalho */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-800 mb-1">Estabelecimentos</h1>
          <p className="text-slate-500 text-sm">Consulte e gerencie os estabelecimentos cadastrados no sistema.</p>
        </div>
        {canAdd && (
          <button
            onClick={() => setShowCreate(true)}
            className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-white text-sm font-semibold hover:scale-[1.02] transition-all flex-shrink-0"
            style={{ background: '#1D4ED8' }}
          >
            <Plus size={16} />
            Adicionar estabelecimento
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
            <label className={labelCls()}>Nome Fantasia</label>
            <input value={filters.nomeFantasia} onChange={setF('nomeFantasia')} className={inputCls()} placeholder="Nome do estabelecimento" />
          </div>
          <div>
            <label className={labelCls()}>CNPJ</label>
            <input
              value={filters.cnpj}
              onChange={(e) => setFilters((p) => ({ ...p, cnpj: maskCnpj(e.target.value) }))}
              className={inputCls()}
              placeholder="00.000.000/0000-00"
            />
          </div>
          <div>
            <label className={labelCls()}>Cidade</label>
            <input value={filters.cidade} onChange={setF('cidade')} className={inputCls()} />
          </div>
          <div>
            <label className={labelCls()}>Estado (UF)</label>
            <input
              maxLength={2}
              value={filters.estado}
              onChange={(e) => setFilters((p) => ({ ...p, estado: e.target.value.toUpperCase() }))}
              className={inputCls()}
              placeholder="SP"
            />
          </div>
          <div>
            <label className={labelCls()}>Status</label>
            <select value={filters.status} onChange={setF('status')} className={inputCls()}>
              {STATUS_OPTIONS.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
            </select>
          </div>
          <div>
            <label className={labelCls()}>Tipo</label>
            <select value={filters.matriz} onChange={setF('matriz')} className={inputCls()}>
              <option value="">Todos</option>
              <option value="true">Matriz</option>
              <option value="false">Filial</option>
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
                {result.totalElements} estabelecimento(s) encontrado(s)
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
              <p className="text-sm">Nenhum estabelecimento encontrado com esses filtros.</p>
            </div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="bg-slate-50 border-b border-slate-100">
                      {[
                        { label: 'Nome Fantasia', col: 'nomeFantasia', px: 'px-6' },
                        { label: 'CNPJ',          col: 'cnpj',         px: 'px-4' },
                        { label: 'Cidade',        col: 'cidade',       px: 'px-4' },
                        { label: 'Estado',        col: 'estado',       px: 'px-4' },
                        { label: 'Tipo',          col: 'matriz',       px: 'px-4' },
                        { label: 'Status',        col: 'status',       px: 'px-4' },
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
                    {items.map((est) => (
                      <tr key={est.id} className="hover:bg-slate-50 transition-colors">
                        <td className="px-6 py-4 font-semibold text-slate-800 text-xs">{est.nomeFantasia || '—'}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs font-mono">{formatCnpj(est.cnpj)}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs">{est.cidade || '—'}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs">{est.estado || '—'}</td>
                        <td className="px-4 py-4 text-xs">
                          <span className={`font-semibold px-2.5 py-1 rounded-full text-xs ${est.matriz ? 'bg-blue-100 text-blue-700' : 'bg-slate-100 text-slate-500'}`}>
                            {est.matriz ? 'Matriz' : 'Filial'}
                          </span>
                        </td>
                        <td className="px-4 py-4">
                          <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${statusColor[est.status] || 'bg-slate-100 text-slate-500'}`}>
                            {est.status || '—'}
                          </span>
                        </td>
                        <td className="px-4 py-4 text-right">
                          <div className="flex items-center justify-end gap-2">
                            {canEdit ? (
                              <button
                                onClick={() => setEditTarget(est)}
                                className="p-1.5 rounded-lg text-slate-400 hover:text-vulpes-orange hover:bg-vulpes-orange/10 transition-colors"
                                title="Editar"
                              >
                                <Pencil size={15} />
                              </button>
                            ) : (
                              <button
                                onClick={() => setViewTarget(est)}
                                className="p-1.5 rounded-lg text-slate-400 hover:text-blue-500 hover:bg-blue-50 transition-colors"
                                title="Visualizar"
                              >
                                <Eye size={15} />
                              </button>
                            )}
                            {canDelete && (
                              <button
                                onClick={() => setDeleteTarget(est)}
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
