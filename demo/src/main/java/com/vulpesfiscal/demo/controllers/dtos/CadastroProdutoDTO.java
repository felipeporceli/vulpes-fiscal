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

public record CadastroProdutoDTO(

        @NotNull(message = "Campo obrigatório")
        Integer idProduto,

        @NotBlank(message = "Campo obrigatório")
        String descricao,

        String codigoBarras,

        @NotNull(message = "Campo obrigatório")
        Integer ncm,

        @NotNull(message = "Campo obrigatório")
        Integer cfop,

        @NotBlank(message = "Campo obrigatório")
        String unidade,

        @NotNull(message = "Campo obrigatório")
        Double preco,

        @NotNull(message = "Campo obrigatório")
        Boolean ativo,

        @NotNull(message = "Campo obrigatório")
        Integer qtdEstoque

){
}
