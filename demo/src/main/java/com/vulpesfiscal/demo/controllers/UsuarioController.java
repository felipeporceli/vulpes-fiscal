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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController implements ControllerGenerico {

    private final UsuarioService service;
    private final UsuarioMapper mapper;
    private final UsuarioValidator validator;

    // Salvar novo Usuario. Finalizando gerando a URL da nova entidade e entregando-a no header da response.
    @PostMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<Void> salvar (
            @RequestBody @Valid CadastroUsuarioDTO dto,
            @PathVariable Integer empresaId,
            @PathVariable Integer estabelecimentoId) {
        Usuario usuario = mapper.toEntity(dto);
        service.salvar(empresaId, estabelecimentoId, dto);
        var url = gerarHeaderLocation(usuario.getId());
        return ResponseEntity.created(url).build();
    }

    /* Obter detalhes por ID obtendo filtros opcionais pela URL, busca produtos paginados no banco e devolve o
    resultado convertido para DTO. */
    @GetMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<Page<ResultadoPesquisaUsuarioDTO>> pesquisa (
            @RequestParam (value = "id", required = false)
            Integer id,

            @RequestParam (value = "perfil-id", required = false)
            Integer perfilId,

            @RequestParam (value = "nome", required = false)
            String nome,

            @RequestParam (value = "email", required = false)
            String email,

            @RequestParam (value = "ativo", required = false)
            Boolean ativo,

            @RequestParam (value = "pagina", defaultValue = "0")
            Integer pagina,

            @RequestParam (value = "tamanho-pagina", defaultValue = "10")
            Integer tamanhoPagina,

            @PathVariable Integer empresaId,

            @PathVariable Integer estabelecimentoId
    ) {
        Page<Usuario> paginaResultado = service.pesquisar(id, perfilId, nome, email, ativo, empresaId, estabelecimentoId, pagina, tamanhoPagina);
        Page<ResultadoPesquisaUsuarioDTO> resultado = paginaResultado.map(mapper::toDTO);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}/{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable Integer id,
            @RequestBody AtualizacaoUsuarioDTO dto,
            @PathVariable Integer empresaId,
            @PathVariable Integer estabelecimentoId
    ) {
        Usuario usuario = validator.pesquisarPorId(id);
        validator.validarPesquisar(empresaId, estabelecimentoId);
        Usuario dadosAtualizados = mapper.toEntityUpdate(dto, usuario);

        usuario.setNome(dadosAtualizados.getNome());
        usuario.setId(dadosAtualizados.getId());
        usuario.setEmail(dadosAtualizados.getEmail());
        usuario.setSenha(dadosAtualizados.getSenha());
        usuario.setAtivo(dadosAtualizados.getAtivo());
        usuario.setEstabelecimento(dadosAtualizados.getEstabelecimento());
        usuario.setEmpresa(dadosAtualizados.getEmpresa());
        usuario.setPerfilId(dadosAtualizados.getPerfilId());

        service.atualizar(usuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}/{id}")
    public void deletar (
            @PathVariable Integer id,
            @PathVariable Integer empresaId,
            @PathVariable Integer estabelecimentoId
    ) {
        service.deletar(id, empresaId, estabelecimentoId);
    }

}
