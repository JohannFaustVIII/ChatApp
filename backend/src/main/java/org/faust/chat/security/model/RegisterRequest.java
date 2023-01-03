package org.faust.chat.security.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.faust.util.validation.password.MatchingPassword;
import org.faust.util.validation.password.Password;
import org.springframework.validation.annotation.Validated;

@Validated
@MatchingPassword(message = "Passwords have to match.")
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @Getter
    @NotNull(message = "Name can't be empty.")
    @Size(min = 3, message = "Name has to be at least 3 characters long.")
    private String name;

    @Getter
    @NotNull(message = "Password can't be empty.")
    @Size(min = 8, message = "Password has to be at least 8 characters long.")
    @Password
    private String password;

    @Getter
    @Password
    private String matchingPassword;

    @Getter
    @NotNull(message = "E-mail can't be empty.")
    @Email(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", message = "E-mail has to be in correct format.")
    private String email;
}
