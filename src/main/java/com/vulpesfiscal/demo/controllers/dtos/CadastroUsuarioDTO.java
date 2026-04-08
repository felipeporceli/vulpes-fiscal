package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.aspectj.bridge.IMessage;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "Usuario")
public record CadastroUsuarioDTO(

        @NotNull(message = "Campo obrigatório")
        Integer perfilId,

        @NotBlank(message = "Campo obrigatório")
        String nome,

        @NotBlank(message = "Campo obrigatório")
        String email,

        @NotBlank(message = "Campo obrigatório")
        String senha,

        @NotBlank(message = "Campo obrigatório")
        String username,

        @NotNull(message = "Campo obrigatório")
        Boolean ativo,

        @NotNull(message = "Campo obrigatorio")
        String cpf,

        @NotNull(message = "Campo obrigatorio")
        String telefone,

        @NotNull(message = "Campo obrigatório")
        List<String> roles

){
}
