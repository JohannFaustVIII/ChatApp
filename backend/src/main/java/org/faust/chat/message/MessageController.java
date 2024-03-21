package org.faust.chat.message;

import lombok.RequiredArgsConstructor;
import org.faust.chat.access.AccessService;
import org.faust.chat.api.MessagesApi;
import org.faust.chat.message.command.MessageCommandService;
import org.faust.chat.model.CreateMessage;
import org.faust.chat.model.EditMessage;
import org.faust.chat.model.Message;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

// TODO: change implementation into CQRS
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class MessageController implements MessagesApi {

    private final MessageService messageService;

    private final AccessService accessService;

    private final MessageCommandService messageCommandService;

    // this is fine? query would be required for a single message, maybe get history?
    @Override
    public Flux<Message> messagesGet(ServerWebExchange exchange) {
        return messageService.getMessages();
    }

    // what can be changed? only message's text so only it can be sent, and maybe edit time?
    @Override
    public Mono<Void> messagesIdPatch(UUID id, Mono<EditMessage> editMessage, ServerWebExchange exchange) {
        return editMessage
                .flatMap(
                        message -> accessService.getRequesterId()
                                .map(userId -> new org.faust.chat.message.command.model.EditMessage(
                                        id,
                                        userId,
                                        message.getEditDate().toLocalDateTime(),
                                        LocalDateTime.now(),
                                        message.getContent())
                                ))
                .flatMap(messageCommandService::editMessage);
//        return messageService.updateMessage(id, message);
    }

    // only date and text is required for post, the rest is set by server
    @Override
    public Mono<Void> messagesPost(Mono<CreateMessage> createMessage, ServerWebExchange exchange) {
        return createMessage
                .flatMap(
                        message -> accessService.getRequesterId()
                                .map(userId -> new org.faust.chat.message.command.model.CreateMessage(
                                        UUID.randomUUID(), // TODO: CHANGE IT
                                        userId,
                                        message.getCreationDate().toLocalDateTime(),
                                        message.getContent()
                        )))
                .flatMap(messageCommandService::addMessage);
//        return messageService.addMessage(accessService.addSenderIdToMessage(message));
    }

    // this is correct, wrap into command? TO THINK
    @Override
    public Mono<Void> messagesIdDelete(UUID id, ServerWebExchange exchange) {
        return Mono.just(id)
                .flatMap(messageId -> accessService.getRequesterId()
                        .map(userId -> new org.faust.chat.message.command.model.DeleteMessage(
                                messageId,
                                userId,
                                LocalDateTime.now(), // TODO: change to request
                                LocalDateTime.now()
                        )))
                .flatMap(messageCommandService::deleteMessage);
//        return messageService.deleteMessage(id);
    }
}
