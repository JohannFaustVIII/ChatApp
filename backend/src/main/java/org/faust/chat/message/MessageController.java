package org.faust.chat.message;

import lombok.RequiredArgsConstructor;
import org.faust.chat.access.AccessService;
import org.faust.chat.api.MessagesApi;
import org.faust.chat.message.command.MessageCommandService;
import org.faust.chat.message.query.MessageQueryService;
import org.faust.chat.message.query.model.MessageById;
import org.faust.chat.model.CreateMessage;
import org.faust.chat.model.DeleteMessage;
import org.faust.chat.model.EditMessage;
import org.faust.chat.model.Message;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

// TODO: change implementation into CQRS
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class MessageController implements MessagesApi {

    private final MessageService messageService;

    private final AccessService accessService;

    private final MessageCommandService messageCommandService;

    private final MessageQueryService messageQueryService;

    @Override
    public Flux<Message> messagesGet(ServerWebExchange exchange) {
        return messageQueryService.getAllMessages();
    }

    @Override
    public Mono<Message> messagesIdGet(UUID id, ServerWebExchange exchange) {
        return Mono.just(id)
                .map(MessageById::new)
                .flatMap(messageQueryService::getMessageById);
    }

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
    }

    @Override
    public Mono<Void> messagesPost(Mono<CreateMessage> createMessage, ServerWebExchange exchange) {
        return createMessage
                .flatMap(
                        message -> accessService.getRequesterId()
                                .map(userId -> new org.faust.chat.message.command.model.CreateMessage(
                                        userId,
                                        message.getCreationDate().toLocalDateTime(),
                                        LocalDateTime.now(),
                                        message.getContent()
                                )))
                .flatMap(messageCommandService::addMessage);
    }

    @Override
    public Mono<Void> messagesIdDelete(UUID id, Mono<DeleteMessage> deleteMessage, ServerWebExchange exchange) {
        return deleteMessage
                .flatMap(
                        message -> accessService.getRequesterId()
                                .map(userId -> new org.faust.chat.message.command.model.DeleteMessage(
                                        id,
                                        userId,
                                        message.getDeleteDate().toLocalDateTime(),
                                        LocalDateTime.now())
                                ))
                .flatMap(messageCommandService::deleteMessage);

    }
}
