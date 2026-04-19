package com.vulpesfiscal.demo.security;

import com.vulpesfiscal.demo.entities.Client;
import com.vulpesfiscal.demo.services.ClientService;
import lombok.RequiredArgsConstructor;
import com.vulpesfiscal.demo.security.oauth2.OAuth2ResourceOwnerPasswordAuthenticationToken;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    private final ClientService clientService;
    private final TokenSettings tokenSettings;
    private final ClientSettings clientSettings;

    @Override
    public void save(RegisteredClient registeredClient) {}

    @Override
    public RegisteredClient findById(String id) {
        return null;
    }

    @Override
    public RegisteredClient findByClientId(String id) {
        System.out.println(">>> findByClientId chamado com: " + id);

        Client client = clientService.obterPorId(id);

        System.out.println(">>> client encontrado: " + client.getClientId());

        if (client == null) {
            return null;
        }

        return RegisteredClient
                .withId(client.getId().toString())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret()) // 👈 remove o "{bcrypt}" daqui
                .redirectUri(client.getRedirectURI())
                .scope(OidcScopes.OPENID)
                .scope(client.getScope())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(OAuth2ResourceOwnerPasswordAuthenticationToken.PASSWORD)
                .tokenSettings(tokenSettings)
                .clientSettings(clientSettings)
                .build();
    }
}
