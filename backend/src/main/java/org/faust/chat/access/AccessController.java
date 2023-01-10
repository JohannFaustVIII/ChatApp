package org.faust.chat.access;

import jakarta.validation.Valid;
import org.faust.chat.access.model.LoginRequest;
import org.faust.chat.access.model.RefreshRequest;
import org.faust.chat.access.model.RegisterRequest;
import org.faust.chat.access.model.Token;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/access")
public class AccessController {

    @PostMapping(path = "/register", produces = "application/json")
    public Mono<Void> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return Mono.empty();
    }

    @PostMapping(path = "/login", produces = "application/json")
    public Mono<Token> login(@RequestBody @Valid LoginRequest loginRequest) {
        return Mono.just(new Token());
    }

    @PostMapping(path = "/logout", produces = "application/json")
    public Mono<Void> logout() {
        return Mono.empty();
    }

    @PostMapping(path = "/refresh", produces = "application/json")
    public Mono<Token> refresh(@RequestBody @Valid RefreshRequest refreshRequest) {
        return Mono.just(new Token());
    }

    /*
    TODO:
        1. Controller to login and register, maybe logout too?
        2. Create models for login and register
        3. Create annotations to verify e-mail and matching passwords (to use with Valid in controller)
        4. Create interface for authenticating service
        5. Create implementation of the service using Keycloak (with defaults etc.)
        6. Update SecurityConfiguration (remove usage of login redirect to keycloak and add access to endpoints in the controller)
     */
}
