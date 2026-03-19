package com.vulpesfiscal.demo.security;

import com.vulpesfiscal.demo.entities.Usuario;
import com.vulpesfiscal.demo.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioService service;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = service.obterPorEmail(email);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado!");
        }
        System.out.println(usuario.getRoles());
        return new CustomUserDetails(usuario);

    }
}