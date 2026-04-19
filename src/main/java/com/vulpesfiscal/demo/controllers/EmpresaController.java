package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoEmpresaDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroEmpresaDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaEmpresaDTO;
import com.vulpesfiscal.demo.controllers.mappers.EmpresaMapper;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.enums.PorteEmpresa;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import com.vulpesfiscal.demo.services.EmpresaService;
import com.vulpesfiscal.demo.validator.EmpresaValidator;
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

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
@Tag(name = "Empresas")
public class EmpresaController implements ControllerGenerico{

    private final EmpresaService service;
    private final EmpresaMapper mapper;
    private final EmpresaValidator validator;
    private final EmpresaRepository repository;


    // Salvar nova empresa. Finalizando gerando a URL da nova entidade e entregando-a no header da response.
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPORTE')")

    @Operation(summary = "Cadastrar nova Empresa no sistema.", description = "Cadastrar nova Empresa no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Empresa cadastrada com sucesso."),
            @ApiResponse(responseCode = "422", description = "Empresa já cadastrado com o CNPJ."),
            @ApiResponse(responseCode = "422", description = "Campo invalido ou obrigatorio"),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })

    @PostMapping
    public ResponseEntity<Void> salvar (@RequestBody @Valid CadastroEmpresaDTO dto) {
        Empresa empresa = mapper.toEntity(dto);
        service.salvar(empresa);
        var url = gerarHeaderLocation(empresa.getId());
        return ResponseEntity.created(url).build();
    }

    /* Pesquisa empresas com filtros opcionais. Para ADMINISTRADOR/SUPORTE: qualquer empresa.
       Para EMPRESARIO/GERENTE: apenas a empresa vinculada ao seu cadastro (empresaId do JWT). */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPORTE','EMPRESARIO','GERENTE')")

    @Operation(summary = "Obter informacoes da Empresa.", description = "Obter informacoes da Empresa por filtros.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })

    @GetMapping
    public ResponseEntity<Page<ResultadoPesquisaEmpresaDTO>> pesquisa (
            @RequestParam (value = "cnpj", required = false)
            String cnpj,

            @RequestParam (value = "razao-social", required = false)
            String razaoSocial,

            @RequestParam (value = "inscricao-estadual", required = false)
            String inscricaoEstadual,

            @RequestParam (value = "regime-tributario", required = false)
            RegimeTributarioEmpresa regimeTributario,

            @RequestParam (value = "status", required = false)
            StatusEmpresa status,

            @RequestParam (value = "porte", required = false)
            PorteEmpresa porte,

            @RequestParam (value = "pagina", defaultValue = "0")
            Integer pagina,

            @RequestParam (value = "tamanho-pagina", defaultValue = "10")
            Integer tamanhoPagina,

            @RequestParam (value = "empresa-id", required = false)
            Integer empresaId,

            @RequestParam (value = "ordenar-por", required = false)
            String ordenarPor,

            @RequestParam (value = "direcao", required = false, defaultValue = "asc")
            String direcao
    ) {
        Page<Empresa> paginaResultado = service.pesquisar(cnpj, razaoSocial, inscricaoEstadual, regimeTributario, status, porte, empresaId, pagina, tamanhoPagina, ordenarPor, direcao);
        Page<ResultadoPesquisaEmpresaDTO> resultado = paginaResultado.map(mapper::toDTO);
        return ResponseEntity.ok(resultado);
    }



    /* Deletar empresa por id na URL, .map para seguir práticas Rest */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPORTE')")

    @Operation(summary = "Deletar Empresa do sistema.", description = "Deletar Empresa do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empresa deletada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Empresa nao encontrada."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })

    @DeleteMapping("/{empresaId}")
    public void deletar (@PathVariable("empresaId") Integer empresaId) {
        service.deletar(empresaId);
    }

    /* Atualizar empresa por id na URL, mas novos atributos no body da requisicao. */

    @PutMapping("/{empresaId}")

    @Operation(summary = "Atualizar Empresa no sistema.", description = "Atualizar Empresa do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Empresa atualizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Empresa nao encontrada."),
            @ApiResponse(responseCode = "422", description = "Campo invalido ou obrigatorio."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    public ResponseEntity<Void> atualizar(
            @PathVariable Integer empresaId,
            @RequestBody @Valid AtualizacaoEmpresaDTO dto
    ) {
        validator.validarPesquisar(empresaId);
        service.atualizar(empresaId, dto);
        return ResponseEntity.noContent().build();
    }


}
