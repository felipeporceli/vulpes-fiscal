package com.vulpesfiscal.demo.security.oauth2;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.*;

import java.security.Principal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class OAuth2ResourceOwnerPasswordAuthenticationProvider implements AuthenticationProvider {

    private static final OAuth2TokenType ID_TOKEN_TOKEN_TYPE =
            new OAuth2TokenType(OidcParameterNames.ID_TOKEN);

    private final AuthenticationManager authenticationManager;
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    public OAuth2ResourceOwnerPasswordAuthenticationProvider(
            AuthenticationManager authenticationManager,
            OAuth2AuthorizationService authorizationService,
            OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2ResourceOwnerPasswordAuthenticationToken passwordAuth =
                (OAuth2ResourceOwnerPasswordAuthenticationToken) authentication;

        // Valida o client
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClient(passwordAuth);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        if (!registeredClient.getAuthorizationGrantTypes()
                .contains(OAuth2ResourceOwnerPasswordAuthenticationToken.PASSWORD)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        // Autentica o usuário com username + password
        Authentication userAuth;
        try {
            userAuth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            passwordAuth.getUsername(), passwordAuth.getPassword()));
        } catch (AuthenticationException ex) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT,
                            "Usuário ou senha inválidos.", null));
        }

        // Resolve scopes
        Set<String> authorizedScopes = registeredClient.getScopes();
        if (!passwordAuth.getScopes().isEmpty()) {
            for (String s : passwordAuth.getScopes()) {
                if (!registeredClient.getScopes().contains(s)) {
                    throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_SCOPE);
                }
            }
            authorizedScopes = new LinkedHashSet<>(passwordAuth.getScopes());
        }

        var grantType = OAuth2ResourceOwnerPasswordAuthenticationToken.PASSWORD;

        DefaultOAuth2TokenContext.Builder ctxBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(userAuth)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(authorizedScopes)
                .authorizationGrantType(grantType)
                .authorizationGrant(passwordAuth);

        OAuth2Authorization.Builder authBuilder = OAuth2Authorization
                .withRegisteredClient(registeredClient)
                .principalName(userAuth.getName())
                .authorizationGrantType(grantType)
                .authorizedScopes(authorizedScopes)
                .attribute(Principal.class.getName(), userAuth);

        // Access token
        OAuth2Token generatedAccess = tokenGenerator.generate(
                ctxBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build());
        if (generatedAccess == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "Falha ao gerar access token.", null));
        }
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                generatedAccess.getTokenValue(),
                generatedAccess.getIssuedAt(),
                generatedAccess.getExpiresAt(),
                authorizedScopes);
        if (generatedAccess instanceof ClaimAccessor ca) {
            authBuilder.token(accessToken, m -> m.put(
                    OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ca.getClaims()));
        } else {
            authBuilder.accessToken(accessToken);
        }

        // Refresh token
        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
            OAuth2Token generatedRefresh = tokenGenerator.generate(
                    ctxBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build());
            if (generatedRefresh instanceof OAuth2RefreshToken r) {
                refreshToken = r;
                authBuilder.refreshToken(refreshToken);
            }
        }

        // ID token (OIDC openid scope)
        OidcIdToken idToken = null;
        if (authorizedScopes.contains(OidcScopes.OPENID)) {
            OAuth2Token generatedId = tokenGenerator.generate(
                    ctxBuilder.tokenType(ID_TOKEN_TOKEN_TYPE)
                              .authorization(authBuilder.build())
                              .build());
            if (generatedId instanceof Jwt jwt) {
                // variável local effectively-final para uso no lambda abaixo
                OidcIdToken built = new OidcIdToken(jwt.getTokenValue(), jwt.getIssuedAt(),
                        jwt.getExpiresAt(), jwt.getClaims());
                idToken = built;
                authBuilder.token(built, m -> m.put(
                        OAuth2Authorization.Token.CLAIMS_METADATA_NAME, built.getClaims()));
            }
        }

        authorizationService.save(authBuilder.build());

        Map<String, Object> extra = idToken != null
                ? Map.of(OidcParameterNames.ID_TOKEN, idToken.getTokenValue())
                : Collections.emptyMap();

        return new OAuth2AccessTokenAuthenticationToken(
                registeredClient, clientPrincipal, accessToken, refreshToken, extra);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2ResourceOwnerPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private static OAuth2ClientAuthenticationToken getAuthenticatedClient(Authentication auth) {
        if (auth.getPrincipal() instanceof OAuth2ClientAuthenticationToken client
                && client.isAuthenticated()) {
            return client;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }
}
