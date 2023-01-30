package org.faust.chat.message;

import lombok.RequiredArgsConstructor;
import org.faust.chat.api.MessagesApi;
import org.faust.chat.model.Message;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class MessageController implements MessagesApi {

    private final MessageService messageService;

    @Override
    public Flux<Message> messagesGet(ServerWebExchange exchange) {
        return messageService.getMessages();
    }

    @Override
    public Mono<Message> messagesIdPatch(UUID id, Mono<Message> message, ServerWebExchange exchange) {
        return messageService.updateMessage(id, message);
    }

    @Override
    public Mono<Message> messagesPost(Mono<Message> message, ServerWebExchange exchange) {
        return messageService.addMessage(message);
    }

    @Override
    public Mono<Void> messagesIdDelete(UUID id, ServerWebExchange exchange) {
        return messageService.deleteMessage(id);
    }
}
