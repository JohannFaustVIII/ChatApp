package org.faust.chat.security.keycloak;

import org.faust.chat.security.AuthenticationRepository;
import org.springframework.stereotype.Repository;

@Repository
public class KeycloakAuthenticationRepository implements AuthenticationRepository {

    // client registration - /clients-registrations/openid-connect ??? will it work for users

    @Override
    public String authorize(String user, String password) {
        /*
            /protocol/openid-connect/token
         */
        return null;
    }

    @Override
    public boolean isValid(String token) {
        /*
            /protocol/openid-connect/token/introspect should be good to check the token
            requires confidential access type!!!
         */
        return false;
    }

    @Override
    public String refresh(String token) {
        /*
          /protocol/openid-connect/token to use
         */
        return null;
    }

    @Override
    public boolean logout(String token) {
        /*
            /protocol/openid-connect/revoke for token
            or /protocol/openid-connect/logout
         */
        return false;
    }
}
