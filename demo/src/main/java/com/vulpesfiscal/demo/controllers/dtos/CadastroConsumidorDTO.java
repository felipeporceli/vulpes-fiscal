package com.vulpesfiscal.demo.controllers.dtos;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record CadastroConsumidorDTO(

        @NotBlank(message = "Campo obrigatório")
        @CPF
        String cpf,

        String nome,

        @NotBlank(message = "Campo obrigatório")
        String email

){
}
