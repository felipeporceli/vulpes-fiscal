package com.vulpesfiscal.demo.controllers.dtos;

/* Essa classe é responsável por armazenar métodos e informações usadas para o tratamento de exceções do nosso sistema a
 Nível de controle (controller). Ou seja, respostas HTTPs que estejam de acordo com o contrato. */

import org.springframework.http.HttpStatus;

import java.util.List;

public record ErroResposta (Integer status, String mensagem, List<ErroCampo> erros) {

    /* Metodo que retorna um objeto do tipo ErroResposta que contém o código 400 (bad request). Vamos utilizar esse
    metodo para retornamos ao consumidor da API um erro padrão */
    public static ErroResposta respostaPadrao (String mensagem) {
        return new ErroResposta(HttpStatus.BAD_REQUEST.value(), mensagem, List.of());
    }


    /* Metodo que retorna um objeto do tipo ErroResposta que contém o código 409 (Conflict). Vamos utilizar esse
   metodo para retornamos ao consumidor quando uma entidade já existir */
    public static ErroResposta respostaConflito (String mensagem) {
        return new ErroResposta(HttpStatus.CONFLICT.value(), mensagem, List.of());
    }
}
