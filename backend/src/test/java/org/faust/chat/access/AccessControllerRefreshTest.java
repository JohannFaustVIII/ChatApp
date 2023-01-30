package org.faust.chat.access;

import configuration.SecurityMocksConfiguration;
import org.faust.chat.access.model.LoginRequest;
import org.faust.chat.access.model.RefreshRequest;
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
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebFluxTest(value = AccessController.class)
@Import({SecurityMocksConfiguration.class, SecurityConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccessControllerRefreshTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccessService accessService;

    @BeforeEach
    public void setUp() {
        when(accessService.refresh(any(RefreshRequest.class)))
                .thenReturn(Mono.just(new Token("user_access_token", "user_refresh_token")));
    }

    @Test
    void returnBadRequestForNullRefreshToken() {
        String refreshToken = null;

        // when
        WebTestClient.ResponseSpec response = makePostRequest("/access/refresh", new RefreshRequest(refreshToken));

        // then
        response.expectStatus().isBadRequest();
        response.expectBody().jsonPath("errors").value(Matchers.containsInAnyOrder(
                "Refresh token can't be empty."));
    }

    @Test
    void returnOkForExistingRefreshToken() {
        String refreshToken = "example_token";

        // when
        WebTestClient.ResponseSpec response = makePostRequest("/access/refresh", new RefreshRequest(refreshToken));

        // then
        response.expectStatus().isOk();
        response.expectBody().jsonPath("token").isEqualTo("user_access_token");
        response.expectBody().jsonPath("refreshToken").isEqualTo("user_refresh_token");
    }

    private WebTestClient.ResponseSpec makePostRequest(String uri, Object body) {
        return webTestClient.post()
                .uri(uri)
                .bodyValue(body).exchange();
    }
}
