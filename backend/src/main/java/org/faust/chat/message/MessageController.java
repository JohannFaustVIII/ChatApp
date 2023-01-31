package org.faust.chat.message;

import lombok.RequiredArgsConstructor;
import org.faust.chat.api.MessagesApi;
import org.faust.chat.model.Message;
import org.faust.chat.security.AuthenticatedUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
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
        final Mono<Message> inputMono = message;
        message = ReactiveSecurityContextHolder.getContext()
                .map(context -> (AuthenticatedUser)context.getAuthentication().getPrincipal())
                .map(AuthenticatedUser::getUUID)
                .flatMap(uuid -> inputMono.map(m -> m.senderId(uuid))); // TODO: move it... somewhere? not a responsibility of message service nor controller, maybe to access service? or new service
        return messageService.addMessage(message);
    }

    @Override
    public Mono<Void> messagesIdDelete(UUID id, ServerWebExchange exchange) {
        return messageService.deleteMessage(id);
    }
}
