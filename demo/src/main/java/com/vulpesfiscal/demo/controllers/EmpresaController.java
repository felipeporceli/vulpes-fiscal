package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.CadastroEmpresaDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaEmpresaDTO;
import com.vulpesfiscal.demo.controllers.mappers.EmpresaMapper;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.enums.PorteEmpresa;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import com.vulpesfiscal.demo.services.EmpresaService;
import com.vulpesfiscal.demo.validator.EmpresaValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaController implements ControllerGenerico{

    private final EmpresaService service;
    private final EmpresaMapper mapper;
    private final EmpresaValidator validator;

    // Salvar nova empresa. Finalizando gerando a URL da nova entidade e entregando-a no header da response.
    @PostMapping
    public ResponseEntity<Void> salvar (@RequestBody @Valid CadastroEmpresaDTO dto) {
        Empresa empresa = mapper.toEntity(dto);
        System.out.println(dto.ambienteSefaz());
        System.out.println(empresa.getAmbienteSefaz());
        service.salvar(empresa);
        var url = gerarHeaderLocation(empresa.getId());
        return ResponseEntity.created(url).build();
    }

    /* Obter detalhes por ID obtendo filtros opcionais pela URL, busca empresas paginadas no banco e devolve o
    resultado convertido para DTO. */
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
            Integer tamanhoPagina
    ) {
        Page<Empresa> paginaResultado = service.pesquisar(cnpj, razaoSocial, inscricaoEstadual, regimeTributario, status, porte, pagina, tamanhoPagina);
        Page<ResultadoPesquisaEmpresaDTO> resultado = paginaResultado.map(mapper::toDTO);
        return ResponseEntity.ok(resultado);
    }


    /* Deletar empresa por id na URL, .map para seguir práticas Rest */
    @DeleteMapping("{id}")
    public void deletar (@PathVariable("id") String cnpj) {
        service.deletar(cnpj);
    }

    /* Atualizar empresa por id na URL, mas novos atributos no body da requisicao. */
    @PutMapping("{cnpj}")
    public ResponseEntity<Void> atualizar(
            @PathVariable String cnpj,
            @RequestBody CadastroEmpresaDTO dto
    ) {
        Empresa empresa = service.pesquisarPorCnpj(cnpj);

        Empresa dadosAtualizados = mapper.toEntity(dto);

        empresa.setCnpj(dadosAtualizados.getCnpj());
        empresa.setStatus(dadosAtualizados.getStatus());
        empresa.setRazaoSocial(dadosAtualizados.getRazaoSocial());
        empresa.setRegimeTributario(dadosAtualizados.getRegimeTributario());
        empresa.setInscricaoEstadual(dadosAtualizados.getInscricaoEstadual());
        empresa.setDataAbertura(dadosAtualizados.getDataAbertura());
        empresa.setNomeFantasia(dadosAtualizados.getNomeFantasia());
        empresa.setPorte(dadosAtualizados.getPorte());

        service.atualizar(empresa);

        return ResponseEntity.noContent().build();
    }


}
