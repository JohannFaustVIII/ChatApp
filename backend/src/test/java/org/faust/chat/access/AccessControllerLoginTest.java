package org.faust.chat.access;

import configuration.SecurityMocksConfiguration;
import org.faust.chat.access.model.LoginRequest;
import org.faust.chat.access.model.Token;
import org.faust.chat.security.SecurityConfiguration;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebFluxTest(value = AccessController.class)
@Import({SecurityMocksConfiguration.class, SecurityConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AccessControllerLoginTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccessService accessService;

    @BeforeEach
    public void setUp() {
        when(accessService.login(any(LoginRequest.class)))
                .thenReturn(Mono.just(new Token("user_access_token")));
    }

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
        response.expectBody().jsonPath("token").isEqualTo("user_access_token");
    }

    private ResponseSpec makePostRequest(String uri, Object body) {
        return webTestClient.post()
                .uri(uri)
                .bodyValue(body).exchange();
    }
}