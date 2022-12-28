package org.faust.chat.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class GrantedAuthoritiesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String[] ROLES_TO_IGNORE = new String[] {"offline_access", "default-roles", "uma_authorization"};

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<?> authorities = (Collection<?>) jwt.getClaimAsMap("realm_access").get("roles");
        return authorities.stream()
                .map(Object::toString)
                .filter(role -> Arrays.stream(ROLES_TO_IGNORE).noneMatch(role::startsWith))
                .map(role -> "ROLE_" + role.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
