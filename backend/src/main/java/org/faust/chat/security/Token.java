package org.faust.chat.security;

import lombok.Builder;

@Builder
public record Token(String accessToken, String refreshToken) {

}
