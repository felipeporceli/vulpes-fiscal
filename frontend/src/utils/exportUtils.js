/**
 * exportUtils.js — Utilitários de exportação seguros para CSV e XLSX.
 *
 * ── Nota de segurança sobre a biblioteca xlsx (SheetJS) ──────────────────────
 * O pacote `xlsx` possui dois CVEs registrados:
 *   • GHSA-4r6h-8v6p-xvw6 — Prototype Pollution
 *   • GHSA-5pgg-2g8v-p4x9 — ReDoS
 *
 * AMBOS os vetores de ataque estão no PARSER (XLSX.read / XLSX.readFile),
 * acionados ao processar um arquivo externo/malicioso fornecido pelo atacante.
 * Este módulo NUNCA chama read() ou readFile() — usa exclusivamente
 * XLSX.utils.aoa_to_sheet() + XLSX.write(), operações de ESCRITA com dados
 * que nós mesmos geramos. Portanto, as vulnerabilidades não são exploráveis
 * neste contexto. Referência: CWE-1321 (Prototype Pollution via object merge).
 *
 * ── Demais medidas de segurança implementadas ────────────────────────────────
 *
 * 1. CSV/Formula Injection Prevention (OWASP)
 *    Valores que começam com '=', '+', '-', '@', '\t' ou '\r' são prefixados
 *    com apóstrofo, impedindo que o Excel os interprete como fórmulas.
 *
 * 2. Tipo de célula String forçado no XLSX
 *    Toda célula de dados recebe `cell.t = 's'` explicitamente.
 *    Mesmo que um valor comece com '=', o Excel o trata como texto puro.
 *
 * 3. RFC 4180 — Escaping correto de CSV
 *    Valores com vírgula, aspas ou quebras de linha são envolvidos em aspas
 *    duplas; aspas internas são duplicadas ("").
 *
 * 4. UTF-8 BOM no CSV
 *    Garante que o Excel abrirá caracteres acentuados sem corromper a
 *    codificação ao importar o arquivo.
 *
 * 5. Revogação imediata de Object URL
 *    URL.revokeObjectURL() é chamado logo após o clique de download para
 *    liberar memória e evitar vazamento de referência no DOM.
 *
 * 6. Cap de 10.000 registros por exportação
 *    Protege contra exportações que consumam memória excessiva no cliente.
 *
 * 7. Nome de arquivo sanitizado + timestamp
 *    Caracteres especiais são removidos do nome base; sufixo ISO timestamp
 *    evita colisões e sobrescrita acidental.
 */

import * as XLSX from 'xlsx';

export const EXPORT_MAX_ROWS = 10_000;

// ─── Colunas exportadas (ordem e largura) ────────────────────────────────────
// NOTA DE SEGURANÇA: o campo `id` (PK do banco) é intencionalmente omitido.
// Expor IDs sequenciais facilita ataques de enumeração e revela a estrutura
// interna do banco de dados.
export const EXPORT_COLUMNS = [
  { header: 'Razão Social',       key: 'razaoSocial'       },
  { header: 'Nome Fantasia',      key: 'nomeFantasia'      },
  { header: 'CNPJ',               key: 'cnpj'              },
  { header: 'Inscrição Estadual', key: 'inscricaoEstadual' },
  { header: 'Regime Tributário',  key: 'regimeTributario'  },
  { header: 'Porte',              key: 'porte'             },
  { header: 'UF',                 key: 'uf'                },
  { header: 'Ambiente SEFAZ',     key: 'ambienteSefaz'     },
  { header: 'Status',             key: 'status'            },
  { header: 'Data de Abertura',   key: 'dataAbertura'      },
  { header: 'CNAE',               key: 'cnae'              },
];

// ─── Colunas de Estabelecimento (o campo `id` é intencionalmente omitido) ────
export const ESTABELECIMENTO_EXPORT_COLUMNS = [
  { header: 'Nome Fantasia',       key: 'nomeFantasia'       },
  { header: 'CNPJ',                key: 'cnpj'               },
  { header: 'Telefone',            key: 'telefone'           },
  { header: 'E-mail',              key: 'email'              },
  { header: 'Inscrição Estadual',  key: 'inscricaoEstadual'  },
  { header: 'Inscrição Municipal', key: 'inscricaoMunicipal' },
  { header: 'Logradouro',          key: 'logradouro'         },
  { header: 'Número',              key: 'numero'             },
  { header: 'Bairro',              key: 'bairro'             },
  { header: 'Cidade',              key: 'cidade'             },
  { header: 'Estado',              key: 'estado'             },
  { header: 'CEP',                 key: 'cep'                },
  { header: 'Status',              key: 'status'             },
  { header: 'Tipo',                key: 'matriz'             },
  { header: 'Data de Abertura',    key: 'dataAbertura'       },
  { header: 'Data de Fechamento',  key: 'dataFechamento'     },
];

// ─── Colunas de Consumidor (o campo `id` é intencionalmente omitido) ─────────
export const CONSUMIDOR_EXPORT_COLUMNS = [
  { header: 'Nome',               key: 'nome'              },
  { header: 'CPF',                key: 'cpf'               },
  { header: 'E-mail',             key: 'email'             },
  { header: 'Telefone',           key: 'telefone'          },
  { header: 'Município',          key: 'municipio'         },
  { header: 'UF',                 key: 'uf'                },
  { header: 'CEP',                key: 'cep'               },
  { header: 'Logradouro',         key: 'logradouro'        },
  { header: 'Número',             key: 'numero'            },
  { header: 'Bairro',             key: 'bairro'            },
  { header: 'Inscrição Estadual', key: 'inscricaoEstadual' },
];

// ─── Helpers internos ────────────────────────────────────────────────────────

/** Prefixos que o Excel interpreta como início de fórmula */
const FORMULA_PREFIXES = ['=', '+', '-', '@', '\t', '\r'];

/**
 * Converte qualquer valor para string segura.
 * - Nulos/undefined → string vazia
 * - Prefixos perigosos → prefixados com apóstrofo (CSV injection prevention)
 */
function sanitize(val) {
  if (val === null || val === undefined) return '';
  const s = String(val).trim();
  if (s.length > 0 && FORMULA_PREFIXES.some(p => s.startsWith(p))) {
    return `'${s}`;
  }
  return s;
}

/** Formata CNPJ: XX.XXX.XXX/XXXX-XX */
function fmtCnpj(v) {
  if (!v) return '';
  const d = String(v).replace(/\D/g, '');
  return d.length === 14
    ? d.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5')
    : d;
}

/** Formata CPF: XXX.XXX.XXX-XX */
function fmtCpf(v) {
  if (!v) return '';
  const d = String(v).replace(/\D/g, '');
  return d.length === 11
    ? d.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
    : d;
}

/** Mapeia empresa → array de strings sanitizadas na ordem de EXPORT_COLUMNS.
 *  O campo `id` (PK do banco) é explicitamente excluído da exportação. */
function toRow(e) {
  return [
    sanitize(e.razaoSocial),
    sanitize(e.nomeFantasia),
    sanitize(fmtCnpj(e.cnpj)),
    sanitize(e.inscricaoEstadual),
    sanitize(e.regimeTributario),
    sanitize(e.porte),
    sanitize(e.uf),
    sanitize(e.ambienteSefaz),
    sanitize(e.status),
    sanitize(e.dataAbertura),
    sanitize(e.cnae),
  ];
}

/** Mapeia estabelecimento → array de strings sanitizadas na ordem de ESTABELECIMENTO_EXPORT_COLUMNS. */
function toRowEstabelecimento(e) {
  return [
    sanitize(e.nomeFantasia),
    sanitize(fmtCnpj(e.cnpj)),
    sanitize(e.telefone),
    sanitize(e.email),
    sanitize(e.inscricaoEstadual),
    sanitize(e.inscricaoMunicipal),
    sanitize(e.logradouro),
    sanitize(e.numero),
    sanitize(e.bairro),
    sanitize(e.cidade),
    sanitize(e.estado),
    sanitize(e.cep),
    sanitize(e.status),
    sanitize(e.matriz ? 'Matriz' : 'Filial'),
    sanitize(e.dataAbertura),
    sanitize(e.dataFechamento),
  ];
}

/** Mapeia consumidor → array de strings sanitizadas na ordem de CONSUMIDOR_EXPORT_COLUMNS.
 *  O campo `id` (PK do banco) é explicitamente excluído da exportação. */
function toRowConsumidor(c) {
  return [
    sanitize(c.nome),
    sanitize(fmtCpf(c.cpf)),
    sanitize(c.email),
    sanitize(c.telefone),
    sanitize(c.municipio),
    sanitize(c.uf),
    sanitize(c.cep),
    sanitize(c.logradouro),
    sanitize(c.numero),
    sanitize(c.bairro),
    sanitize(c.inscricaoEstadual),
  ];
}

/** Nome de arquivo com timestamp ISO (sem chars especiais) */
function buildFilename(base) {
  const safeName = base.replace(/[^a-zA-Z0-9_-]/g, '_');
  const ts = new Date().toISOString().slice(0, 19).replace(/[T:]/g, '-');
  return `${safeName}_${ts}`;
}

/** Cria Blob e dispara download; revoga o URL imediatamente */
function triggerDownload(blob, filename) {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.style.display = 'none';
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url); // libera memória imediatamente
}

// ─── Exportação CSV ──────────────────────────────────────────────────────────

/** Escapa um valor para CSV (RFC 4180 + fórmula injection prevention) */
function csvEscape(val) {
  const s = sanitize(val);
  if (s.includes(',') || s.includes('"') || s.includes('\n') || s.includes('\r')) {
    return `"${s.replace(/"/g, '""')}"`;
  }
  return s;
}

/** Lógica interna de exportação CSV — reutilizada por empresas e consumidores */
function exportCsvInternal(data, columns, toRowFn, baseName) {
  if (!Array.isArray(data) || data.length === 0) {
    throw new Error('Nenhum dado para exportar.');
  }

  const headers = columns.map(c => csvEscape(c.header)).join(',');
  const rows = data
    .slice(0, EXPORT_MAX_ROWS)
    .map(item => toRowFn(item).map(csvEscape).join(','));

  // \uFEFF = UTF-8 BOM
  const content = '\uFEFF' + [headers, ...rows].join('\r\n');
  const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' });
  triggerDownload(blob, `${buildFilename(baseName)}.csv`);
}

/**
 * Exporta array de empresas como CSV.
 * - UTF-8 BOM para compatibilidade com Excel
 * - RFC 4180 compliant
 * - CSV/Formula injection prevention
 */
export function exportCsv(data, baseName = 'empresas') {
  exportCsvInternal(data, EXPORT_COLUMNS, toRow, baseName);
}

/**
 * Exporta array de consumidores como CSV.
 */
export function exportCsvConsumidor(data, baseName = 'consumidores') {
  exportCsvInternal(data, CONSUMIDOR_EXPORT_COLUMNS, toRowConsumidor, baseName);
}

// ─── Exportação XLSX ─────────────────────────────────────────────────────────

/** Lógica interna de exportação XLSX — reutilizada por empresas e consumidores */
function exportXlsxInternal(data, columns, toRowFn, colWidths, sheetName, baseName) {
  if (!Array.isArray(data) || data.length === 0) {
    throw new Error('Nenhum dado para exportar.');
  }

  const headers = columns.map(c => c.header);
  const rows    = data.slice(0, EXPORT_MAX_ROWS).map(toRowFn);

  // aoa_to_sheet: array de arrays → sem inferência automática de tipo
  const ws = XLSX.utils.aoa_to_sheet([headers, ...rows]);

  // ── Força tipo String em todas as células de dados (pula linha 0 = header) ─
  const range = XLSX.utils.decode_range(ws['!ref']);
  for (let R = range.s.r + 1; R <= range.e.r; R++) {
    for (let C = range.s.c; C <= range.e.c; C++) {
      const addr = XLSX.utils.encode_cell({ r: R, c: C });
      if (!ws[addr]) continue;
      ws[addr].t = 's'; // 's' = string; impede fórmula mesmo com '=' no valor
    }
  }

  ws['!cols']   = colWidths;
  ws['!freeze'] = { xSplit: 0, ySplit: 1 };

  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, sheetName);

  const buf = XLSX.write(wb, { type: 'array', bookType: 'xlsx' });
  const blob = new Blob([buf], {
    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  });
  triggerDownload(blob, `${buildFilename(baseName)}.xlsx`);
}

/**
 * Exporta array de empresas como XLSX.
 *
 * Usa XLSX.utils.aoa_to_sheet (array-of-arrays) — a abordagem mais segura
 * pois evita qualquer inferência de tipo sobre os dados.
 * Todas as células de dados têm tipo 's' (string) explicitamente definido,
 * prevenindo execução de fórmulas no Excel/LibreOffice independente do valor.
 */
export function exportXlsx(data, baseName = 'empresas') {
  exportXlsxInternal(data, EXPORT_COLUMNS, toRow, [
    { wch: 42 }, // Razão Social
    { wch: 32 }, // Nome Fantasia
    { wch: 20 }, // CNPJ
    { wch: 20 }, // Inscrição Estadual
    { wch: 26 }, // Regime Tributário
    { wch: 16 }, // Porte
    { wch: 6  }, // UF
    { wch: 16 }, // Ambiente SEFAZ
    { wch: 12 }, // Status
    { wch: 18 }, // Data de Abertura
    { wch: 14 }, // CNAE
  ], 'Empresas', baseName);
}

/**
 * Exporta array de consumidores como XLSX.
 */
export function exportXlsxConsumidor(data, baseName = 'consumidores') {
  exportXlsxInternal(data, CONSUMIDOR_EXPORT_COLUMNS, toRowConsumidor, [
    { wch: 36 }, // Nome
    { wch: 16 }, // CPF
    { wch: 32 }, // E-mail
    { wch: 16 }, // Telefone
    { wch: 24 }, // Município
    { wch: 6  }, // UF
    { wch: 12 }, // CEP
    { wch: 36 }, // Logradouro
    { wch: 8  }, // Número
    { wch: 20 }, // Bairro
    { wch: 20 }, // Inscrição Estadual
  ], 'Consumidores', baseName);
}

/** Exporta array de estabelecimentos como CSV. */
export function exportCsvEstabelecimento(data, baseName = 'estabelecimentos') {
  exportCsvInternal(data, ESTABELECIMENTO_EXPORT_COLUMNS, toRowEstabelecimento, baseName);
}

/** Exporta array de estabelecimentos como XLSX. */
export function exportXlsxEstabelecimento(data, baseName = 'estabelecimentos') {
  exportXlsxInternal(data, ESTABELECIMENTO_EXPORT_COLUMNS, toRowEstabelecimento, [
    { wch: 32 }, // Nome Fantasia
    { wch: 20 }, // CNPJ
    { wch: 18 }, // Telefone
    { wch: 32 }, // E-mail
    { wch: 20 }, // Inscrição Estadual
    { wch: 20 }, // Inscrição Municipal
    { wch: 36 }, // Logradouro
    { wch: 8  }, // Número
    { wch: 20 }, // Bairro
    { wch: 24 }, // Cidade
    { wch: 8  }, // Estado
    { wch: 12 }, // CEP
    { wch: 12 }, // Status
    { wch: 10 }, // Tipo
    { wch: 18 }, // Data de Abertura
    { wch: 18 }, // Data de Fechamento
  ], 'Estabelecimentos', baseName);
}
