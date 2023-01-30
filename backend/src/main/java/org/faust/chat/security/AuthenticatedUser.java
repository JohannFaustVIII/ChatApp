package org.faust.chat.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticatedUser extends UserDetails {

    String getToken();
}
