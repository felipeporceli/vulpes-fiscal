package com.vulpesfiscal.demo.common;

import com.vulpesfiscal.demo.controllers.dtos.ErroCampo;
import com.vulpesfiscal.demo.controllers.dtos.ErroResposta;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErroResposta error = ErroResposta.builder()
                .status(403)
                .mensagem("Usuário não possui permissão para acessar este recurso")
                .erros(List.of(
                        ErroCampo.builder()
                                .campo("autorizacao")
                                .erro("Acesso negado para este recurso")
                                .build()
                ))
                .build();

        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
