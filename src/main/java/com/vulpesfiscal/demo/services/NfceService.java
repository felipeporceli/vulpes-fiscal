package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.config.FocusNfceProperties;
import com.vulpesfiscal.demo.exceptions.CampoInvalidoException;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaNfceDTO;
import com.vulpesfiscal.demo.controllers.dtos.focusnfe.FocusNfceFormaPagamentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.focusnfe.FocusNfceItemDTO;
import com.vulpesfiscal.demo.controllers.dtos.focusnfe.FocusNfceRequestDTO;
import com.vulpesfiscal.demo.controllers.dtos.focusnfe.FocusNfceResponseDTO;
import com.vulpesfiscal.demo.entities.*;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusNfce;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.NfceRepository;
import com.vulpesfiscal.demo.repositories.ProdutoTributacaoRepository;
import com.vulpesfiscal.demo.repositories.UsuarioRepository;
import com.vulpesfiscal.demo.security.SecurityService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NfceService {

    @PersistenceContext
    private EntityManager entityManager;

    private final NfceRepository nfceRepository;
    private final ProdutoTributacaoRepository produtoTributacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SecurityService securityService;
    private final FocusNfceProperties focusNfceProperties;
    private final RestTemplate restTemplate;

    private static final DateTimeFormatter FOCUS_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Transactional
    public void gerarNfce(Venda venda, Integer estabelecimentoId) {
        Estabelecimento estab = venda.getEstabelecimento();
        Empresa empresa = venda.getEmpresa();
        Consumidor consumidor = venda.getConsumidor();

        FocusNfceRequestDTO request = FocusNfceRequestDTO.builder()
                .cnpjEmitente(estab.getCnpj())
                .dataEmissao(OffsetDateTime.now().format(FOCUS_DATE_FORMAT))
                .naturezaOperacao("VENDA AO CONSUMIDOR")
                .modalidadeFrete("9")
                .localDestino(resolverLocalDestino(consumidor, estab))
                .presencaComprador("1")
                .nomeDestinatario(consumidor != null ? consumidor.getNome() : null)
                .cpfDestinatario(consumidor != null ? consumidor.getCpf() : null)
                .indicadorInscricaoEstadualDestinatario("9")
                .items(montarItens(venda, empresa))
                .formasPagamento(montarFormasPagamento(venda))
                .build();

        String login = securityService.obterLoginUsuarioLogado();
        Usuario usuarioLogado = usuarioRepository.findByEmail(login);

        Nfce nfceEntity = new Nfce();
        nfceEntity.setEmpresa(empresa);
        nfceEntity.setEstabelecimento(estab);
        nfceEntity.setUsuario(usuarioLogado);
        nfceEntity.setSerie(1);
        nfceEntity.setValorTotal(venda.getValorTotal());
        nfceEntity.setDataEmissao(OffsetDateTime.now());

        String token = empresa.getTokenFocusNfe();
        if (token == null || token.isBlank()) {
            throw new CampoInvalidoException("tokenFocusNfe",
                    "Token FocusNFE não configurado para a empresa. Configure via PATCH /empresas/{id}/token-focus-nfe");
        }

        String ref = "VF-" + venda.getId() + "-" + System.currentTimeMillis();

        String tokenMascarado = token.length() > 6
                ? token.substring(0, 3) + "***" + token.substring(token.length() - 3)
                : "***";
        try {
            log.info("[FocusNFE] Enviando NFC-e | ref={} | cnpj={} | token={} | mock={}", ref, request.getCnpjEmitente(), tokenMascarado, focusNfceProperties.isMock());
            FocusNfceResponseDTO response = focusNfceProperties.isMock()
                    ? mockResponse(ref)
                    : chamarFocusNfe(request, ref, token);
            log.info("[FocusNFE] Resposta | status={} | numero={} | chave={}", response.getStatus(), response.getNumero(), response.getChaveNfce());
            nfceEntity.setNumero(response.getNumero() != null ? response.getNumero() : ref);
            nfceEntity.setChaveAcesso(response.getChaveNfce());
            nfceEntity.setProtocoloAutorizacao(response.getProtocoloNota());
            nfceEntity.setStatusNfce(
                    "autorizado".equalsIgnoreCase(response.getStatus())
                            ? StatusNfce.AUTORIZADA
                            : StatusNfce.REJEITADA
            );
        } catch (Exception e) {
            log.error("[FocusNFE] Erro ao emitir NFC-e para venda {} | ref={} | erro={}", venda.getId(), ref, e.getMessage());
            nfceEntity.setNumero(ref);
            nfceEntity.setStatusNfce(StatusNfce.REJEITADA);
        }

        nfceRepository.save(nfceEntity);
    }

    // ─── Montagem dos itens ───────────────────────────────────────────────────

    private List<FocusNfceItemDTO> montarItens(Venda venda, Empresa empresa) {
        String ufEstab = venda.getEstabelecimento().getEstado();
        boolean simplesNacional = empresa.getRegimeTributario() == RegimeTributarioEmpresa.SIMPLES_NACIONAL
                || empresa.getRegimeTributario() == RegimeTributarioEmpresa.SIMPLES_EXCESSO_SUBLIMITE;

        List<FocusNfceItemDTO> itens = new ArrayList<>();
        int numeroItem = 1;

        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();

            Optional<ProdutoTributacao> trib = produtoTributacaoRepository
                    .findByEmpresaIdAndProdutoIdTecnicoAndUf(empresa.getId(), produto.getIdTecnico(), ufEstab);

            String icmsCst = trib.map(t -> simplesNacional ? t.getCsosnIcms() : t.getCstIcms())
                    .orElse(simplesNacional ? "400" : "00");
            String pisCst = trib.map(ProdutoTributacao::getCstPis).orElse("07");
            String cofinsCst = trib.map(ProdutoTributacao::getCstCofins).orElse("07");

            itens.add(FocusNfceItemDTO.builder()
                    .numeroItem(numeroItem++)
                    .codigoProduto(String.valueOf(produto.getIdProduto()))
                    .descricao(produto.getDescricao())
                    .cfop(String.valueOf(item.getCfop()))
                    .codigoNcm(produto.getNcm())
                    .unidadeComercial(produto.getUnidade())
                    .quantidadeComercial(item.getQuantidade())
                    .valorUnitarioComercial(item.getValorUnitario())
                    .valorBruto(item.getValorTotal())
                    .unidadeTributavel(produto.getUnidade())
                    .quantidadeTributavel(item.getQuantidade())
                    .valorUnitarioTributavel(item.getValorUnitario())
                    .icmsOrigem(produto.getOrig())
                    .icmsSituacaoTributaria(icmsCst)
                    .pisSituacaoTributaria(pisCst)
                    .cofinsSituacaoTributaria(cofinsCst)
                    .build());
        }

        return itens;
    }

    // ─── Montagem do pagamento ────────────────────────────────────────────────

    private List<FocusNfceFormaPagamentoDTO> montarFormasPagamento(Venda venda) {
        Pagamento pag = venda.getPagamento();
        return List.of(
                FocusNfceFormaPagamentoDTO.builder()
                        .formaPagamento(pag.getMetodoPagamento().getCodigoSefaz())
                        .valorPagamento(venda.getValorTotal())
                        .build()
        );
    }

    // ─── Mock para desenvolvimento sem certificado ────────────────────────────

    private FocusNfceResponseDTO mockResponse(String ref) {
        log.warn("[FocusNFE] MODO MOCK ativo — NFC-e não enviada à SEFAZ | ref={}", ref);
        FocusNfceResponseDTO mock = new FocusNfceResponseDTO();
        mock.setStatus("autorizado");
        mock.setNumero(ref);
        mock.setChaveNfce("MOCK-" + ref);
        mock.setProtocoloNota("MOCK-PROTOCOLO");
        return mock;
    }

    // ─── Chamada HTTP ao FocusNFE ─────────────────────────────────────────────

    private FocusNfceResponseDTO chamarFocusNfe(FocusNfceRequestDTO request, String ref, String token) {
        String credentials = token + ":";
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encoded);

        String url = focusNfceProperties.getUrl() + "/v2/nfce?ref=" + ref;
        HttpEntity<FocusNfceRequestDTO> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<FocusNfceResponseDTO> response =
                    restTemplate.postForEntity(url, entity, FocusNfceResponseDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("FocusNFE retornou erro HTTP {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private String resolverLocalDestino(Consumidor consumidor, Estabelecimento estab) {
        if (consumidor == null || consumidor.getUf() == null) return "1";
        return consumidor.getUf().equalsIgnoreCase(estab.getEstado()) ? "1" : "2";
    }

    // ─── Pesquisa (Criteria API) ──────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<ResultadoPesquisaNfceDTO> pesquisar(
            Integer empresaId,
            Integer estabelecimentoId,
            StatusNfce statusNfce,
            String chaveAcesso,
            String numero,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Integer pagina,
            Integer tamanhoPagina
    ) {
        int tamanho = Math.min(tamanhoPagina, 100);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Nfce> cq = cb.createQuery(Nfce.class);
        Root<Nfce> root = cq.from(Nfce.class);
        cq.select(root);

        List<Predicate> predicates = buildPredicates(cb, root,
                empresaId, estabelecimentoId, statusNfce, chaveAcesso, numero, dataInicio, dataFim);
        if (!predicates.isEmpty()) cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get("dataCriacao")));

        List<Nfce> lista = entityManager.createQuery(cq)
                .setFirstResult(pagina * tamanho)
                .setMaxResults(tamanho)
                .getResultList();

        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Nfce> countRoot = countCq.from(Nfce.class);
        countCq.select(cb.count(countRoot));
        List<Predicate> countPreds = buildPredicates(cb, countRoot,
                empresaId, estabelecimentoId, statusNfce, chaveAcesso, numero, dataInicio, dataFim);
        if (!countPreds.isEmpty()) countCq.where(countPreds.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countCq).getSingleResult();

        List<ResultadoPesquisaNfceDTO> dtos = lista.stream()
                .map(ResultadoPesquisaNfceDTO::fromEntity)
                .toList();

        return new PageImpl<>(dtos, PageRequest.of(pagina, tamanho), total);
    }

    @Transactional(readOnly = true)
    public ResultadoPesquisaNfceDTO buscarPorId(Integer id, Integer empresaId) {
        Nfce nfce = nfceRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("NFC-e não encontrada."));
        return ResultadoPesquisaNfceDTO.fromEntity(nfce);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Nfce> root,
                                             Integer empresaId, Integer estabelecimentoId,
                                             StatusNfce statusNfce, String chaveAcesso,
                                             String numero, LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<Predicate> predicates = new ArrayList<>();
        if (empresaId != null)         predicates.add(cb.equal(root.get("empresa").get("id"), empresaId));
        if (estabelecimentoId != null) predicates.add(cb.equal(root.get("estabelecimento").get("id"), estabelecimentoId));
        if (statusNfce != null)        predicates.add(cb.equal(root.get("statusNfce"), statusNfce));
        if (chaveAcesso != null && !chaveAcesso.isBlank())
                                       predicates.add(cb.like(root.get("chaveAcesso"), "%" + chaveAcesso + "%"));
        if (numero != null && !numero.isBlank())
                                       predicates.add(cb.equal(root.get("numero"), numero));
        if (dataInicio != null)        predicates.add(cb.greaterThanOrEqualTo(root.get("dataCriacao"), dataInicio));
        if (dataFim != null)           predicates.add(cb.lessThanOrEqualTo(root.get("dataCriacao"), dataFim));
        return predicates;
    }
}
