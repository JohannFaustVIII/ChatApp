package org.faust.chat.security.keycloak;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
class KeycloakAuthenticationRepositoryException extends RuntimeException {

    public KeycloakAuthenticationRepositoryException(Throwable cause) {
        super(cause);
    }
}
