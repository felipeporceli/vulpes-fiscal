package com.vulpesfiscal.demo.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OAuth2ResourceOwnerPasswordAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        // Só processa grant_type=password
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!"password".equals(grantType)) {
            return null;
        }

        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return null;
        }

        Set<String> scopes = new HashSet<>();
        String scopeParam = request.getParameter(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scopeParam)) {
            scopes.addAll(Arrays.asList(scopeParam.split(" ")));
        }

        return new OAuth2ResourceOwnerPasswordAuthenticationToken(clientPrincipal, scopes, username, password);
    }
}
