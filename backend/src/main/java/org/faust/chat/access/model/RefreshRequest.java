package org.faust.chat.access.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {

    @Getter
    @NotNull(message = "Refresh token can't be empty.")
    private String refreshToken;
}
