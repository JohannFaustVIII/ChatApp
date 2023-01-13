package org.faust.chat.security.keycloak;

import lombok.RequiredArgsConstructor;
import org.faust.chat.security.AuthenticatedUser;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KeycloakAuthFilter implements WebFilter {

    private final KeycloakAuthenticationRepository repository;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final List<String> authorizationHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authorizationHeaders == null || authorizationHeaders.isEmpty()) {
            return chain.filter(exchange);
        }
        for (String header : authorizationHeaders) {
            if (!header.startsWith("Bearer")) {
                continue;
            }
            final String jwtToken = header.substring(7);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                final boolean isTokenValid = repository.isValid(jwtToken);
                if (isTokenValid) {
                    AuthenticatedUser user = repository.getUserInfo(jwtToken);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    return chain.filter(exchange).contextWrite(c -> ReactiveSecurityContextHolder.withAuthentication(authToken));
                }
            }
        }
        return chain.filter(exchange);
    }
}
