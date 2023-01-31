package org.faust.chat.access;

import org.faust.chat.security.SecurityConfiguration;
import org.faust.chat.security.keycloak.KeycloakAuthenticatedUser;
import org.faust.chat.security.keycloak.KeycloakAuthenticationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebFluxTest(value = {AccessController.class, AccessService.class})
@Import(SecurityConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccessControllerLogoutTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private KeycloakAuthenticationRepository keycloakAuthenticationRepository;

    @BeforeEach
    void setUp() {
        when(keycloakAuthenticationRepository.isValid("testtoken"))
                .thenReturn(true);
        when(keycloakAuthenticationRepository.getUserInfo("testtoken"))
                .thenReturn(new KeycloakAuthenticatedUser("testuser", "testtoken", Collections.emptyList(), UUID.randomUUID()));
        when(keycloakAuthenticationRepository.logout("testtoken"))
                .thenReturn(true);
    }

    @Test
    void returnUnauthorizedForNoAuthenticationToken() {
        //when
        ResponseSpec response = makePostRequest("/access/logout");
        //then
        response.expectStatus().isUnauthorized();
    }

    @Test
    void returnOkForExistingAuthenticationToken() {
        //when
        ResponseSpec response = makePostRequest("/access/logout", "testtoken");
        //then
        response.expectStatus().isOk();
        response.expectBody().jsonPath("true");
    }

    private WebTestClient.ResponseSpec makePostRequest(String uri) {
        return webTestClient.post()
                .uri(uri)
                .exchange();
    }

    private WebTestClient.ResponseSpec makePostRequest(String uri, String token) {
        return webTestClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .exchange();
    }
}
