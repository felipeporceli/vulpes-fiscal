import { useState, useEffect, useRef } from 'react';
import {
  Search, Pencil, Trash2, X, ChevronLeft, ChevronRight,
  AlertTriangle, ChevronUp, ChevronDown, ChevronsUpDown,
  Plus, Download, FileText, FileSpreadsheet,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { exportCsvConsumidor, exportXlsxConsumidor, EXPORT_MAX_ROWS } from '../utils/exportUtils';

const API = import.meta.env.VITE_API_URL ?? '';

// ─── Helpers de formatação / máscara ─────────────────────────────────────────

function formatCpf(v) {
  if (!v) return '—';
  const d = v.replace(/\D/g, '');
  return d.length === 11
    ? d.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
    : v;
}

function maskCpf(raw) {
  let v = raw.replace(/\D/g, '').slice(0, 11);
  if (v.length > 9)      v = v.replace(/(\d{3})(\d{3})(\d{3})(\d{0,2})/, '$1.$2.$3-$4');
  else if (v.length > 6) v = v.replace(/(\d{3})(\d{3})(\d{0,3})/, '$1.$2.$3');
  else if (v.length > 3) v = v.replace(/(\d{3})(\d{0,3})/, '$1.$2');
  return v;
}

function isValidCpf(raw) {
  const n = raw.replace(/\D/g, '');
  if (n.length !== 11) return false;
  if (/^(\d)\1+$/.test(n)) return false;
  let sum = 0;
  for (let i = 0; i < 9; i++) sum += parseInt(n[i]) * (10 - i);
  let rem = (sum * 10) % 11;
  if (rem === 10 || rem === 11) rem = 0;
  if (rem !== parseInt(n[9])) return false;
  sum = 0;
  for (let i = 0; i < 10; i++) sum += parseInt(n[i]) * (11 - i);
  rem = (sum * 10) % 11;
  if (rem === 10 || rem === 11) rem = 0;
  return rem === parseInt(n[10]);
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
  const d1 = calc(n.slice(0, 12));
  if (d1 !== parseInt(n[12])) return false;
  return calc(n.slice(0, 13)) === parseInt(n[13]);
}

function maskCep(raw) {
  let v = raw.replace(/\D/g, '').slice(0, 8);
  if (v.length > 5) v = v.replace(/(\d{5})(\d{0,3})/, '$1-$2');
  return v;
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
function errInputCls() {
  return 'w-full border border-red-400 rounded-xl px-3 py-2 text-sm text-slate-700 focus:outline-none focus:border-red-400 focus:ring-2 focus:ring-red-200 transition-all bg-white';
}
function labelCls() { return 'block text-xs font-medium text-slate-500 mb-1'; }

// ─── Modal de cadastro ────────────────────────────────────────────────────────
function CreateModal({ token, isRestrito, userEmpresaId, empresasOptions, onClose, onSaved }) {
  // Para restritos: empresa fixa do JWT; para admin/suporte: escolha no dropdown
  const [modalEmpresaId, setModalEmpresaId] = useState(
    isRestrito ? String(userEmpresaId ?? '') : ''
  );
  const [estabelecimentoId, setEstabelecimentoId] = useState('');
  const [estabelecOptions,  setEstabelecOptions]  = useState([]);
  const [loadingEstabels,   setLoadingEstabels]   = useState(false);

  const [isCnpj,        setIsCnpj]        = useState(false);
  const [isEstrangeiro, setIsEstrangeiro] = useState(false);

  const EMPTY = {
    nome:              '',
    cpf:               '',
    email:             '',
    telefone:          '',
    logradouro:        '',
    numero:            '',
    complemento:       '',
    bairro:            '',
    municipio:         '',
    uf:                '',
    cep:               '',
    inscricaoEstadual: '',
    paisId:            '1058',
    pais:              'Brasil',
    cnpj:              '',
    estrangeiroId:     '',
  };
  const [form,     setForm]     = useState(EMPTY);
  const [loading,  setLoading]  = useState(false);
  const [errors,   setErrors]   = useState({});
  const [apiError, setApiError] = useState('');

  const set = (k) => (e) => setForm((p) => ({ ...p, [k]: e.target.value }));

  // Carrega estabelecimentos sempre que a empresa selecionada mudar
  useEffect(() => {
    if (!modalEmpresaId) { setEstabelecOptions([]); setEstabelecimentoId(''); return; }
    setLoadingEstabels(true);
    setEstabelecimentoId('');
    fetch(`${API}/estabelecimentos/empresa/${modalEmpresaId}?tamanho-pagina=100&pagina=0`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((r) => r.json())
      .then((data) => {
        // Filtra apenas opções com id válido (garante que o backend foi reiniciado com a nova DTO)
        const validos = (data.content ?? []).filter((e) => e.id != null);
        setEstabelecOptions(validos);
      })
      .catch(() => setEstabelecOptions([]))
      .finally(() => setLoadingEstabels(false));
  }, [modalEmpresaId, token]);

  function validate() {
    const e = {};
    if (!modalEmpresaId && !isRestrito) e.empresa = 'Selecione uma empresa';
    if (!estabelecimentoId)             e.estabelecimentoId = 'Selecione um estabelecimento';
    if (!form.nome.trim())              e.nome     = 'Nome é obrigatório';
    if (!form.cpf.trim())
      e.cpf = 'CPF é obrigatório';
    else if (form.cpf.replace(/\D/g, '').length !== 11)
      e.cpf = 'CPF incompleto — informe os 11 dígitos';
    else if (!isValidCpf(form.cpf))
      e.cpf = 'CPF inválido — verifique os dígitos verificadores';
    if (!form.email.trim())
      e.email = 'E-mail é obrigatório';
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email.trim()))
      e.email = 'E-mail em formato inválido';
    if (!form.telefone.trim()) e.telefone = 'Telefone é obrigatório';
    // Endereço obrigatório
    if (!form.logradouro.trim()) e.logradouro = 'Logradouro é obrigatório';
    if (!form.numero.trim())     e.numero     = 'Número é obrigatório';
    if (!form.bairro.trim())     e.bairro     = 'Bairro é obrigatório';
    if (!form.municipio.trim())  e.municipio  = 'Município é obrigatório';
    if (!form.uf.trim())
      e.uf = 'UF é obrigatória';
    else if (form.uf.trim().length !== 2)
      e.uf = 'UF deve ter exatamente 2 letras';
    if (!form.cep.trim())
      e.cep = 'CEP é obrigatório';
    else if (form.cep.replace(/\D/g, '').length !== 8)
      e.cep = 'CEP incompleto — informe os 8 dígitos';
    // Condicionais
    if (isCnpj) {
      if (!form.cnpj.trim())
        e.cnpj = 'CNPJ é obrigatório quando marcado';
      else if (form.cnpj.replace(/\D/g, '').length !== 14)
        e.cnpj = 'CNPJ incompleto — informe os 14 dígitos';
      else if (!isValidCnpj(form.cnpj))
        e.cnpj = 'CNPJ inválido — verifique os dígitos verificadores';
    }
    if (isEstrangeiro && !form.estrangeiroId.trim())
      e.estrangeiroId = 'ID Estrangeiro é obrigatório quando marcado';
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
        nome:              form.nome.trim(),
        cpf:               form.cpf.replace(/\D/g, ''),
        email:             form.email.trim(),
        telefone:          form.telefone.trim(),
        logradouro:        form.logradouro.trim(),
        numero:            form.numero.trim(),
        complemento:       form.complemento.trim() || null,
        bairro:            form.bairro.trim(),
        municipio:         form.municipio.trim(),
        uf:                form.uf.trim().toUpperCase(),
        cep:               form.cep.replace(/\D/g, ''),
        inscricaoEstadual: form.inscricaoEstadual.trim() || null,
        paisId:            form.paisId,
        pais:              form.pais,
        cnpj:              isCnpj && form.cnpj ? form.cnpj.replace(/\D/g, '') : null,
        estrangeiroId:     isEstrangeiro && form.estrangeiroId ? form.estrangeiroId.trim() : null,
      };

      const res = await fetch(
        `${API}/consumidores/empresa/${modalEmpresaId}/estabelecimento/${estabelecimentoId}`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
          body: JSON.stringify(body),
        }
      );
      if (!res.ok) {
        const bd = await res.json().catch(() => ({}));
        throw new Error(bd.mensagem || `Erro ${res.status}`);
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

  const ec = (k) => errors[k] ? errInputCls() : inputCls();

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col">

        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <div>
            <h2 className="font-bold text-slate-800">Adicionar novo consumidor</h2>
            <p className="text-xs text-slate-400 mt-0.5">
              Campos marcados com <span className="text-red-400">*</span> são obrigatórios
            </p>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700 transition-colors">
            <X size={20} />
          </button>
        </div>

        {/* Body */}
        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6 space-y-5">
          {apiError && (
            <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl px-4 py-3">
              <AlertTriangle size={14} className="mt-0.5 flex-shrink-0" />
              {apiError}
            </div>
          )}
          {Object.keys(errors).length > 0 && (
            <div className="bg-amber-50 border border-amber-200 rounded-xl px-4 py-3">
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

          {/* ── Empresa (admin/suporte apenas) ── */}
          {!isRestrito && (
            <section>
              <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Empresa</p>
              {field('empresa', 'Empresa', true,
                <select
                  value={modalEmpresaId}
                  onChange={(e) => setModalEmpresaId(e.target.value)}
                  className={ec('empresa')}
                >
                  <option value="">Selecione uma empresa…</option>
                  {empresasOptions.map((emp) => (
                    <option key={emp.id} value={String(emp.id)}>
                      {emp.razaoSocial}{emp.nomeFantasia ? ` — ${emp.nomeFantasia}` : ''}
                    </option>
                  ))}
                </select>
              )}
            </section>
          )}

          {/* ── Estabelecimento ── */}
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Estabelecimento</p>
            {field('estabelecimentoId', 'Estabelecimento', true,
              <select
                value={estabelecimentoId}
                onChange={(e) => setEstabelecimentoId(e.target.value)}
                className={ec('estabelecimentoId')}
                disabled={loadingEstabels || !modalEmpresaId}
              >
                <option value="">
                  {!modalEmpresaId
                    ? (isRestrito ? 'Carregando…' : 'Selecione uma empresa primeiro')
                    : loadingEstabels
                    ? 'Carregando estabelecimentos…'
                    : estabelecOptions.length === 0
                    ? 'Nenhum estabelecimento encontrado'
                    : 'Selecione um estabelecimento…'}
                </option>
                {estabelecOptions.map((est) => (
                  <option key={est.id} value={String(est.id)}>
                    {est.nomeFantasia
                      ? `${est.nomeFantasia} (CNPJ: ${est.cnpj ?? '—'})`
                      : `CNPJ: ${est.cnpj ?? est.id}`}
                  </option>
                ))}
              </select>
            )}
          </section>

          {/* ── Dados pessoais ── */}
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Dados pessoais</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="sm:col-span-2">
                {field('nome', 'Nome', true,
                  <input value={form.nome} onChange={set('nome')} className={ec('nome')} placeholder="Nome completo" />
                )}
              </div>
              {field('cpf', 'CPF', true,
                <input
                  value={form.cpf}
                  onChange={(e) => setForm((p) => ({ ...p, cpf: maskCpf(e.target.value) }))}
                  className={ec('cpf')}
                  placeholder="000.000.000-00"
                />
              )}
              {field('email', 'E-mail', true,
                <input type="email" value={form.email} onChange={set('email')} className={ec('email')} placeholder="email@exemplo.com" />
              )}
              {field('telefone', 'Telefone', true,
                <input value={form.telefone} onChange={set('telefone')} className={ec('telefone')} placeholder="(11) 99999-9999" />
              )}
              {field('inscricaoEstadual', 'Inscrição Estadual', false,
                <input value={form.inscricaoEstadual} onChange={set('inscricaoEstadual')} className={inputCls()} />
              )}
            </div>
          </section>

          {/* ── Informações adicionais ── */}
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Informações adicionais</p>
            <div className="flex flex-wrap gap-5 mb-4">
              <label className="flex items-center gap-2 text-sm text-slate-700 cursor-pointer select-none">
                <input
                  type="checkbox"
                  checked={isCnpj}
                  onChange={(e) => { setIsCnpj(e.target.checked); if (!e.target.checked) setForm((p) => ({ ...p, cnpj: '' })); }}
                  className="w-4 h-4 rounded border-slate-300 accent-orange-500 cursor-pointer"
                />
                Consumidor é CNPJ?
              </label>
              <label className="flex items-center gap-2 text-sm text-slate-700 cursor-pointer select-none">
                <input
                  type="checkbox"
                  checked={isEstrangeiro}
                  onChange={(e) => { setIsEstrangeiro(e.target.checked); if (!e.target.checked) setForm((p) => ({ ...p, estrangeiroId: '' })); }}
                  className="w-4 h-4 rounded border-slate-300 accent-orange-500 cursor-pointer"
                />
                Consumidor é estrangeiro?
              </label>
            </div>
            {(isCnpj || isEstrangeiro) && (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {isCnpj && field('cnpj', 'CNPJ', true,
                  <input
                    value={form.cnpj}
                    onChange={(e) => setForm((p) => ({ ...p, cnpj: maskCnpj(e.target.value) }))}
                    className={ec('cnpj')}
                    placeholder="00.000.000/0000-00"
                  />
                )}
                {isEstrangeiro && field('estrangeiroId', 'ID Estrangeiro', true,
                  <input value={form.estrangeiroId} onChange={set('estrangeiroId')} className={ec('estrangeiroId')} />
                )}
              </div>
            )}
          </section>

          {/* ── Endereço (obrigatório) ── */}
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Endereço</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="sm:col-span-2">
                {field('logradouro', 'Logradouro', true,
                  <input value={form.logradouro} onChange={set('logradouro')} className={ec('logradouro')} placeholder="Rua, Av., etc." />
                )}
              </div>
              {field('numero', 'Número', true,
                <input value={form.numero} onChange={set('numero')} className={ec('numero')} placeholder="123" />
              )}
              {field('complemento', 'Complemento', false,
                <input value={form.complemento} onChange={set('complemento')} className={inputCls()} placeholder="Apto, Bloco…" />
              )}
              {field('bairro', 'Bairro', true,
                <input value={form.bairro} onChange={set('bairro')} className={ec('bairro')} />
              )}
              {field('municipio', 'Município', true,
                <input value={form.municipio} onChange={set('municipio')} className={ec('municipio')} />
              )}
              {field('uf', 'UF', true,
                <input
                  maxLength={2}
                  value={form.uf}
                  onChange={set('uf')}
                  className={ec('uf')}
                  placeholder="SP"
                  style={{ textTransform: 'uppercase' }}
                  onInput={(e) => { e.target.value = e.target.value.toUpperCase(); }}
                />
              )}
              {field('cep', 'CEP', true,
                <input
                  value={form.cep}
                  onChange={(e) => setForm((p) => ({ ...p, cep: maskCep(e.target.value) }))}
                  className={ec('cep')}
                  placeholder="00000-000"
                />
              )}
            </div>
          </section>
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
            {loading ? 'Cadastrando…' : 'Cadastrar consumidor'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── Modal de edição ─────────────────────────────────────────────────────────
function EditModal({ consumidor, empresaId, token, onClose, onSaved }) {
  const [form, setForm] = useState({
    nome:              consumidor.nome              ?? '',
    email:             consumidor.email             ?? '',
    telefone:          consumidor.telefone          ?? '',
    logradouro:        consumidor.logradouro        ?? '',
    numero:            consumidor.numero            ?? '',
    complemento:       consumidor.complemento       ?? '',
    bairro:            consumidor.bairro            ?? '',
    municipio:         consumidor.municipio         ?? '',
    uf:                consumidor.uf                ?? '',
    cep:               consumidor.cep               ?? '',
    inscricaoEstadual: consumidor.inscricaoEstadual ?? '',
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
        `${API}/consumidores/empresa/${empresaId}/${consumidor.id}`,
        {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
          body: JSON.stringify({
            ...form,
            cep: form.cep ? form.cep.replace(/\D/g, '') : null,
          }),
        }
      );
      if (!res.ok) {
        const bd = await res.json().catch(() => ({}));
        throw new Error(bd.mensagem || `Erro ${res.status}`);
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
          <h2 className="font-bold text-slate-800">Editar Consumidor</h2>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700 transition-colors">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6">
          {error && (
            <div className="mb-4 flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl px-4 py-3">
              <AlertTriangle size={14} className="mt-0.5 flex-shrink-0" />
              {error}
            </div>
          )}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="sm:col-span-2">
              <label className={labelCls()}>Nome</label>
              <input value={form.nome} onChange={set('nome')} className={inputCls()} />
            </div>
            <div>
              <label className={labelCls()}>E-mail</label>
              <input type="email" value={form.email} onChange={set('email')} className={inputCls()} />
            </div>
            <div>
              <label className={labelCls()}>Telefone</label>
              <input value={form.telefone} onChange={set('telefone')} className={inputCls()} />
            </div>
            <div>
              <label className={labelCls()}>Inscrição Estadual</label>
              <input value={form.inscricaoEstadual} onChange={set('inscricaoEstadual')} className={inputCls()} />
            </div>
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
              <label className={labelCls()}>Município</label>
              <input value={form.municipio} onChange={set('municipio')} className={inputCls()} />
            </div>
            <div>
              <label className={labelCls()}>UF</label>
              <input maxLength={2} value={form.uf} onChange={set('uf')} className={inputCls()} placeholder="SP" />
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
        </form>

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
function DeleteModal({ consumidorId, empresaId, nome, token, onClose, onDeleted }) {
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState('');

  async function handleDelete() {
    setLoading(true);
    setError('');
    try {
      const res = await fetch(`${API}/consumidores/empresa/${empresaId}/${consumidorId}`, {
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
            <h2 className="font-bold text-slate-800 text-sm">Excluir consumidor</h2>
            <p className="text-slate-500 text-xs">Esta ação não pode ser desfeita.</p>
          </div>
        </div>
        <p className="text-slate-600 text-sm mb-4">
          Tem certeza que deseja excluir <strong>{nome || `consumidor #${consumidorId}`}</strong>?
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

// ─── ConsumidoresPage ─────────────────────────────────────────────────────────
export default function ConsumidoresPage() {
  const { token, user } = useAuth();

  const isRestrito = !user?.hasRole('ADMINISTRADOR', 'SUPORTE');
  const canDelete  = user?.hasRole('ADMINISTRADOR', 'SUPORTE', 'EMPRESARIO', 'GERENTE', 'CAIXA');

  // Para admin/suporte: ID da empresa selecionada no filtro (opcional)
  // Para restritos: fixo do JWT
  const [empresaId,      setEmpresaId]      = useState(isRestrito ? String(user?.empresaId ?? '') : '');
  // Lista de empresas para o dropdown (admin/suporte)
  const [empresasOptions, setEmpresasOptions] = useState([]);

  const [filters, setFilters] = useState({
    cpf:       '',
    nome:      '',
    email:     '',
    uf:        '',
    municipio: '',
    telefone:  '',
  });

  const [result,     setResult]     = useState(null);
  const [page,       setPage]       = useState(0);
  const [sortCol,    setSortCol]    = useState(null);
  const [sortDir,    setSortDir]    = useState('asc');
  const [loading,    setLoading]    = useState(false);
  const [error,      setError]      = useState('');

  const [showCreate,   setShowCreate]   = useState(false);
  const [editTarget,   setEditTarget]   = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [toast,        setToast]        = useState(null);
  const [exportOpen,   setExportOpen]   = useState(false);
  const [exporting,    setExporting]    = useState(false);
  const exportRef = useRef(null);

  const setF = (k) => (e) => setFilters((p) => ({ ...p, [k]: e.target.value }));

  // Carrega lista de empresas para admin/suporte (dropdown de filtro + dropdown do modal)
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

  // Constrói os query params comuns aos dois endpoints
  function buildParams(pg, col, dir) {
    const params = new URLSearchParams({ pagina: pg, 'tamanho-pagina': 10 });
    if (filters.cpf)       params.set('cpf',       filters.cpf.replace(/\D/g, ''));
    if (filters.nome)      params.set('nome',      filters.nome);
    if (filters.email)     params.set('email',     filters.email);
    if (filters.uf)        params.set('uf',        filters.uf);
    if (filters.municipio) params.set('municipio', filters.municipio);
    if (filters.telefone)  params.set('telefone',  filters.telefone);
    if (col)               { params.set('ordenar-por', col); params.set('direcao', dir); }
    return params;
  }

  async function buscarComSort(pg = 0, col = sortCol, dir = sortDir) {
    setError('');
    setLoading(true);
    try {
      const params = buildParams(pg, col, dir);
      let url;

      if (isRestrito) {
        // Usuários restritos: empresa obrigatória (vem do JWT), usa /empresa/{id}
        const eid = String(user?.empresaId ?? '');
        url = `${API}/consumidores/empresa/${eid}?${params}`;
      } else {
        // Admin/suporte: usa endpoint global, empresa é opcional
        if (empresaId) params.set('empresa-id', empresaId);
        url = `${API}/consumidores?${params}`;
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
          url = `${API}/consumidores/empresa/${user?.empresaId}?${params}`;
        } else {
          if (empresaId) params.set('empresa-id', empresaId);
          url = `${API}/consumidores?${params}`;
        }

        const res = await fetch(url, { headers: { Authorization: `Bearer ${token}` } });
        if (!res.ok) throw new Error(`Erro ao buscar dados (página ${pg + 1}): ${res.status}`);

        const data = await res.json();
        allRows.push(...(data.content ?? []));
        if ((data.content?.length ?? 0) < PAGE_SIZE) break;
      }

      if (allRows.length === 0) { showToast('Nenhum dado retornado para exportar.', 'error'); return; }

      if (format === 'csv') {
        exportCsvConsumidor(allRows, 'consumidores');
        showToast(`CSV exportado — ${allRows.length} registro(s)`);
      } else {
        exportXlsxConsumidor(allRows, 'consumidores');
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
          isRestrito={isRestrito}
          userEmpresaId={user?.empresaId}
          empresasOptions={empresasOptions}
          onClose={() => setShowCreate(false)}
          onSaved={() => {
            setShowCreate(false);
            showToast('Consumidor cadastrado com sucesso!');
            buscar(page);
          }}
        />
      )}

      {/* Edit Modal — usa empresaId do próprio consumidor (campo empresaId da DTO) */}
      {editTarget && (
        <EditModal
          consumidor={editTarget}
          empresaId={String(editTarget.empresaId ?? (isRestrito ? user?.empresaId : ''))}
          token={token}
          onClose={() => setEditTarget(null)}
          onSaved={() => {
            setEditTarget(null);
            showToast('Consumidor atualizado com sucesso!');
            buscar(page);
          }}
        />
      )}

      {/* Delete Modal — usa empresaId do próprio consumidor */}
      {deleteTarget && (
        <DeleteModal
          consumidorId={deleteTarget.id}
          empresaId={String(deleteTarget.empresaId ?? (isRestrito ? user?.empresaId : ''))}
          nome={deleteTarget.nome}
          token={token}
          onClose={() => setDeleteTarget(null)}
          onDeleted={() => {
            setDeleteTarget(null);
            showToast('Consumidor excluído com sucesso!');
            buscar(page);
          }}
        />
      )}

      {/* Cabeçalho */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-800 mb-1">Consumidores</h1>
          <p className="text-slate-500 text-sm">Consulte, edite ou exclua consumidores cadastrados no sistema.</p>
        </div>
        <button
          onClick={() => setShowCreate(true)}
          className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-white text-sm font-semibold hover:scale-[1.02] transition-all flex-shrink-0"
          style={{ background: '#1D4ED8' }}
        >
          <Plus size={16} />
          Adicionar consumidor
        </button>
      </div>

      {/* Filtros */}
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
        <h2 className="font-semibold text-slate-700 text-sm mb-4">Filtros de pesquisa</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">

          {/* Empresa — dropdown opcional para admin/suporte */}
          {!isRestrito && (
            <div className="sm:col-span-2">
              <label className="block text-xs font-medium text-slate-500 mb-1">Empresa</label>
              <select
                value={empresaId}
                onChange={(e) => setEmpresaId(e.target.value)}
                className={inputCls()}
              >
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
            <label className="block text-xs font-medium text-slate-500 mb-1">Nome</label>
            <input value={filters.nome} onChange={setF('nome')} className={inputCls()} placeholder="Nome do consumidor" />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-500 mb-1">CPF</label>
            <input
              value={filters.cpf}
              onChange={(e) => setFilters((p) => ({ ...p, cpf: maskCpf(e.target.value) }))}
              className={inputCls()}
              placeholder="000.000.000-00"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-500 mb-1">E-mail</label>
            <input value={filters.email} onChange={setF('email')} className={inputCls()} placeholder="email@exemplo.com" />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-500 mb-1">Telefone</label>
            <input value={filters.telefone} onChange={setF('telefone')} className={inputCls()} placeholder="(11) 99999-9999" />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-500 mb-1">Município</label>
            <input value={filters.municipio} onChange={setF('municipio')} className={inputCls()} />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-500 mb-1">UF</label>
            <input
              maxLength={2}
              value={filters.uf}
              onChange={(e) => setFilters((p) => ({ ...p, uf: e.target.value.toUpperCase() }))}
              className={inputCls()}
              placeholder="SP"
            />
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
                {result.totalElements} consumidor(es) encontrado(s)
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
              <p className="text-sm">Nenhum consumidor encontrado com esses filtros.</p>
            </div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="bg-slate-50 border-b border-slate-100">
                      {[
                        { label: 'Nome',      col: 'nome',      px: 'px-6' },
                        { label: 'CPF',       col: 'cpf',       px: 'px-4' },
                        { label: 'E-mail',    col: 'email',     px: 'px-4' },
                        { label: 'Telefone',  col: 'telefone',  px: 'px-4' },
                        { label: 'Município', col: 'municipio', px: 'px-4' },
                        { label: 'UF',        col: 'uf',        px: 'px-4' },
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
                    {items.map((c) => (
                      <tr key={c.id} className="hover:bg-slate-50 transition-colors">
                        <td className="px-6 py-4 font-semibold text-slate-800 text-xs">{c.nome || '—'}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs font-mono">{formatCpf(c.cpf)}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs">{c.email || '—'}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs">{c.telefone || '—'}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs">{c.municipio || '—'}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs">{c.uf || '—'}</td>
                        <td className="px-4 py-4 text-right">
                          <div className="flex items-center justify-end gap-2">
                            <button
                              onClick={() => setEditTarget(c)}
                              className="p-1.5 rounded-lg text-slate-400 hover:text-vulpes-orange hover:bg-vulpes-orange/10 transition-colors"
                              title="Editar"
                            >
                              <Pencil size={15} />
                            </button>
                            {canDelete && (
                              <button
                                onClick={() => setDeleteTarget(c)}
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
