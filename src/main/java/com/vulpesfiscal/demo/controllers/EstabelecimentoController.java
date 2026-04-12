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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Estabelecimentos")
public class EstabelecimentoController implements ControllerGenerico{

    private final EstabelecimentoService service;
    private final EstabelecimentoMapper mapper;
    private final EstabelecimentoValidator validator;
    private final EstabelecimentoRepository estabelecimentoRepository;


    /* Endpoint responsável por cadastrar um novo estabelecimento vinculado a uma empresa existente.
    O ID da empresa é informado na URL e os dados do estabelecimento são enviados no corpo da requisição.
    Em caso de sucesso, retorna HTTP 201 com a URL do novo recurso no header Location. */
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )

    @Operation(summary = "Cadastrar novo Estabelecimento no sistema", description = "Cadastrar novo estabelecimento vinculado à empresa no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Estabelecimento cadastrado com sucesso."),
            @ApiResponse(responseCode = "422", description = "Estabelecimento já cadastrado com o CNPJ."),
            @ApiResponse(responseCode = "422", description = "CNPJ invalido."),
            @ApiResponse(responseCode = "422", description = "Campo obrigatorio nao informado"),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
            @ApiResponse(responseCode = "404", description = "Empresa nao encontrada para o id informado."),

    })

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
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )

    @Operation(summary = "Obter informacoes do Estabelecimento.", description = "Obter informacoes do Estabelecimento por filtros")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "422", description = "Estabelecimento já cadastrado com o CNPJ."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),

    })

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
                tamanhoPagina,
                empresaId);
        Page<ResultadoPesquisaEstabelecimentoDTO> resultado = paginaResultado.map(mapper::toDTO);
        return ResponseEntity.ok(resultado);
    }


    /* Deletar Estabelecimento por id na URL, .map para seguir práticas Rest */
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )

    @Operation(summary = "Deletar Estabelecimento do sistema.", description = "Deletar Estabelecimento do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estabelecimento deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Estabelecimento ou Empresa nao encontrados."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),

    })


    @DeleteMapping("/empresa/{empresaId}/{id}")
    public void deletar (@PathVariable("id") Integer id,
                         @PathVariable("empresaId") Integer empresaId) {
        service.deletar(id);
    }

    /* Atualizar Estabelecimento por id na URL, mas novos atributos no body da requisicao. */
    @PreAuthorize(
            "(hasAnyRole('ADMINISTRADOR','SUPORTE')) or " +
                    "((hasAnyRole('EMPRESARIO')) and " +
                    "(#empresaId == authentication.principal.empresaId))"
    )

    @Operation(summary = "Atualizar Estabelecimento.", description = "Atualizar Estabelecimento do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estabelecimento atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Estabelecimento ou Empresa nao encontrados."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),

    })

    @PutMapping("/empresa/{empresaId}/{estabelecimentoId}")
    public ResponseEntity<Void> atualizar(
            @PathVariable Integer empresaId,
            @PathVariable Integer estabelecimentoId,
            @RequestBody @Valid AtualizacaoEstabelecimentoDTO dto
    ) {
        validator.validarPesquisar(empresaId, estabelecimentoId);
        service.atualizar(empresaId, estabelecimentoId, dto);
        return ResponseEntity.noContent().build();
    }


}
