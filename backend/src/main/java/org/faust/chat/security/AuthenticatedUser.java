package org.faust.chat.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface AuthenticatedUser extends UserDetails {

    String getToken();

    UUID getUUID();
}
