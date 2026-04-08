package com.vulpesfiscal.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityService {

    public String obterLoginUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("Nenhum usuário autenticado encontrado.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Jwt jwt)) {
            throw new IllegalStateException(
                    "O principal autenticado não é um JWT. Tipo recebido: " + principal.getClass().getName()
            );
        }

        String login = jwt.getClaimAsString("email");

        if (login == null || login.isBlank()) {
            login = jwt.getSubject();
        }

        if (login == null || login.isBlank()) {
            throw new IllegalStateException("Não foi possível identificar o login do usuário no token.");
        }

        return login;
    }

    public Integer obterEmpresaId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("Nenhum usuário autenticado encontrado.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Jwt jwt)) {
            throw new IllegalStateException(
                    "O principal autenticado não é um JWT. Tipo recebido: " + principal.getClass().getName()
            );
        }

        Object empresaId = jwt.getClaim("empresaId");

        if (empresaId == null) {
            throw new IllegalStateException("Claim empresaId não encontrada no token.");
        }

        return Integer.valueOf(empresaId.toString());
    }
}