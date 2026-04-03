    package com.vulpesfiscal.demo.security;

    import lombok.Getter;
    import lombok.RequiredArgsConstructor;
    import org.jspecify.annotations.Nullable;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.GrantedAuthority;

    import java.util.Collection;

    @RequiredArgsConstructor
    @Getter
    public class CustomAuthentication implements Authentication {

        private final CustomUserDetails principal;
        private boolean authenticated = true;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return principal.getAuthorities();
        }

        @Override
        public @Nullable Object getCredentials() {
            return principal.getPassword();
        }

        @Override
        public @Nullable Object getDetails() {
            return principal;
        }

        @Override
        public @Nullable Object getPrincipal() {
            return principal;
        }

        @Override
        public boolean isAuthenticated() {
            return authenticated;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            this.authenticated = isAuthenticated;
        }

        @Override
        public String getName() {
            return principal.getUsername();
        }
    }