package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoConsumidorDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroConsumidorDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaConsumidorDTO;
import com.vulpesfiscal.demo.controllers.mappers.ConsumidorMapper;
import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.services.ConsumidorService;
import com.vulpesfiscal.demo.validator.ConsumidorValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consumidores")
@RequiredArgsConstructor
public class ConsumidorController implements ControllerGenerico{

    private final ConsumidorService service;
    private final ConsumidorMapper mapper;
    private final ConsumidorValidator validator;

    // Salvar nova Consumidor. Finalizando gerando a URL da nova entidade e entregando-a no header da response.
    @PostMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<Void> salvar (@RequestBody @Valid CadastroConsumidorDTO dto,
                                        @PathVariable Integer empresaId,
                                        @PathVariable Integer estabelecimentoId) {
        Consumidor consumidor = service.salvar(dto, empresaId, estabelecimentoId);
        var url = gerarHeaderLocation(consumidor.getId());
        return ResponseEntity.created(url).build();
    }

    /* Obter detalhes por ID obtendo filtros opcionais pela URL, busca Consumidors paginadas no banco e devolve o
    resultado convertido para DTO. */
    @GetMapping
    public ResponseEntity<Page<ResultadoPesquisaConsumidorDTO>> pesquisa (
            @RequestParam (value = "id", required = false)
            Integer id,

            @RequestParam (value = "cpf", required = false)
            String cpf,

            @RequestParam (value = "nome", required = false)
            String nome,

            @RequestParam (value = "email", required = false)
            String email,

            @RequestParam (value = "pagina", defaultValue = "0")
            Integer pagina,

            @RequestParam (value = "tamanho-pagina", defaultValue = "10")
            Integer tamanhoPagina
    ) {
        Page<Consumidor> paginaResultado = service.pesquisar(id, cpf, nome, email, pagina, tamanhoPagina);
        Page<ResultadoPesquisaConsumidorDTO> resultado = paginaResultado.map(mapper::toDTO);
        return ResponseEntity.ok(resultado);
    }


    /* Deletar Consumidor por id na URL, .map para seguir práticas Rest */
    @DeleteMapping("{id}")
    public void deletar (@PathVariable("id") Integer id) {
        service.deletar(id);
    }

    /* Atualizar Consumidor por id na URL, mas novos atributos no body da requisicao. */
    @PutMapping("{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable Integer id,
            @RequestBody AtualizacaoConsumidorDTO dto
    ) {
        Consumidor Consumidor = validator.pesquisarPorId(id);

        Consumidor dadosAtualizados = mapper.toEntityUpdate(dto, Consumidor);

        Consumidor.setNome(dadosAtualizados.getNome());
        Consumidor.setCpf(dadosAtualizados.getCpf());
        Consumidor.setEmail(dadosAtualizados.getEmail());

        service.atualizar(Consumidor);

        return ResponseEntity.noContent().build();
    }


}
