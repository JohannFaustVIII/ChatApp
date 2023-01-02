package org.faust.chat.security.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Getter
    @NotEmpty(message = "Name can't be empty.")
    private String name;

    @Getter
    @NotNull(message = "Password can't be empty.")
    @Size(min = 8, message = "Password has to be at least 8 characters long.")
    private String password;

}
