package org.faust.chat.security;

import jakarta.validation.Valid;
import org.faust.chat.security.model.LoginRequest;
import org.faust.chat.security.model.RefreshRequest;
import org.faust.chat.security.model.RegisterRequest;
import org.faust.chat.security.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/access")
public class AccessController {

    private static final Logger logger = LoggerFactory.getLogger(AccessController.class);

    @PostMapping(path = "/register", produces = "application/json")
    public Mono<Void> register(@Valid RegisterRequest registerRequest) {
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
    public Mono<Token> refresh(@Valid RefreshRequest refreshRequest) {
        return Mono.just(new Token());
    }



    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        logger.warn("Returning HTTP 400 Bad Request", e);
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
