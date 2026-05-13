<div align="center">

# Vulpes Fiscal

**Plataforma SaaS de emissão de NFC-e e gestão de vendas para o varejo brasileiro**

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-6DB33F?style=flat-square&logo=springboot)
![React](https://img.shields.io/badge/React-18-61DAFB?style=flat-square&logo=react)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)

</div>

---

## O Problema

Estabelecimentos comerciais no Brasil são **legalmente obrigados** a emitir NFC-e para cada venda ao consumidor final. A ausência ou falha na emissão gera multas, autuações fiscais e bloqueio de CNPJ. O processo vai muito além de "enviar um arquivo" — envolve:

- Cálculo correto de tributos (ICMS, PIS, COFINS, IBS/CBS) conforme o regime tributário da empresa
- Numeração sequencial única controlada por estabelecimento e série
- Comunicação em tempo real com a SEFAZ via API homologada
- Geração e entrega do DANFE (documento auxiliar em PDF) ao consumidor
- Controle de estoque sincronizado com cada venda

O **Vulpes Fiscal** resolve todos esses pontos em uma única plataforma: do cadastro do produto à autorização do documento fiscal, com dashboard de métricas e envio automático do DANFE por e-mail.

---

## Stack

**Backend** — Java 21 · Spring Boot 4.0.1 · Spring Security OAuth2 · Spring Data JPA · PostgreSQL 16 · MapStruct · Lombok · Hypersistence Utils · Flying Saucer/OpenPDF · ZXing · Thymeleaf · Spring Mail · SpringDoc OpenAPI

**Frontend** — React 18 · Vite · Tailwind CSS · Framer Motion · OAuth2 Authorization Code Flow

**Infraestrutura** — Docker Compose · nginx Alpine · Multi-stage Dockerfile (JDK 21 build → JRE 21 runtime)

---

## Arquitetura

```
┌──────────────────────────────────────────────────────────────┐
│                       nginx : 80                             │
│        React SPA (estático)  │  proxy reverso → :8080        │
└──────────────────────────────┬───────────────────────────────┘
                               │
┌──────────────────────────────▼───────────────────────────────┐
│                   Spring Boot : 8080                         │
│                                                              │
│   ┌─────────────────────┐   ┌──────────────────────────┐    │
│   │ OAuth2 Auth Server  │   │  OAuth2 Resource Server  │    │
│   │  JWT RSA-2048       │   │  @PreAuthorize / SpEL    │    │
│   └─────────────────────┘   └──────────────────────────┘    │
│                                                              │
│   Controllers → Services → Repositories                      │
│                    │                                         │
│              NFC-e Engine (16 classes)                       │
│     IdeService · EmitenteService · DestinatarioService       │
│     ProdService · ImpostoService · TotalService              │
│     ICMSService · PISService · CofinsService · IPIService    │
│                    │                                         │
│              FocusNFe API  →  SEFAZ                          │
└──────────────────────────────┬───────────────────────────────┘
                               │ JDBC
┌──────────────────────────────▼───────────────────────────────┐
│                  PostgreSQL 16                               │
│         multi-tenant: empresa_id em todas as tabelas         │
└──────────────────────────────────────────────────────────────┘
```

---

## Decisões Técnicas

### Multi-tenancy por coluna com dupla validação
Toda entidade carrega `empresa_id`. O isolamento é garantido em duas camadas independentes: `@PreAuthorize` nos endpoints (compara o claim `empresaId` do JWT com o path param) e queries de repositório sempre filtradas por empresa. Não há possibilidade de vazamento de dados entre clientes.

### OAuth2 Authorization Server embutido
O próprio Spring Boot serve como Authorization Server — sem Keycloak ou serviço externo. JWT assinado com RSA-2048 (chaves PEM no classpath). Refresh token rotativo com reúso desabilitado, TTL de 1h para access token e 7 dias para refresh. Uma peça a menos de infraestrutura para operar.

### NFC-e decomposta em 16 classes especializadas
O engine de montagem do XML NFC-e (leiaute SEFAZ 4.0) é particionado por responsabilidade: cada bloco do documento (`ide`, `emit`, `dest`, `det`, `imposto`, `total`, `transp`, `pag`) tem seu próprio serviço. Quando a SEFAZ publica uma nova versão do leiaute, a alteração fica localizada em uma única classe.

### Tributação por produto e UF de destino
`ProdutoTributacao` armazena CST/CSOSN, alíquotas de ICMS, PIS e COFINS por produto e por UF. Vendas interestaduais calculam tributação correta sem configuração manual por NFC-e.

### DANFE gerado inteiramente no servidor
Flying Saucer converte XHTML + CSS em PDF sem chamada externa. O template Thymeleaf (modo XML) segue o leiaute oficial SEFAZ. O QR Code é embutido como base64 antes da renderização. O envio por e-mail é feito com `@Async`, sem bloquear a resposta HTTP.

### Criptografia AES-256-GCM para o token da SEFAZ
O token de integração com a API FocusNFe é armazenado cifrado via `@Convert(converter = TokenEncryptionConverter.class)`. A chave de cifração é injetada por variável de ambiente e é obrigatória na inicialização — sem ela o container não sobe (falha explícita).

### Lazy loading global e explícito
Todos os `@ManyToOne` e `@OneToOne` são declarados `FetchType.LAZY`. Num modelo com autorreferência em campos de auditoria (`criadoPor` / `atualizadoPor` → `Usuario` → `Empresa` → `Usuario`...), sem lazy loading o Hibernate gerava queries com mais de 1.664 colunas — limite hard do PostgreSQL. Lazy explícito é a única solução sustentável nesse modelo.

### MapStruct em tempo de compilação
Nenhum mapeamento por reflexão em runtime. Os mappers são gerados pelo annotation processor do Maven; erros de mapeamento viram erros de compilação.

### Parcelas com juros compostos reais
O `VendaService` gera um registro `Pagamento` por parcela, com juros compostos calculados sobre o valor total (Cartão de Crédito: 2,99%/mês · Boleto: 1,99%/mês · Crédito Loja: 3,50%/mês), vencimentos no dia 20 dos meses seguintes e controle de status por parcela.

---

## Funcionalidades

- Emissão de NFC-e nos ambientes de homologação e produção (via FocusNFe)
- DANFE em PDF para download e envio por e-mail ao consumidor
- Gestão de vendas com desconto, parcelamento e controle de estoque em tempo real
- Múltiplos métodos de pagamento: PIX, dinheiro, cartão, boleto, crédito loja
- Dashboard com métricas do dia e gráfico de NFC-e dos últimos 7 dias
- Gestão de empresas, estabelecimentos (matriz/filiais) e usuários
- Catálogo de produtos com NCM, CFOP, código de barras e estoque
- Tributação configurável por produto e UF de destino
- Controle de acesso por perfil: `ADMINISTRADOR` `SUPORTE` `EMPRESARIO` `GERENTE` `CAIXA` `VENDEDOR`
- Dark mode com persistência de preferência
- Exportação de listagens em CSV/Excel

---

## Segurança

| Aspecto | Implementação |
|---|---|
| Senhas | BCrypt strength 10 |
| Tokens | JWT RSA-2048 · 1h access · 7d refresh rotativo |
| Autorização | `@PreAuthorize` com SpEL em todos os endpoints |
| Multi-tenancy | `empresaId` no JWT validado por request |
| Dados sensíveis | Token FocusNFe cifrado com AES-256-GCM no banco |
| Segredos | 100% via variáveis de ambiente |

---

## Estrutura do Projeto

```
vulpes-fiscal/
├── src/main/java/com/vulpesfiscal/demo/
│   ├── controllers/          # REST endpoints · DTOs · MapStruct mappers · JPA Specifications
│   ├── services/
│   │   ├── nfce/             # Engine NFC-e — 16 classes, uma por bloco do XML SEFAZ
│   │   │   └── det/imposto/  # ICMS · PIS · COFINS · IPI por regime tributário
│   │   ├── NfceService.java        # Orquestra emissão + integração FocusNFe
│   │   ├── DanfeNfceService.java   # Geração de DANFE em PDF
│   │   └── EmailService.java       # Envio assíncrono de e-mail
│   ├── entities/             # JPA entities + enums
│   ├── repositories/         # Spring Data JPA
│   ├── security/             # Auth Server · JWT customizer · CustomUserDetails
│   ├── configuration/        # Security · RSA · Async · OpenAPI
│   ├── exceptions/           # Hierarquia de exceções de negócio
│   └── validator/            # Validadores reutilizáveis
├── src/main/resources/
│   ├── templates/pdf/        # Template XHTML do DANFE (Thymeleaf modo XML)
│   └── certs/                # Chaves RSA PEM
├── frontend/
│   ├── src/pages/            # 10 páginas React (Dashboard · Vendas · NFC-e · Produtos…)
│   ├── src/context/          # AuthContext (OAuth2) · ThemeContext (dark mode)
│   └── nginx.conf            # Proxy reverso + SPA fallback
├── Dockerfile                # Multi-stage: JDK 21 build → JRE 21 Alpine runtime
├── frontend/Dockerfile       # Multi-stage: Node 20 build → nginx Alpine serve
└── docker-compose.yml        # Stack completa: db · backend · frontend
```

---

## Deploy em Homologação

> As emissões de NFC-e em homologação são enviadas à SEFAZ em modo de teste — nenhum documento fiscal real é gerado.

**Pré-requisito:** Docker e Docker Compose instalados.

**1. Clone o repositório e crie o arquivo de variáveis:**

```bash
git clone <url-do-repositorio>
cd vulpes-fiscal
cp .env.example .env
```

**2. Configure o `.env` com os valores mínimos para homologação:**

```env
DB_USERNAME=vulpesfiscal
DB_PASSWORD=vulpesfiscal
ENCRYPTION_SECRET_KEY=qualquer-chave-de-32-caracteres-aqui

# Homologação FocusNFe
FOCUSNFE_URL=https://homologacao.focusnfe.com.br
FOCUSNFE_MOCK=false

# Credenciais OAuth2 do frontend (devem coincidir com a tabela client no banco)
VITE_CLIENT_ID=client-homologacao
VITE_CLIENT_SECRET=secret-homologacao
VITE_REDIRECT_URI=http://localhost/callback
```

**3. Suba os containers:**

```bash
docker compose up -d --build
```

**4. Aguarde o banco inicializar e acesse:**

| Serviço | URL |
|---|---|
| Aplicação | `http://localhost` |
| API (Swagger) | `http://localhost:8080/swagger-ui.html` |

---

## Documentação da API

```
http://localhost:8080/swagger-ui.html
```

---

<div align="center">
<sub>Desenvolvido por Felipe Porceli Volpe</sub>
</div>
