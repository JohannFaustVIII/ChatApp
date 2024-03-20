package org.faust.chat.message;

import lombok.RequiredArgsConstructor;
import org.faust.chat.access.AccessService;
import org.faust.chat.api.MessagesApi;
import org.faust.chat.model.Message;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

// TODO: change implementation into CQRS
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class MessageController implements MessagesApi {

    private final MessageService messageService;

    private final AccessService accessService;

    // this is fine? query would be required for a single message, maybe get history?
    @Override
    public Flux<Message> messagesGet(ServerWebExchange exchange) {
        return messageService.getMessages();
    }

    // what can be changed? only message's text so only it can be sent, and maybe edit time?
    @Override
    public Mono<Message> messagesIdPatch(UUID id, Mono<Message> message, ServerWebExchange exchange) {
        return messageService.updateMessage(id, message);
    }

    // only date and text is required for post, the rest is set by server
    @Override
    public Mono<Message> messagesPost(Mono<Message> message, ServerWebExchange exchange) {
        return messageService.addMessage(accessService.addSenderIdToMessage(message));
    }

    // this is correct, wrap into command? TO THINK
    @Override
    public Mono<Void> messagesIdDelete(UUID id, ServerWebExchange exchange) {
        return messageService.deleteMessage(id);
    }
}
