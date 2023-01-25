package org.faust.chat.access;

import lombok.RequiredArgsConstructor;
import org.faust.chat.access.model.LoginRequest;
import org.faust.chat.access.model.RefreshRequest;
import org.faust.chat.access.model.Token;
import org.faust.chat.security.AuthenticationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccessService {

    private final AuthenticationRepository authenticationRepository;

    public Mono<Token> login(LoginRequest loginRequest) {
        return Mono.just(loginRequest)
                .map(login -> authenticationRepository.authorize(login.getName(), login.getPassword()))
                .map(token -> new Token(token.accessToken(), token.refreshToken()));
    }

    public Mono<Boolean> logout(String token) {
        return Mono.just(authenticationRepository.logout(token));
    }

    public Mono<Token> refresh(RefreshRequest refreshRequest) {
        return Mono.just(refreshRequest)
                .map(request -> authenticationRepository.refresh(request.getRefreshToken()))
                .map(token -> new Token(token.accessToken(), token.refreshToken()));
    }
}
