package org.faust.chat.access;

import configuration.SecurityMocksConfiguration;
import org.faust.chat.access.model.RegisterRequest;
import org.faust.chat.security.SecurityConfiguration;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

@ActiveProfiles("test")
@WebFluxTest(value = AccessController.class)
@Import({SecurityMocksConfiguration.class, SecurityConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccessControllerRegisterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccessService accessService;

    @Test
    void returnBadRequestForNullCredentials() {
        String login = null;
        String password = null;
        String matchingPassword = null;
        String email = null;

        // when
        ResponseSpec response = makePostRequest("/access/register", new RegisterRequest(login, password, matchingPassword, email));

        // then
        response.expectStatus().isBadRequest();
        response.expectBody().jsonPath("errors").value(Matchers.containsInAnyOrder(
                "Name can't be empty.",
                "Password can't be empty.",
                "E-mail can't be empty."));
    }

    @Test
    void returnBadRequestForEmptyCredentials() {
        String login = "";
        String password = "";
        String matchingPassword = "";
        String email = "";

        // when
        ResponseSpec response = makePostRequest("/access/register", new RegisterRequest(login, password, matchingPassword, email));

        // then
        response.expectStatus().isBadRequest();
        response.expectBody().jsonPath("errors").value(Matchers.containsInAnyOrder(
                "Name has to be at least 3 characters long.",
                "Password has to be at least 8 characters long.",
                "E-mail has to be in correct format."));
    }

    @Test
    void returnBadRequestForTooShortLogin() {
        String login = "A";
        String password = "12345678";
        String matchingPassword = "12345678";
        String email = "test@example.com";

        // when
        ResponseSpec response = makePostRequest("/access/register", new RegisterRequest(login, password, matchingPassword, email));

        // then
        response.expectStatus().isBadRequest();
        response.expectBody().jsonPath("errors").value(Matchers.containsInAnyOrder(
                "Name has to be at least 3 characters long."));
    }

    @Test
    void returnBadRequestForTooShortPassword() {
        String login = "user";
        String password = "123456";
        String matchingPassword = "123456";
        String email = "test@example.com";

        // when
        ResponseSpec response = makePostRequest("/access/register", new RegisterRequest(login, password, matchingPassword, email));

        // then
        response.expectStatus().isBadRequest();
        response.expectBody().jsonPath("errors").value(Matchers.containsInAnyOrder(
                "Password has to be at least 8 characters long."));
    }

    @Test
    void returnBadRequestForNotMatchingPasswords() {
        String login = "user";
        String password = "12345678";
        String matchingPassword = "totally_different";
        String email = "test@example.com";

        // when
        ResponseSpec response = makePostRequest("/access/register", new RegisterRequest(login, password, matchingPassword, email));

        // then
        response.expectStatus().isBadRequest();
        response.expectBody().jsonPath("errors").value(Matchers.containsInAnyOrder(
                "Passwords have to match."));
    }

    @Test
    void returnBadRequestForWrongFormattedEmail() {
        String login = "user";
        String password = "12345678";
        String matchingPassword = "12345678";
        String email = "test@example";

        // when
        ResponseSpec response = makePostRequest("/access/register", new RegisterRequest(login, password, matchingPassword, email));

        // then
        response.expectStatus().isBadRequest();
        response.expectBody().jsonPath("errors").value(Matchers.containsInAnyOrder(
                "E-mail has to be in correct format."));
    }

    @Test
    void returnOkForCorrectCredentials() {
        String login = "user";
        String password = "12345678";
        String matchingPassword = "12345678";
        String email = "test@example.com";

        // when
        ResponseSpec response = makePostRequest("/access/register", new RegisterRequest(login, password, matchingPassword, email));

        // then
        response.expectStatus().isOk();
    }

    private ResponseSpec makePostRequest(String uri, Object body) {
        return webTestClient.post()
                .uri(uri)
                .bodyValue(body).exchange();
    }
}
