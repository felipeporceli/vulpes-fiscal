package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.mappers.EstabelecimentoMapper;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.EstabelecimentoRepository;
import com.vulpesfiscal.demo.services.EstabelecimentoService;
import com.vulpesfiscal.demo.validator.EstabelecimentoValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/estabelecimentos")
@RequiredArgsConstructor
public class EstabelecimentoController implements ControllerGenerico{

    private final EstabelecimentoService service;
    private final EstabelecimentoMapper mapper;
    private final EstabelecimentoValidator validator;
    private final EstabelecimentoRepository estabelecimentoRepository;


    /* Endpoint responsável por cadastrar um novo estabelecimento vinculado a uma empresa existente.
    O ID da empresa é informado na URL e os dados do estabelecimento são enviados no corpo da requisição.
    Em caso de sucesso, retorna HTTP 201 com a URL do novo recurso no header Location. */
    @PreAuthorize(
            "(hasAnyRole('ADMINISTRADOR','SUPORTE')) or " +
                    "((hasAnyRole('EMPRESARIO')) and " +
                    "(#empresaId == authentication.principal.empresaId))"
    )
    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<Void> salvar(
            @PathVariable Integer empresaId,
            @RequestBody @Valid CadastroEstabelecimentoDTO dto
    ) {
        Estabelecimento estabelecimento = service.salvar(empresaId, dto);
        URI location = gerarHeaderLocation(estabelecimento.getId());
        return ResponseEntity.created(location).build();
    }

    /* Obter detalhes por ID obtendo filtros opcionais pela URL, busca Estabelecimentos paginadas no banco e devolve o
    resultado convertido para DTO. */
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasRole('EMPRESARIO') and #empresaId.toString() == authentication.principal.claims['empresaId'].toString())"
    )
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<Page<ResultadoPesquisaEstabelecimentoDTO>> pesquisa (
            @RequestParam (value = "cnpj", required = false)
            String cnpj,

            @RequestParam (value = "nome-fantasia", required = false)
            String nomeFantasia,

            @RequestParam (value = "cidade", required = false)
            String cidade,

            @RequestParam (value = "estado", required = false)
            String estado,

            @RequestParam (value = "status", required = false)
            StatusEmpresa status,

            @RequestParam (value = "matriz", required = false)
            Boolean matriz,

            @RequestParam (value = "pagina", defaultValue = "0")
            Integer pagina,

            @RequestParam (value = "tamanho-pagina", defaultValue = "10")
            Integer tamanhoPagina,

            @PathVariable Integer empresaId
    ) {
        Page<Estabelecimento> paginaResultado = service.pesquisar(
                cnpj,
                nomeFantasia,
                cidade,
                estado,
                status,
                matriz,
                pagina,
                tamanhoPagina);
        Page<ResultadoPesquisaEstabelecimentoDTO> resultado = paginaResultado.map(mapper::toDTO);
        return ResponseEntity.ok(resultado);
    }


    /* Deletar Estabelecimento por id na URL, .map para seguir práticas Rest */
    @PreAuthorize(
            "(hasAnyRole('ADMINISTRADOR','SUPORTE')) or " +
                    "((hasAnyRole('EMPRESARIO')) and " +
                    "(#empresaId == authentication.principal.empresaId))"
    )
    @DeleteMapping("/empresa/{empresaId}/{id}")
    public void deletar (@PathVariable("id") String cnpj,
                         @PathVariable("empresaId") Integer empresaId) {
        service.deletar(cnpj);
    }

    /* Atualizar Estabelecimento por id na URL, mas novos atributos no body da requisicao. */
    @PreAuthorize(
            "(hasAnyRole('ADMINISTRADOR','SUPORTE')) or " +
                    "((hasAnyRole('EMPRESARIO')) and " +
                    "(#empresaId == authentication.principal.empresaId))"
    )
    @PutMapping("/empresa/{empresaId}/{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable Integer empresaId,
            @PathVariable Integer id,
            @RequestBody @Valid AtualizacaoEstabelecimentoDTO dto
    ) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estabelecimento nao encontrado para o id informado"));

        Estabelecimento dadosAtualizados = mapper.toEntityUpdate(dto, estabelecimento);

        estabelecimento.setNomeFantasia(dadosAtualizados.getNomeFantasia());
        estabelecimento.setTelefone(dadosAtualizados.getTelefone());
        estabelecimento.setEmail(dadosAtualizados.getEmail());
        estabelecimento.setEstado(dadosAtualizados.getEstado());
        estabelecimento.setStatus(dadosAtualizados.getStatus());
        estabelecimento.setInscricaoEstadual(dadosAtualizados.getInscricaoEstadual());
        estabelecimento.setMatriz(dadosAtualizados.isMatriz());
        estabelecimento.setDataAbertura(dadosAtualizados.getDataAbertura());
        estabelecimento.setCidade(dadosAtualizados.getCidade());
        estabelecimento.setEstado(dadosAtualizados.getEstado());
        estabelecimento.setNumero(dadosAtualizados.getNumero());
        estabelecimento.setComplemento(dadosAtualizados.getComplemento());
        estabelecimento.setBairro(dadosAtualizados.getMunicipioId());
        estabelecimento.setCep(dadosAtualizados.getCep());
        estabelecimento.setPaisId(dadosAtualizados.getPaisId());
        estabelecimento.setPais(dadosAtualizados.getPais());
        estabelecimento.setCodUf(dadosAtualizados.getCodUf());
        estabelecimento.setDataFechamento(dadosAtualizados.getDataFechamento());

        service.atualizar(estabelecimento);

        return ResponseEntity.noContent().build();
    }


}
