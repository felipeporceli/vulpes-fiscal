package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoConsumidorDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroConsumidorDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaConsumidorDTO;
import com.vulpesfiscal.demo.controllers.mappers.ConsumidorMapper;
import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.services.ConsumidorService;
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
@RequestMapping("/consumidores")
@RequiredArgsConstructor
@Tag(name = "Consumidores")
public class ConsumidorController implements ControllerGenerico {

    private final ConsumidorService service;
    private final ConsumidorMapper mapper;

    // Salvar novo Consumidor. Finalizando gerando a URL da nova entidade e entregando-a no header da response.
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Cadastrar novo Consumidor no sistema.", description = "Cadastrar novo Consumidor no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consumidor cadastrado com sucesso."),
            @ApiResponse(responseCode = "409", description = "Ja existe Consumidor cadastrado com o CPF."),
            @ApiResponse(responseCode = "422", description = "Campo invalido ou obrigatorio."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })
    @PostMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<Void> salvar(
            @RequestBody @Valid CadastroConsumidorDTO dto,
            @PathVariable Integer empresaId,
            @PathVariable Integer estabelecimentoId) {
        Consumidor consumidor = service.salvar(dto, empresaId, estabelecimentoId);
        var url = gerarHeaderLocation(consumidor.getId());
        return ResponseEntity.created(url).build();
    }

    /* Pesquisa global de consumidores — exclusiva para ADMINISTRADOR e SUPORTE.
       O filtro empresa-id é opcional: se omitido, retorna consumidores de todas as empresas. */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPORTE')")
    @Operation(summary = "Pesquisa global de Consumidores.", description = "Pesquisa consumidores sem restrição de empresa (ADMINISTRADOR/SUPORTE).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })
    @GetMapping
    public ResponseEntity<Page<ResultadoPesquisaConsumidorDTO>> pesquisaGlobal(
            @RequestParam(value = "empresa-id",  required = false)                     Integer empresaId,
            @RequestParam(value = "id",           required = false)                     Integer id,
            @RequestParam(value = "cpf",          required = false)                     String cpf,
            @RequestParam(value = "nome",         required = false)                     String nome,
            @RequestParam(value = "email",        required = false)                     String email,
            @RequestParam(value = "cep",          required = false)                     String cep,
            @RequestParam(value = "uf",           required = false)                     String uf,
            @RequestParam(value = "municipio",    required = false)                     String municipio,
            @RequestParam(value = "telefone",     required = false)                     String telefone,
            @RequestParam(value = "pagina",       defaultValue = "0")                   Integer pagina,
            @RequestParam(value = "tamanho-pagina", defaultValue = "10")               Integer tamanhoPagina,
            @RequestParam(value = "ordenar-por",  required = false)                     String ordenarPor,
            @RequestParam(value = "direcao",      required = false, defaultValue = "asc") String direcao
    ) {
        Page<Consumidor> paginaResultado = service.pesquisar(
                empresaId, id, cpf, nome, email, pagina, uf, municipio, cep, telefone, tamanhoPagina, ordenarPor, direcao
        );
        return ResponseEntity.ok(paginaResultado.map(mapper::toDTO));
    }

    /* Obter detalhes por ID obtendo filtros opcionais pela URL, busca Consumidores paginados no banco e devolve o
    resultado convertido para DTO. */
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Obter informacoes do Consumidor.", description = "Obter informacoes do Consumidor por filtros.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Consumidor nao encontrado."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<Page<ResultadoPesquisaConsumidorDTO>> pesquisa(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "cep", required = false) String cep,
            @RequestParam(value = "uf", required = false) String uf,
            @RequestParam(value = "municipio", required = false) String municipio,
            @RequestParam(value = "telefone", required = false) String telefone,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "tamanho-pagina", defaultValue = "10") Integer tamanhoPagina,
            @RequestParam(value = "ordenar-por", required = false) String ordenarPor,
            @RequestParam(value = "direcao", required = false, defaultValue = "asc") String direcao,
            @PathVariable Integer empresaId
    ) {
        Page<Consumidor> paginaResultado = service.pesquisar(
                empresaId, id, cpf, nome, email, pagina, uf, municipio, cep, telefone, tamanhoPagina, ordenarPor, direcao
        );
        Page<ResultadoPesquisaConsumidorDTO> resultado = paginaResultado.map(mapper::toDTO);
        return ResponseEntity.ok(resultado);
    }

    /* Deletar Consumidor por id na URL. */
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Deletar Consumidor do sistema.", description = "Deletar Consumidor do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Consumidor deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Consumidor nao encontrado."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })
    @DeleteMapping("/empresa/{empresaId}/{consumidorId}")
    public void deletar(
            @PathVariable Integer empresaId,
            @PathVariable Integer consumidorId) {
        service.deletar(consumidorId, empresaId);
    }

    /* Atualizar Consumidor por id na URL, com novos atributos no body da requisição. */
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Atualizar Consumidor no sistema.", description = "Atualizar Consumidor do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Consumidor atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Consumidor nao encontrado."),
            @ApiResponse(responseCode = "422", description = "Campo invalido ou obrigatorio."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })
    @PutMapping("/empresa/{empresaId}/{consumidorId}")
    public ResponseEntity<Void> atualizar(
            @PathVariable Integer empresaId,
            @PathVariable Integer consumidorId,
            @RequestBody @Valid AtualizacaoConsumidorDTO dto) {
        service.atualizar(consumidorId, empresaId, dto);
        return ResponseEntity.noContent().build();
    }
}