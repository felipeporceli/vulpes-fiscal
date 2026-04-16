package com.vulpesfiscal.demo.security.oauth2;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Collections;
import java.util.Set;

public class OAuth2ResourceOwnerPasswordAuthenticationToken extends AbstractAuthenticationToken {

    public static final AuthorizationGrantType PASSWORD = new AuthorizationGrantType("password");

    private final Authentication clientPrincipal;
    private final Set<String> scopes;
    private final String username;
    private final String password;

    public OAuth2ResourceOwnerPasswordAuthenticationToken(
            Authentication clientPrincipal,
            Set<String> scopes,
            String username,
            String password) {
        super(Collections.emptyList());
        this.clientPrincipal = clientPrincipal;
        this.scopes = scopes != null ? Collections.unmodifiableSet(scopes) : Collections.emptySet();
        this.username = username;
        this.password = password;
    }

    @Override public Object getCredentials() { return ""; }
    @Override public Object getPrincipal()    { return clientPrincipal; }

    public AuthorizationGrantType getAuthorizationGrantType() { return PASSWORD; }
    public Authentication getClientPrincipal() { return clientPrincipal; }
    public Set<String> getScopes()             { return scopes; }
    public String getUsername()                { return username; }
    public String getPassword()                { return password; }
}
