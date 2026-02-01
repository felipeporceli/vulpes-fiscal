package com.vulpesfiscal.demo.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vulpesfiscal.demo.entities.enums.AmbienteSefazEmpresa;
import com.vulpesfiscal.demo.entities.enums.PorteEmpresa;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.br.CNPJ;

import java.time.LocalDate;

public record CadastroEmpresaDTO (

        @NotBlank(message = "Campo obrigatório")
        String razaoSocial,

        String nomeFantasia,

        @NotBlank(message = "Campo obrigatório")
        String cnpj,

        @NotBlank(message = "Campo obrigatório")
        String inscricaoEstadual,

        @NotNull(message = "Campo obrigatório")
        RegimeTributarioEmpresa regimeTributario,

        @NotNull(message = "Campo obrigatório")
        StatusEmpresa status,

        @NotNull(message = "Campo obrigatório")
        PorteEmpresa porte,

        @NotNull(message = "Campo obrigatório")
        AmbienteSefazEmpresa ambienteSefaz,

        @JsonFormat(pattern = "dd/MM/yyyy")
        @NotNull
        @Past(message = "Não pode ser data futura")
        LocalDate dataAbertura
){
}
