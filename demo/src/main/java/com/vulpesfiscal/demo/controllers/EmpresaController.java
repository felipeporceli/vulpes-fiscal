package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaEmpresaDTO;
import com.vulpesfiscal.demo.controllers.mappers.EmpresaMapper;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import com.vulpesfiscal.demo.services.EmpresaService;
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

    // Salvar nova empresa. Finalizando gerando a URL da nova entidade e entregando-a no header da response.
    @PostMapping
    public ResponseEntity<Void> salvar (@RequestBody Empresa empresa) {
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
            String regimeTributario,

            @RequestParam (value = "status", required = false)
            StatusEmpresa statusEmpresa,

            @RequestParam (value = "pagina", defaultValue = "0")
            Integer pagina,

            @RequestParam (value = "tamanho-pagina", defaultValue = "10")
            Integer tamanhoPagina
    ) {
        Page<Empresa> paginaResultado = service.pesquisa(cnpj, razaoSocial, inscricaoEstadual, regimeTributario, statusEmpresa, pagina, tamanhoPagina);
        Page<ResultadoPesquisaEmpresaDTO> resultado = paginaResultado.map(mapper::toDTO);
        return ResponseEntity.ok(resultado);
    }



}
