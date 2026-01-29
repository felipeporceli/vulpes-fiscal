package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.mappers.EstabelecimentoMapper;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import com.vulpesfiscal.demo.services.EstabelecimentoService;
import com.vulpesfiscal.demo.validator.EstabelecimentoValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/estabelecimentos")
@RequiredArgsConstructor
public class EstabelecimentoController implements ControllerGenerico{

    private final EstabelecimentoService service;
    private final EstabelecimentoMapper mapper;
    private final EstabelecimentoValidator validator;


    /* Endpoint responsável por cadastrar um novo estabelecimento vinculado a uma empresa existente.
    O ID da empresa é informado na URL e os dados do estabelecimento são enviados no corpo da requisição.
    Em caso de sucesso, retorna HTTP 201 com a URL do novo recurso no header Location. */
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
    @GetMapping
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
            Integer tamanhoPagina
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
    @DeleteMapping("{id}")
    public void deletar (@PathVariable("id") String cnpj) {
        service.deletar(cnpj);
    }

    /* Atualizar Estabelecimento por id na URL, mas novos atributos no body da requisicao. */
    @PutMapping("{cnpj}")
    public ResponseEntity<Void> atualizar(
            @PathVariable String cnpj,
            @RequestBody AtualizacaoEstabelecimentoDTO dto
    ) {
        Estabelecimento estabelecimento = validator.pesquisarPorCnpj(cnpj);

        Estabelecimento dadosAtualizados = mapper.toEntityUpdate(dto, estabelecimento);

        estabelecimento.setNomeFantasia(dadosAtualizados.getNomeFantasia());
        estabelecimento.setTelefone(dadosAtualizados.getTelefone());
        estabelecimento.setEmail(dadosAtualizados.getEmail());
        estabelecimento.setEstado(dadosAtualizados.getEstado());
        estabelecimento.setStatus(dadosAtualizados.getStatus());
        estabelecimento.setInscricaoEstadual(dadosAtualizados.getInscricaoEstadual());
        estabelecimento.setMatriz(dadosAtualizados.isMatriz());
        estabelecimento.setDataAbertura(dadosAtualizados.getDataAbertura());
        estabelecimento.setInscricaoMunicipal(dadosAtualizados.getInscricaoMunicipal());

        service.atualizar(estabelecimento);

        return ResponseEntity.noContent().build();
    }


}
