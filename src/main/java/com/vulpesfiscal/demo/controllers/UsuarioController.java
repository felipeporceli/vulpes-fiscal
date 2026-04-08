package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.*;
import com.vulpesfiscal.demo.controllers.mappers.UsuarioMapper;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.Usuario;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import com.vulpesfiscal.demo.services.UsuarioService;
import com.vulpesfiscal.demo.validator.UsuarioValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios")
public class UsuarioController implements ControllerGenerico {

    private final UsuarioService service;
    private final UsuarioMapper mapper;
    private final UsuarioValidator validator;


    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPORTE')")
    @PostMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}")
    @Operation(summary = "Salvar usuário", description = "Cadastrar novo usuário no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cadastro realizado com sucesso."),
            @ApiResponse(responseCode = "409", description = "Usuário já cadastrado no sistema com esse e-mail."),
            @ApiResponse(responseCode = "404", description = "Empresa ou Estabelecimento não encontrado(s)"),
            @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para esse recurso")

    })
    public ResponseEntity<Void> salvar(
            @RequestBody @Valid CadastroUsuarioDTO dto,
            @PathVariable Integer empresaId,
            @PathVariable Integer estabelecimentoId) {

        Usuario usuario = mapper.toEntity(dto);
        Usuario salvo = service.salvar(empresaId, estabelecimentoId, usuario);
        var url = gerarHeaderLocation(salvo.getId());
        return ResponseEntity.created(url).build();
    }

    /* Obter detalhes por ID obtendo filtros opcionais pela URL, busca produtos paginados no banco e devolve o
    resultado convertido para DTO. */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPORTE')")
    @GetMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}")
    @Operation(summary = "Obter informações de usuário", description = "Obter informações do usuário por filtros.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Empresa ou Estabelecimento não encontrado(s)."),
            @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para esse recurso"),

    })
    public ResponseEntity<Page<ResultadoPesquisaUsuarioDTO>> pesquisa (
            @RequestParam (value = "id", required = false)
            Integer id,

            @RequestParam (value = "perfil-id", required = false)
            Integer perfilId,

            @RequestParam (value = "nome", required = false)
            String nome,

            @RequestParam (value = "username", required = false)
            String username,

            @RequestParam (value = "cpf", required = false)
            String cpf,

            @RequestParam (value = "username", required = false)
            String roles,

            @RequestParam (value = "email", required = false)
            String email,

            @RequestParam (value = "telefone", required = false)
            String telefone,

            @RequestParam (value = "ativo", required = false)
            Boolean ativo,

            @RequestParam (value = "pagina", defaultValue = "0")
            Integer pagina,

            @RequestParam (value = "tamanho-pagina", defaultValue = "10")
            Integer tamanhoPagina,

            @PathVariable Integer empresaId,

            @PathVariable Integer estabelecimentoId
    ) {
        Page<Usuario> paginaResultado = service.pesquisar(id, perfilId, nome, email, ativo, empresaId,
                estabelecimentoId, username, cpf, roles, telefone, pagina, tamanhoPagina);
        Page<ResultadoPesquisaUsuarioDTO> resultado = paginaResultado.map(mapper::toDTO);
        return ResponseEntity.ok(resultado);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPORTE')")
    @PutMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}/{id}")
    @Operation(summary = "Atualizar informacoes do usuario", description = "Atualizar informacao do usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Empresa ou Estabelecimento nao encontrado(s)."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),

    })

    public ResponseEntity<Void> atualizar(
            @PathVariable Integer id,
            @RequestBody AtualizacaoUsuarioDTO dto,
            @PathVariable Integer empresaId,
            @PathVariable Integer estabelecimentoId
    ) {
        validator.validarPesquisar(empresaId, estabelecimentoId);
        service.atualizar(id, empresaId, estabelecimentoId, dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPORTE')")
    @DeleteMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}/{id}")
    @Operation(summary = "Deletar usuario", description = "Deletar usuario do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado(s)."),
            @ApiResponse(responseCode = "404", description = "Empresa ou Estabelecimento nao encontrado(s)."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),

    })
    public void deletar (
            @PathVariable Integer id,
            @PathVariable Integer empresaId,
            @PathVariable Integer estabelecimentoId
    ) {
        service.deletar(id, empresaId, estabelecimentoId);
    }

}
