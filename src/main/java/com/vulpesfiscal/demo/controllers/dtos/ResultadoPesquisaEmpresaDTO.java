package com.vulpesfiscal.demo.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vulpesfiscal.demo.entities.enums.AmbienteSefazEmpresa;
import com.vulpesfiscal.demo.entities.enums.PorteEmpresa;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;

import java.time.LocalDate;
import java.util.List;

public record ResultadoPesquisaEmpresaDTO(
        Integer id,
        String razaoSocial,
        String nomeFantasia,
        String cnpj,
        String inscricaoEstadual,
        RegimeTributarioEmpresa regimeTributario,
        PorteEmpresa porte,
        AmbienteSefazEmpresa ambienteSefaz,
        StatusEmpresa status,
        String cnae,
        String uf,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate dataAbertura
) {}
