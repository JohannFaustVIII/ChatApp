package org.faust.chat.security.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "Message can't be empty.")
    @Min(value = 8, message = "Password has to be at least 8 characters long.") // TODO: this doesn't work, and add proper return of messages
    private String password;

}
