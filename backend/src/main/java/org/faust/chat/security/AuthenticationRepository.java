package org.faust.chat.security;

public interface AuthenticationRepository {

    Token authorize(String user, String password);

    boolean isValid(String accessToken);

    Token refresh(String refreshToken);

    boolean logout(String accessToken);

    AuthenticatedUser getUserInfo(String accessToken);
}
