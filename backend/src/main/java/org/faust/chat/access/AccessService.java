package org.faust.chat.access;

import lombok.RequiredArgsConstructor;
import org.faust.chat.access.model.LoginRequest;
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
                .map(token -> new Token(token.accessToken()));
    }
}
