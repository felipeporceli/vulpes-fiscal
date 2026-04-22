import { useState, useEffect, useRef } from 'react';
import {
  Search, X, ChevronLeft, ChevronRight,
  AlertTriangle, ChevronUp, ChevronDown, ChevronsUpDown,
  Plus, Eye, Download, FileText, FileSpreadsheet, Trash2,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import {
  exportCsvVenda,
  exportXlsxVenda,
  EXPORT_MAX_ROWS,
} from '../utils/exportUtils';

const API = import.meta.env.VITE_API_URL ?? '';

// ─── Helpers ─────────────────────────────────────────────────────────────────

const METODOS_PAGAMENTO = [
  { value: 'DINHEIRO',            label: 'Dinheiro'            },
  { value: 'CHEQUE',              label: 'Cheque'              },
  { value: 'CARTAO_CREDITO',      label: 'Cartão de Crédito'   },
  { value: 'CARTAO_DEBITO',       label: 'Cartão de Débito'    },
  { value: 'CREDITO_LOJA',        label: 'Crédito Loja'        },
  { value: 'VALE_ALIMENTACAO',    label: 'Vale Alimentação'    },
  { value: 'VALE_REFEICAO',       label: 'Vale Refeição'       },
  { value: 'VALE_PRESENTE',       label: 'Vale Presente'       },
  { value: 'VALE_COMBUSTIVEL',    label: 'Vale Combustível'    },
  { value: 'BOLETO',              label: 'Boleto Bancário'     },
  { value: 'PIX',                 label: 'PIX'                 },
  { value: 'TRANSFERENCIA_BANCARIA', label: 'Transferência Bancária' },
  { value: 'CARTEIRA_DIGITAL',    label: 'Carteira Digital'    },
  { value: 'SEM_PAGAMENTO',       label: 'Sem Pagamento'       },
  { value: 'OUTROS',              label: 'Outros'              },
];

const METODOS_PARCELAVEIS = new Set(['CARTAO_CREDITO', 'CREDITO_LOJA', 'BOLETO']);

const statusPagColor = {
  CONCLUIDO: 'bg-emerald-100 text-emerald-700',
  PENDENTE:  'bg-amber-100   text-amber-700',
  ATRASADO:  'bg-red-100     text-red-600',
};

function labelMetodo(val) {
  return METODOS_PAGAMENTO.find(m => m.value === val)?.label ?? val ?? '—';
}

function fmtMoeda(v) {
  if (v == null) return '—';
  return `R$ ${Number(v).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function fmtDataHora(v) {
  if (!v) return '—';
  return new Date(v).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
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
function errCls(k, errors) {
  return errors?.[k]
    ? 'w-full border border-red-400 rounded-xl px-3 py-2 text-sm text-slate-700 focus:outline-none focus:border-red-400 focus:ring-2 focus:ring-red-200 transition-all bg-white'
    : inputCls();
}
function labelCls() { return 'block text-sm font-medium text-slate-500 mb-1'; }

function Field({ k, label, required, errors, children }) {
  return (
    <div>
      <label className={labelCls()}>{label}{required && <span className="text-red-400 ml-0.5">*</span>}</label>
      {children}
      {errors?.[k] && <p className="mt-1 text-red-500 text-xs">{errors[k]}</p>}
    </div>
  );
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

// ─── Modal de visualização ────────────────────────────────────────────────────
function ViewModal({ venda, onClose }) {
  const v = venda;

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
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[90vh] flex flex-col">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <div>
            <h2 className="font-bold text-slate-800">Detalhes da Venda #{v.id}</h2>
            <p className="text-xs text-slate-400 mt-0.5">Visualização somente leitura</p>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700 transition-colors"><X size={20} /></button>
        </div>

        <div className="flex-1 overflow-y-auto p-6 space-y-5">
          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Geral</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {row('Consumidor', v.consumidorNome)}
              {row('Data', fmtDataHora(v.dataCriacao))}
            </div>
          </section>

          <section>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-3">Responsáveis</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {row('Vendedor', v.vendedorNome ?? '—')}
              {row('Caixa', v.usuarioNome ?? '—')}
            </div>
          </section>

          <section>
            <p className="text-sm font-semibold text-slate-400 uppercase tracking-wide mb-3">Pagamento</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {row('Método', labelMetodo(v.metodoPagamento))}
              <div>
                <p className={labelCls()}>Status</p>
                <div className="mt-1">
                  <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${statusPagColor[v.statusPagamento] ?? 'bg-slate-100 text-slate-500'}`}>
                    {v.statusPagamento ?? '—'}
                  </span>
                </div>
              </div>
              {row('Valor Total',  fmtMoeda(v.valorTotal))}
              {row('Desconto',     fmtMoeda(v.desconto ?? 0))}
              {row('Valor Final',  fmtMoeda(v.valorFinal))}
              {row('Parcelas',     v.parcelas ?? 1)}
              {v.metodoPagamento === 'DINHEIRO' && row('Valor Recebido', fmtMoeda(v.valorRecebido))}
              {v.metodoPagamento === 'DINHEIRO' && (() => {
                const troco = (Number(v.valorRecebido) || 0) - (Number(v.valorFinal) || 0);
                return (
                  <div>
                    <p className={labelCls()}>Troco</p>
                    <p className={`text-sm font-bold rounded-xl px-3 py-2 min-h-[38px] ${troco > 0 ? 'bg-emerald-50 text-emerald-700' : 'bg-slate-50 text-slate-500'}`}>
                      {fmtMoeda(Math.max(0, troco))}
                    </p>
                  </div>
                );
              })()}
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

// Taxas idênticas ao VendaService.java
const TAXAS_JUROS = { CARTAO_CREDITO: 0.0299, CREDITO_LOJA: 0.0350, BOLETO: 0.0199 };

function fmtCpf(v) {
  if (!v) return '—';
  const d = String(v).replace(/\D/g, '');
  return d.length === 11 ? d.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4') : v;
}

// ─── Modal de cadastro ────────────────────────────────────────────────────────
function CreateModal({ token, isRestrito, userEmpresaId, empresasOptions, onClose, onSaved }) {
  const [modalEmpresaId,         setModalEmpresaId]         = useState(isRestrito ? String(userEmpresaId ?? '') : '');
  const [estabelecimentos,       setEstabelecimentos]       = useState([]);
  const [modalEstabelecimentoId, setModalEstabelecimentoId] = useState('');
  const [loadingEst,             setLoadingEst]             = useState(false);

  // Consumidor
  const [consumidores,          setConsumidores]          = useState([]);
  const [loadingCons,           setLoadingCons]           = useState(false);
  const [consumidorSelecionado, setConsumidorSelecionado] = useState(null);

  // Vendedores
  const [vendedores,   setVendedores]   = useState([]);
  const [vendedorId,   setVendedorId]   = useState('');

  // Produtos
  const [produtos,     setProdutos]     = useState([]);
  const [loadingProd,  setLoadingProd]  = useState(false);

  const [emitirNfce, setEmitirNfce] = useState(false);

  const ITEM_EMPTY = { produtoObj: null, quantidade: '', tributacaoObj: null, tributacoes: [] };
  const [itens, setItens] = useState([{ ...ITEM_EMPTY }]);

  const [pagamento, setPagamento] = useState({
    metodoPagamento: 'DINHEIRO',
    valorRecebido:   '',
    desconto:        '',
    parcelas:        '1',
  });

  const [loading,  setLoading]  = useState(false);
  const [errors,   setErrors]   = useState({});
  const [apiError, setApiError] = useState('');

  // Busca estabelecimentos
  useEffect(() => {
    if (!modalEmpresaId) {
      setEstabelecimentos([]); setModalEstabelecimentoId('');
      setConsumidores([]); setConsumidorSelecionado(null);
      setProdutos([]); setVendedores([]); setVendedorId('');
      return;
    }
    setLoadingEst(true);
    fetch(`${API}/estabelecimentos/empresa/${modalEmpresaId}?tamanho-pagina=100&pagina=0`, {
      headers: { Authorization: `Bearer ${token}` },
    }).then(r => r.json()).then(d => setEstabelecimentos(d.content ?? [])).catch(() => setEstabelecimentos([])).finally(() => setLoadingEst(false));

    // Busca vendedores da empresa
    fetch(`${API}/usuarios/empresa/${modalEmpresaId}/vendedores`, {
      headers: { Authorization: `Bearer ${token}` },
    }).then(r => r.json()).then(d => setVendedores(Array.isArray(d) ? d : [])).catch(() => setVendedores([]));
  }, [modalEmpresaId, token]);

  // Busca consumidores quando empresa muda
  useEffect(() => {
    if (!modalEmpresaId) { setConsumidores([]); setConsumidorSelecionado(null); return; }
    setLoadingCons(true);
    fetch(`${API}/consumidores/empresa/${modalEmpresaId}?tamanho-pagina=200&pagina=0`, {
      headers: { Authorization: `Bearer ${token}` },
    }).then(r => r.json()).then(d => { setConsumidores(d.content ?? []); setConsumidorSelecionado(null); }).catch(() => setConsumidores([])).finally(() => setLoadingCons(false));
  }, [modalEmpresaId, token]);

  // Busca produtos quando empresa muda
  useEffect(() => {
    if (!modalEmpresaId) { setProdutos([]); return; }
    setLoadingProd(true);
    fetch(`${API}/produtos/empresa/${modalEmpresaId}?tamanho-pagina=200&pagina=0&ativo=true`, {
      headers: { Authorization: `Bearer ${token}` },
    }).then(r => r.json()).then(d => setProdutos(d.content ?? [])).catch(() => setProdutos([])).finally(() => setLoadingProd(false));
  }, [modalEmpresaId, token]);

  function addItem() { setItens(p => [...p, { ...ITEM_EMPTY }]); }
  function removeItem(i) { setItens(p => p.filter((_, idx) => idx !== i)); }
  async function setItemProduto(i, prod) {
    setItens(p => p.map((it, idx) => idx === i
      ? { ...it, produtoObj: prod, tributacaoObj: null, tributacoes: [] }
      : it));
    if (!prod || !modalEmpresaId) return;
    try {
      const url = `${API}/empresa/${modalEmpresaId}/produto-tributacao/produto/${prod.idProduto}`;
      console.log('[tributacao] buscando:', url);
      const res = await fetch(
        url,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (!res.ok) {
        console.error(`[tributacao] HTTP ${res.status} para produto ${prod.idProduto} empresa ${modalEmpresaId}`);
        setItens(p => p.map((it, idx) => idx === i ? { ...it, tributacoes: [] } : it));
        return;
      }
      const data = await res.json();
      console.log(`[tributacao] produto=${prod.idProduto} empresa=${modalEmpresaId} resultado=`, data);
      setItens(p => p.map((it, idx) => idx === i ? { ...it, tributacoes: Array.isArray(data) ? data : [] } : it));
    } catch (err) {
      console.error('[tributacao] erro de rede:', err);
      setItens(p => p.map((it, idx) => idx === i ? { ...it, tributacoes: [] } : it));
    }
  }
  function setItemQty(i, v) {
    setItens(p => p.map((it, idx) => idx === i ? { ...it, quantidade: v } : it));
  }
  function setItemTributacao(i, trib) {
    setItens(p => p.map((it, idx) => idx === i ? { ...it, tributacaoObj: trib } : it));
  }

  function setPag(k, v) { setPagamento(p => ({ ...p, [k]: v })); }
  const parcelavel = METODOS_PARCELAVEIS.has(pagamento.metodoPagamento);

  // Calcula total dos itens selecionados
  const totalItens = itens.reduce((sum, it) => {
    if (!it.produtoObj || !it.quantidade || isNaN(Number(it.quantidade))) return sum;
    return sum + Number(it.produtoObj.preco) * Number(it.quantidade);
  }, 0);

  // Prévia de parcelas
  const desconto = parsePreco(pagamento.desconto) || 0;
  const base = Math.max(0, totalItens - desconto);
  const maxParcelas = Math.max(1, Number(pagamento.parcelas) || 1);

  const previewParcelas = parcelavel && base > 0
    ? Array.from({ length: maxParcelas }, (_, i) => {
        const n = i + 1;
        const taxa = TAXAS_JUROS[pagamento.metodoPagamento] || 0;
        const total = n > 1 ? base * Math.pow(1 + taxa, n) : base;
        return { n, total, parcela: total / n };
      })
    : [];

  function validate() {
    const e = {};

    // ── Vínculo ──
    if (!modalEmpresaId)         e.empresa         = 'Selecione uma empresa';
    if (!modalEstabelecimentoId) e.estabelecimento = 'Selecione um estabelecimento';
    if (!consumidorSelecionado)  e.consumidor      = 'Selecione um consumidor';

    // ── Itens ──
    if (itens.length === 0) e.itens = 'Adicione ao menos um item';
    itens.forEach((it, i) => {
      if (!it.produtoObj) {
        e[`item_prod_${i}`] = `Item ${i + 1}: selecione um produto`;
      } else {
        const qty = Number(it.quantidade);
        if (!it.quantidade || isNaN(qty) || qty < 1) {
          e[`item_qty_${i}`] = `Item ${i + 1}: quantidade inválida`;
        } else if (!Number.isInteger(qty)) {
          e[`item_qty_${i}`] = `Item ${i + 1}: quantidade deve ser um número inteiro`;
        } else if (qty > (it.produtoObj.qtdEstoque ?? 0)) {
          e[`item_qty_${i}`] = `Item ${i + 1}: estoque insuficiente (disponível: ${it.produtoObj.qtdEstoque} un.)`;
        }
      }
    });

    // ── Desconto ──
    const descontoNum = parsePreco(pagamento.desconto) ?? 0;
    if (descontoNum < 0) {
      e.desconto = 'Desconto não pode ser negativo';
    } else if (totalItens > 0 && descontoNum >= totalItens) {
      e.desconto = `Desconto (${fmtMoeda(descontoNum)}) não pode ser maior ou igual ao total dos itens (${fmtMoeda(totalItens)})`;
    }

    // ── Pagamento ──
    if (!pagamento.metodoPagamento) {
      e.metodoPagamento = 'Método de pagamento é obrigatório';
    }

    if (pagamento.metodoPagamento !== 'SEM_PAGAMENTO') {
      const vrNum = parsePreco(pagamento.valorRecebido) ?? 0;

      if (!pagamento.valorRecebido || vrNum <= 0) {
        e.valorRecebido = 'Valor recebido deve ser maior que zero';
      } else {
        // Calcula quanto o cliente deve pagar agora
        const baseNum = Math.max(0, totalItens - descontoNum);
        const n       = parcelavel ? maxParcelas : 1;
        const taxa    = TAXAS_JUROS[pagamento.metodoPagamento] || 0;
        const totalComJuros  = n > 1 ? baseNum * Math.pow(1 + taxa, n) : baseNum;
        const valorPrimeiraParcela = totalComJuros / n;

        // Cartão de crédito: todas as parcelas são CONCLUIDO → precisa cobrir o total
        // Boleto / Crédito Loja: só a 1ª parcela é CONCLUIDO → cobre apenas a parcela
        const minEsperado = pagamento.metodoPagamento === 'CARTAO_CREDITO' || n === 1
          ? totalComJuros
          : valorPrimeiraParcela;

        if (baseNum > 0 && vrNum < minEsperado - 0.005) {
          const rotulo = (n > 1 && pagamento.metodoPagamento !== 'CARTAO_CREDITO')
            ? `valor da 1ª parcela (${fmtMoeda(minEsperado)})`
            : `total da venda (${fmtMoeda(minEsperado)})`;
          e.valorRecebido = `Valor recebido (${fmtMoeda(vrNum)}) é menor que o ${rotulo}`;
        }
      }
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
      const body = {
        consumidorId: consumidorSelecionado.id,
        vendedorId:   vendedorId ? Number(vendedorId) : null,
        emitirNfce,
        desconto,
        itens: itens.map(it => ({
          idProduto:  it.produtoObj.idProduto,
          quantidade: Number(it.quantidade),
          cfop:       it.tributacaoObj?.cfop
                        ? Number(it.tributacaoObj.cfop)
                        : (it.produtoObj?.cfop ? Number(it.produtoObj.cfop) : null),
        })),
        pagamento: {
          metodoPagamento: pagamento.metodoPagamento,
          valorRecebido:   parsePreco(pagamento.valorRecebido) ?? 0,
          desconto,
          statusPagamento: 'CONCLUIDO',
          parcelas:        parcelavel ? maxParcelas : 1,
        },
      };
      const res = await fetch(
        `${API}/vendas/empresa/${modalEmpresaId}/estabelecimento/${modalEstabelecimentoId}`,
        { method: 'POST', headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` }, body: JSON.stringify(body) }
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
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-5xl max-h-[92vh] flex flex-col">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <div>
            <h2 className="font-bold text-slate-800">Nova Venda</h2>
            <p className="text-xs text-slate-400 mt-0.5">Campos com <span className="text-red-400">*</span> são obrigatórios</p>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-700 transition-colors"><X size={20} /></button>
        </div>

        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6 space-y-6">
          {apiError && (
            <div className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl px-4 py-3">
              <AlertTriangle size={14} className="mt-0.5 flex-shrink-0" />{apiError}
            </div>
          )}
          {Object.keys(errors).length > 0 && (
            <div className="bg-amber-50 border border-amber-200 rounded-xl px-4 py-3">
              <p className="text-amber-700 text-xs font-semibold mb-1 flex items-center gap-1.5"><AlertTriangle size={13} /> Corrija os campos destacados:</p>
              <ul className="list-disc list-inside space-y-0.5">
                {Object.values(errors).map((msg, i) => <li key={i} className="text-amber-700 text-xs">{msg}</li>)}
              </ul>
            </div>
          )}

          {/* ── Empresa + Estabelecimento ── */}
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
              <Field k="estabelecimento" label="Estabelecimento" required errors={errors}>
                <select value={modalEstabelecimentoId} onChange={e => setModalEstabelecimentoId(e.target.value)} disabled={!modalEmpresaId || loadingEst} className={errCls('estabelecimento', errors)}>
                  <option value="">{loadingEst ? 'Carregando…' : modalEmpresaId ? 'Selecione…' : 'Selecione uma empresa primeiro'}</option>
                  {estabelecimentos.map(est => (
                    <option key={est.id} value={String(est.id)}>{est.nomeFantasia || est.cnpj || `#${est.id}`}</option>
                  ))}
                </select>
              </Field>
            </div>
          </section>

          {/* ── Consumidor ── */}
          <section>
            <p className="text-sm font-semibold text-slate-400 uppercase tracking-wide mb-3">Consumidor</p>
            <div className="space-y-3">
              <Field k="consumidor" label="Consumidor" required errors={errors}>
                <select
                  value={consumidorSelecionado?.id ?? ''}
                  onChange={e => {
                    const c = consumidores.find(c => String(c.id) === e.target.value) ?? null;
                    setConsumidorSelecionado(c);
                  }}
                  disabled={!modalEmpresaId || loadingCons}
                  className={errCls('consumidor', errors)}
                >
                  <option value="">{loadingCons ? 'Carregando…' : modalEmpresaId ? 'Selecione um consumidor…' : 'Selecione uma empresa primeiro'}</option>
                  {consumidores.map(c => (
                    <option key={c.id} value={String(c.id)}>
                      {c.nome}{c.cpf ? ` — ${fmtCpf(c.cpf)}` : ''}
                    </option>
                  ))}
                </select>
              </Field>

              {consumidorSelecionado && (
                <div className="bg-blue-50 border border-blue-100 rounded-xl px-4 py-3 grid grid-cols-3 gap-3">
                  <div>
                    <p className="text-sm text-slate-400 mb-0.5">Nome</p>
                    <p className="text-sm font-semibold text-slate-700">{consumidorSelecionado.nome ?? '—'}</p>
                  </div>
                  <div>
                    <p className="text-sm text-slate-400 mb-0.5">CPF</p>
                    <p className="text-sm font-semibold text-slate-700 font-mono">{fmtCpf(consumidorSelecionado.cpf)}</p>
                  </div>
                  <div>
                    <p className="text-sm text-slate-400 mb-0.5">Telefone</p>
                    <p className="text-sm font-semibold text-slate-700">{consumidorSelecionado.telefone ?? '—'}</p>
                  </div>
                </div>
              )}
            </div>
          </section>

          {/* ── Vendedor ── */}
          <section>
            <p className="text-sm font-semibold text-slate-400 uppercase tracking-wide mb-3">Vendedor</p>
            <Field k="vendedor" label="Vendedor responsável" errors={errors}>
              <select
                value={vendedorId}
                onChange={e => setVendedorId(e.target.value)}
                disabled={!modalEmpresaId}
                className={inputCls()}
              >
                <option value="">{modalEmpresaId ? 'Selecione um vendedor (opcional)…' : 'Selecione uma empresa primeiro'}</option>
                {vendedores.map(v => (
                  <option key={v.id} value={String(v.id)}>{v.nome}</option>
                ))}
              </select>
            </Field>
          </section>

          {/* ── Itens da Venda ── */}
          <section>
            <div className="flex items-center justify-between mb-3">
              <p className="text-sm font-semibold text-slate-400 uppercase tracking-wide">Itens da Venda</p>
              <button type="button" onClick={addItem} className="flex items-center gap-1 text-xs text-vulpes-orange font-medium hover:underline">
                <Plus size={13} /> Adicionar item
              </button>
            </div>
            {errors.itens && <p className="text-red-500 text-xs mb-2">{errors.itens}</p>}
            <div className="space-y-4">
              {itens.map((it, i) => (
                <div key={i} className="border border-slate-100 rounded-xl p-3 bg-slate-50/50">
                  <div className="grid grid-cols-12 gap-2 items-start">
                    {/* Produto select */}
                    <div className="col-span-5">
                      <label className={labelCls()}>Produto <span className="text-red-400">*</span></label>
                      <select
                        value={it.produtoObj?.idProduto ?? ''}
                        onChange={e => {
                          const prod = produtos.find(p => String(p.idProduto) === e.target.value) ?? null;
                          setItemProduto(i, prod);
                        }}
                        disabled={!modalEmpresaId || loadingProd}
                        className={errors[`item_prod_${i}`]
                          ? 'w-full border border-red-400 rounded-xl px-3 py-2 text-sm text-slate-700 bg-white focus:outline-none'
                          : 'w-full border border-slate-200 rounded-xl px-3 py-2 text-sm text-slate-700 bg-white focus:outline-none focus:border-vulpes-orange'}
                      >
                        <option value="">{loadingProd ? 'Carregando…' : 'Selecione…'}</option>
                        {produtos.map(p => (
                          <option key={p.idProduto} value={String(p.idProduto)}>
                            #{p.idProduto} — {p.descricao}
                          </option>
                        ))}
                      </select>
                      {errors[`item_prod_${i}`] && <p className="mt-1 text-red-500 text-sm">{errors[`item_prod_${i}`]}</p>}
                    </div>
                    {/* Qtd */}
                    <div className="col-span-3">
                      <label className={labelCls()}>Qtd. <span className="text-red-400">*</span></label>
                      <input
                        type="number" min="1"
                        value={it.quantidade}
                        onChange={e => setItemQty(i, e.target.value)}
                        className={errors[`item_qty_${i}`]
                          ? 'w-full border border-red-400 rounded-xl px-3 py-2 text-sm text-slate-700 bg-white focus:outline-none'
                          : 'w-full border border-slate-200 rounded-xl px-3 py-2 text-sm text-slate-700 bg-white focus:outline-none focus:border-vulpes-orange'}
                        placeholder="Qtd"
                      />
                      {errors[`item_qty_${i}`] && <p className="mt-1 text-red-500 text-sm">{errors[`item_qty_${i}`]}</p>}
                    </div>
                    {/* Tributação / CFOP */}
                    <div className="col-span-3">
                      <label className={labelCls()}>Tributação</label>
                      <select
                        value={it.tributacaoObj?.id ?? ''}
                        onChange={e => {
                          const trib = it.tributacoes.find(t => String(t.id) === e.target.value) ?? null;
                          setItemTributacao(i, trib);
                        }}
                        disabled={!it.produtoObj}
                        className="w-full border border-slate-200 rounded-xl px-3 py-2 text-sm text-slate-700 bg-white focus:outline-none focus:border-vulpes-orange disabled:bg-slate-50 disabled:text-slate-400"
                      >
                        <option value="">{it.produtoObj ? (it.tributacoes.length === 0 ? 'Nenhuma cadastrada' : 'Selecione…') : '—'}</option>
                        {it.tributacoes.map(t => (
                          <option key={t.id} value={String(t.id)}>
                            {t.nome ?? `UF: ${t.uf} — CFOP ${t.cfop}`}
                          </option>
                        ))}
                      </select>
                      {it.tributacaoObj && (
                        <p className="mt-1 text-xs text-slate-400">CFOP: <span className="font-semibold text-slate-600">{it.tributacaoObj.cfop}</span></p>
                      )}
                    </div>
                    {/* Remove */}
                    <div className="col-span-1 flex items-end justify-center pb-0.5">
                      {itens.length > 1 && (
                        <button type="button" onClick={() => removeItem(i)} className="p-2 text-red-400 hover:text-red-600 transition-colors">
                          <Trash2 size={14} />
                        </button>
                      )}
                    </div>
                  </div>

                  {/* Info do produto selecionado */}
                  {it.produtoObj && (
                    <div className="mt-2 grid grid-cols-3 gap-3 bg-white border border-slate-100 rounded-xl px-3 py-2.5">
                      <div>
                        <p className="text-sm text-slate-400">Descrição</p>
                        <p className="text-sm font-medium text-slate-700 truncate">{it.produtoObj.descricao}</p>
                      </div>
                      <div>
                        <p className="text-sm text-slate-400">Preço unit.</p>
                        <p className="text-sm font-semibold text-slate-700">{fmtMoeda(it.produtoObj.preco)}</p>
                      </div>
                      <div>
                        <p className="text-sm text-slate-400">Estoque</p>
                        <p className={`text-sm font-semibold ${it.produtoObj.qtdEstoque < 1 ? 'text-red-500' : it.produtoObj.qtdEstoque < 5 ? 'text-amber-600' : 'text-emerald-600'}`}>
                          {it.produtoObj.qtdEstoque} un.
                        </p>
                      </div>
                    </div>
                  )}
                </div>
              ))}
            </div>

            {totalItens > 0 && (
              <div className="mt-3 text-right text-sm font-semibold text-slate-600">
                Subtotal: <span className="text-slate-800">{fmtMoeda(totalItens)}</span>
              </div>
            )}
          </section>

          {/* ── Pagamento ── */}
          <section>
            <p className="text-sm font-semibold text-slate-400 uppercase tracking-wide mb-3">Pagamento</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="sm:col-span-2">
                <Field k="metodoPagamento" label="Método de Pagamento" required errors={errors}>
                  <select value={pagamento.metodoPagamento} onChange={e => setPag('metodoPagamento', e.target.value)} className={errCls('metodoPagamento', errors)}>
                    {METODOS_PAGAMENTO.map(m => <option key={m.value} value={m.value}>{m.label}</option>)}
                  </select>
                </Field>
              </div>
              <Field k="valorRecebido" label="Valor Recebido (R$)" required errors={errors}>
                <div className="relative">
                  <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-sm pointer-events-none">R$</span>
                  <input inputMode="numeric" value={pagamento.valorRecebido} onChange={e => setPag('valorRecebido', maskPreco(e.target.value))} className={`${errCls('valorRecebido', errors)} pl-9`} placeholder="0,00" />
                </div>
              </Field>
              <Field k="desconto" label="Desconto (R$)" errors={errors}>
                <div className="relative">
                  <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-sm pointer-events-none">R$</span>
                  <input inputMode="numeric" value={pagamento.desconto} onChange={e => setPag('desconto', maskPreco(e.target.value))} className={`${inputCls()} pl-9`} placeholder="0,00" />
                </div>
              </Field>

              {parcelavel && (
                <>
                  <div className="sm:col-span-2">
                    <Field k="parcelas" label="Número de Parcelas" errors={errors}>
                      <select value={pagamento.parcelas} onChange={e => setPag('parcelas', e.target.value)} className={inputCls()}>
                        {Array.from({ length: 24 }, (_, i) => i + 1).map(n => (
                          <option key={n} value={String(n)}>{n}x</option>
                        ))}
                      </select>
                    </Field>
                  </div>

                  {previewParcelas.length > 0 && (
                    <div className="sm:col-span-2">
                      <p className="text-sm font-medium text-slate-500 mb-2">Prévia do parcelamento</p>
                      <div className="border border-slate-200 rounded-xl overflow-hidden">
                        <div className="max-h-48 overflow-y-auto divide-y divide-slate-100">
                          {previewParcelas.map(({ n, total, parcela }) => (
                            <div key={n} className={`flex items-center justify-between px-4 py-2.5 text-sm ${n === maxParcelas ? 'bg-vulpes-orange/8 font-semibold' : 'hover:bg-slate-50'}`}>
                              <span className="text-slate-600 w-16">{n}x de</span>
                              <span className="text-slate-800 font-semibold">{fmtMoeda(parcela)}</span>
                              <span className="text-slate-400">= {fmtMoeda(total)}</span>
                              {n > 1 ? (
                                <span className="text-amber-600 text-xs">
                                  +{((Math.pow(1 + (TAXAS_JUROS[pagamento.metodoPagamento] || 0), n) - 1) * 100).toFixed(1)}% juros
                                </span>
                              ) : <span className="text-xs text-emerald-600">sem juros</span>}
                            </div>
                          ))}
                        </div>
                      </div>
                    </div>
                  )}
                </>
              )}
            </div>
          </section>

          {/* ── NFC-e ── */}
          <section>
            <div className="flex items-center gap-3">
              <input type="checkbox" id="emitirNfce" checked={emitirNfce} onChange={e => setEmitirNfce(e.target.checked)} className="w-4 h-4 rounded border-slate-300 accent-orange-500 cursor-pointer" />
              <label htmlFor="emitirNfce" className="text-sm text-slate-700 cursor-pointer select-none">Emitir NFC-e automaticamente ao salvar</label>
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
            {loading ? 'Registrando…' : 'Registrar venda'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── VendasPage ───────────────────────────────────────────────────────────────
export default function VendasPage() {
  const { token, user } = useAuth();

  const isRestrito = !user?.hasRole('ADMINISTRADOR', 'SUPORTE');

  const [empresaId,       setEmpresaId]       = useState(isRestrito ? String(user?.empresaId ?? '') : '');
  const [empresasOptions, setEmpresasOptions] = useState([]);

  const [filters, setFilters] = useState({
    consumidorId:     '',
    estabelecimentoId:'',
    dataInicio:       '',
    dataFim:          '',
  });

  const [result,      setResult]      = useState(null);
  const [page,        setPage]        = useState(0);
  const [sortCol,     setSortCol]     = useState(null);
  const [sortDir,     setSortDir]     = useState('desc');
  const [loading,     setLoading]     = useState(false);
  const [error,       setError]       = useState('');

  const [showCreate,  setShowCreate]  = useState(false);
  const [viewTarget,  setViewTarget]  = useState(null);
  const [toast,       setToast]       = useState(null);
  const [exportOpen,  setExportOpen]  = useState(false);
  const [exporting,   setExporting]   = useState(false);
  const exportRef = useRef(null);

  const setF = (k) => (e) => setFilters(p => ({ ...p, [k]: e.target.value }));

  useEffect(() => {
    if (isRestrito) return;
    fetch(`${API}/empresas?tamanho-pagina=100&pagina=0`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(r => r.json())
      .then(d => setEmpresasOptions(d.content ?? []))
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

  function isoDateTime(dateStr, endOfDay = false) {
    if (!dateStr) return null;
    return endOfDay ? `${dateStr}T23:59:59` : `${dateStr}T00:00:00`;
  }

  function buildParams(pg, col, dir) {
    const params = new URLSearchParams({ pagina: pg, 'tamanho-pagina': 10 });
    if (filters.consumidorId)      params.set('consumidor-id',       filters.consumidorId);
    if (filters.estabelecimentoId) params.set('estabelecimento-id',  filters.estabelecimentoId);
    const di = isoDateTime(filters.dataInicio);
    const df = isoDateTime(filters.dataFim, true);
    if (di) params.set('data-inicio', di);
    if (df) params.set('data-fim',    df);
    if (col) { params.set('ordenar-por', col); params.set('direcao', dir); }
    return params;
  }

  async function buscarComSort(pg = 0, col = sortCol, dir = sortDir) {
    setError('');
    setLoading(true);
    try {
      const params = buildParams(pg, col, dir);
      let url;
      if (isRestrito) {
        url = `${API}/vendas/empresa/${user?.empresaId}?${params}`;
      } else {
        if (empresaId) params.set('empresa-id', empresaId);
        url = `${API}/vendas?${params}`;
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
          url = `${API}/vendas/empresa/${user?.empresaId}?${params}`;
        } else {
          if (empresaId) params.set('empresa-id', empresaId);
          url = `${API}/vendas?${params}`;
        }
        const res = await fetch(url, { headers: { Authorization: `Bearer ${token}` } });
        if (!res.ok) throw new Error(`Erro ao buscar dados (página ${pg + 1}): ${res.status}`);
        const data = await res.json();
        allRows.push(...(data.content ?? []));
        if ((data.content?.length ?? 0) < PAGE_SIZE) break;
      }

      if (allRows.length === 0) { showToast('Nenhum dado retornado para exportar.', 'error'); return; }

      if (format === 'csv') {
        exportCsvVenda(allRows, 'vendas');
        showToast(`CSV exportado — ${allRows.length} registro(s)`);
      } else {
        exportXlsxVenda(allRows, 'vendas');
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
          onSaved={() => { setShowCreate(false); showToast('Venda registrada com sucesso!'); buscar(page); }}
        />
      )}

      {viewTarget && (
        <ViewModal
          venda={viewTarget}
          onClose={() => setViewTarget(null)}
        />
      )}

      {/* Cabeçalho */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-800 mb-1">Vendas</h1>
          <p className="text-slate-500 text-sm">Consulte e registre vendas no sistema.</p>
        </div>
        <button
          onClick={() => setShowCreate(true)}
          className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-white text-sm font-semibold hover:scale-[1.02] transition-all flex-shrink-0"
          style={{ background: '#1D4ED8' }}
        >
          <Plus size={16} />
          Nova venda
        </button>
      </div>

      {/* Filtros */}
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
        <h2 className="font-semibold text-slate-700 text-sm mb-4">Filtros de pesquisa</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
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
            <label className={labelCls()}>ID do Consumidor</label>
            <input type="number" value={filters.consumidorId} onChange={setF('consumidorId')} className={inputCls()} placeholder="Ex: 42" />
          </div>
          <div>
            <label className={labelCls()}>ID do Estabelecimento</label>
            <input type="number" value={filters.estabelecimentoId} onChange={setF('estabelecimentoId')} className={inputCls()} placeholder="Ex: 3" />
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
                {result.totalElements} venda(s) encontrada(s)
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
                    <button onClick={() => handleExport('csv')} className="w-full flex items-center gap-3 px-4 py-3 text-xs text-slate-700 hover:bg-slate-50 transition-colors">
                      <FileText size={14} className="text-emerald-500" /> Exportar como CSV
                    </button>
                    <button onClick={() => handleExport('xlsx')} className="w-full flex items-center gap-3 px-4 py-3 text-xs text-slate-700 hover:bg-slate-50 transition-colors border-t border-slate-50">
                      <FileSpreadsheet size={14} className="text-blue-500" /> Exportar como XLSX
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>

          {items.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-16 text-slate-400">
              <Search size={36} className="mb-3 opacity-40" />
              <p className="text-sm">Nenhuma venda encontrada com esses filtros.</p>
            </div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="bg-slate-50 border-b border-slate-100">
                      {[
                        { label: 'ID',         col: 'id',             px: 'px-6' },
                        { label: 'Consumidor', col: 'consumidorNome', px: 'px-4' },
                        { label: 'Valor Final',col: 'valorTotal',     px: 'px-4' },
                        { label: 'Parcelas',   col: 'parcelas',       px: 'px-4' },
                        { label: 'Método',     col: 'metodoPagamento',px: 'px-4' },
                        { label: 'Status Pag.',col: 'statusPagamento',px: 'px-4' },
                        { label: 'Data',       col: 'dataCriacao',    px: 'px-4' },
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
                    {items.map(v => (
                      <tr key={v.id} className="hover:bg-slate-50 transition-colors">
                        <td className="px-6 py-4 font-semibold text-slate-800 text-xs font-mono">#{v.id}</td>
                        <td className="px-4 py-4 text-slate-700 text-xs max-w-[180px] truncate">{v.consumidorNome ?? '—'}</td>
                        <td className="px-4 py-4 text-slate-700 text-xs font-semibold">{fmtMoeda(v.valorFinal ?? v.valorTotal)}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs text-center">{v.parcelas ?? 1}</td>
                        <td className="px-4 py-4 text-slate-500 text-xs">{labelMetodo(v.metodoPagamento)}</td>
                        <td className="px-4 py-4 text-xs">
                          <span className={`font-semibold px-2.5 py-1 rounded-full text-xs ${statusPagColor[v.statusPagamento] ?? 'bg-slate-100 text-slate-500'}`}>
                            {v.statusPagamento ?? '—'}
                          </span>
                        </td>
                        <td className="px-4 py-4 text-slate-500 text-xs whitespace-nowrap">{fmtDataHora(v.dataCriacao)}</td>
                        <td className="px-4 py-4 text-right">
                          <button
                            onClick={() => setViewTarget(v)}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-blue-500 hover:bg-blue-50 transition-colors"
                            title="Visualizar"
                          >
                            <Eye size={15} />
                          </button>
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
