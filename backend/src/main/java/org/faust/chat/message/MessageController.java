package org.faust.chat.message;

import org.faust.chat.api.MessagesApi;
import org.faust.chat.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class MessageController implements MessagesApi {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    public MessageController(@Autowired MessageService messageService) {
        this.messageService = messageService;
    }

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
