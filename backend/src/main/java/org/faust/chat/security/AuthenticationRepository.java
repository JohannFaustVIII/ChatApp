package org.faust.chat.security;

public interface AuthenticationRepository {

    String authorize(String user, String password);

    boolean isValid(String token);

    String refresh(String token);

    boolean logout(String token);

}
