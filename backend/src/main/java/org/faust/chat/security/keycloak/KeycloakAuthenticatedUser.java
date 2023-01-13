package org.faust.chat.security.keycloak;

import lombok.RequiredArgsConstructor;
import org.faust.chat.security.AuthenticatedUser;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class KeycloakAuthenticatedUser implements AuthenticatedUser {

    private final String username;
    private final String token;
    private final List<GrantedAuthority> authorities;

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // never return password
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
