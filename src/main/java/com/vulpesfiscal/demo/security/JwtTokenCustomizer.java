package com.vulpesfiscal.demo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {
        var principal = context.getPrincipal();

        if (principal != null && principal.getPrincipal() instanceof CustomUserDetails userDetails) {
            var roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            // 👇 adiciona as roles e o userId no token
            context.getClaims()
                    .claim("roles", roles)
                    .claim("userId", userDetails.getId())
                    .claim("empresaId", userDetails.getEmpresaId());
        }
    }
}