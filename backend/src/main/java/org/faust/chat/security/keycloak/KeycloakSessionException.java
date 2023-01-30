package org.faust.chat.security.keycloak;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
class KeycloakSessionException extends RuntimeException {

    public KeycloakSessionException(String message) {
        super(message);
    }

    public KeycloakSessionException(Throwable cause) {
        super(cause);
    }
}
