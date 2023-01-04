package org.faust.chat.security;

import configuration.WebFluxTestSecurityConfiguration;
import org.faust.chat.security.model.LoginRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;


@WebFluxTest(value = AccessController.class)
@Import(WebFluxTestSecurityConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AccessControllerLoginTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void returnBadRequestForNullCredentials() {
        String login = null;
        String password = null;

        // when
        ResponseSpec response = makePostRequest("/access/login", new LoginRequest(login, password));

        // then
        response.expectStatus().isBadRequest();
        response.expectBody().jsonPath("errors").value(Matchers.containsInAnyOrder(
                "Name can't be empty.",
                "Password can't be empty."));
    }

    @Test
    void returnBadRequestForEmptyCredentials() {
        String login = "";
        String password = "";

        // when
        ResponseSpec response = makePostRequest("/access/login", new LoginRequest(login, password));

        // then
        response.expectStatus().isBadRequest();
        response.expectBody().jsonPath("errors").value(Matchers.containsInAnyOrder(
                "Name has to be at least 3 characters long.",
                "Password has to be at least 8 characters long."));
    }

    @Test
    void returnBadRequestForEmptyLogin() {
        String login = "";
        String password = "12345678";

        // when
        ResponseSpec response = makePostRequest("/access/login", new LoginRequest(login, password));

        // then
        response.expectStatus().isBadRequest();
        response.expectBody().jsonPath("errors").value(Matchers.containsInAnyOrder(
                "Name has to be at least 3 characters long."));
    }

    @Test
    void returnBadRequestForTooShortLogin() {
        String login = "A";
        String password = "12345678";

        // when
        ResponseSpec response = makePostRequest("/access/login", new LoginRequest(login, password));

        // then
        response.expectStatus().isBadRequest();
        response.expectBody().jsonPath("errors").value(Matchers.containsInAnyOrder(
                "Name has to be at least 3 characters long."));
    }

    @Test
    void returnBadRequestForEmptyPassword() {
        String login = "user";
        String password = "";

        // when
        ResponseSpec response = makePostRequest("/access/login", new LoginRequest(login, password));

        // then
        response.expectStatus().isBadRequest();
        response.expectBody().jsonPath("errors").value(Matchers.containsInAnyOrder(
                "Password has to be at least 8 characters long."));
    }

    @Test
    void returnBadRequestForTooShortPassword() {
        String login = "user";
        String password = "1234";

        // when
        ResponseSpec response = makePostRequest("/access/login", new LoginRequest(login, password));

        // then
        response.expectStatus().isBadRequest();
        response.expectBody().jsonPath("errors").value(Matchers.containsInAnyOrder(
                "Password has to be at least 8 characters long."));
    }

    @Test
    void returnOkForCorrectLengthCredentials() {
        String login = "user";
        String password = "12345678";

        // when
        ResponseSpec response = makePostRequest("/access/login", new LoginRequest(login, password));

        // then
        response.expectStatus().isOk();
    }

    private ResponseSpec makePostRequest(String uri, Object body) {
        return webTestClient.post()
                .uri(uri)
                .bodyValue(body).exchange();
    }
}