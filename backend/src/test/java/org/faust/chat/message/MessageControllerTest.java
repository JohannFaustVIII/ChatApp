package org.faust.chat.message;

import configuration.SecurityMocksConfiguration;
import org.faust.chat.security.SecurityConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebFluxTest(value = MessageController.class)
@Import({SecurityMocksConfiguration.class, SecurityConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MessageControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        when(messageService.getMessages()).thenReturn(Flux.empty());
    }

    @Test
    void returnUnauthorizedStatusWhenGettingMessagesWithoutAuthentication() {
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/messages")
                .exchange();
        response.expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(roles = "NOBODY")
    void returnForbiddenStatusWhenGettingMessagesWithoutProperRole() {
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/messages")
                .exchange();
        response.expectStatus().isForbidden();
    }

    @Test
    @WithMockUser(roles = "USER")
    void returnOkStatusWhenGettingMessagesAsUser() {
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/messages")
                .exchange();
        response.expectStatus().isOk();
    }

}