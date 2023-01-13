package org.faust.chat.security;

import lombok.RequiredArgsConstructor;
import org.faust.chat.security.keycloak.KeycloakAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(useAuthorizationManager = true)
public class SecurityConfiguration {

    private final KeycloakAuthFilter keycloakAuthFilter;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf().disable() // TODO: CSRF to fix later? or maybe not required because of JWT? to check later
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/access/*").permitAll()
                        .anyExchange().authenticated()
                ).addFilterBefore(keycloakAuthFilter, SecurityWebFiltersOrder.AUTHORIZATION);

        return http.build();
    }

}
