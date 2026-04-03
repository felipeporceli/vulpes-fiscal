package com.vulpesfiscal.demo.security;

import com.vulpesfiscal.demo.entities.Usuario;
import com.vulpesfiscal.demo.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UsuarioService usuarioService;
    private final PasswordEncoder encoder;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        String senhaDigitada = authentication.getCredentials().toString();

        Usuario usuarioEncontrado = usuarioService.obterPorEmail(login);

        if (usuarioEncontrado == null) {
            throw getErroUsuarioNaoEncontrado();
        }

        String senhaCriptografada = usuarioEncontrado.getSenha();
        boolean senhasIguais = encoder.matches(senhaDigitada, senhaCriptografada);

        if (senhasIguais) {
            CustomUserDetails userDetails = new CustomUserDetails(usuarioEncontrado);
            // 👇 troca CustomAuthentication por UsernamePasswordAuthenticationToken
            return new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, // credentials null após autenticado
                    userDetails.getAuthorities()
            );
        }

        throw getErroUsuarioNaoEncontrado();
    }

    private UsernameNotFoundException getErroUsuarioNaoEncontrado() {
        return new UsernameNotFoundException("Usuario ou senha incorretos");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}