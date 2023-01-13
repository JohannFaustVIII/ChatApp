package org.faust.chat.security.keycloak;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KeycloakRolesConverter {

    private static final String[] ROLES_TO_IGNORE = new String[] {"offline_access", "default-roles", "uma_authorization"};

    public List<GrantedAuthority> convert(JsonNode roles) {
        List<String> authorities = mapJsonNodeToList(roles);

        return readCustomRoles(authorities);
    }

    private List<String> mapJsonNodeToList(JsonNode roles) {
        List<String> authorities = new ArrayList<>();
        for (JsonNode role : roles) {
            authorities.add(role.asText());
        }
        return authorities;
    }

    private List<GrantedAuthority> readCustomRoles(List<String> authorities) {
        return authorities.stream()
                .map(Object::toString)
                .filter(role -> Arrays.stream(ROLES_TO_IGNORE).noneMatch(role::startsWith))
                .map(role -> "ROLE_" + role.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
