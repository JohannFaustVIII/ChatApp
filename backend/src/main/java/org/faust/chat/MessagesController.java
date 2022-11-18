package org.faust.chat;

import org.faust.chat.api.MessagesApi;
import org.faust.chat.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class MessagesController implements MessagesApi {

    private static final Logger logger = LoggerFactory.getLogger(MessagesController.class);

    @Override
    public Flux<Message> messagesGet(ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<Void> messagesIdPut(Integer id, Mono<Message> message, ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<Void> messagesPost(Mono<Message> message, ServerWebExchange exchange) {
        return message
                .doOnNext(this::handleMessage)
                .flatMap(m -> Mono.empty());
    }

    private void handleMessage(Message message) {
        logger.info(message.getId() + "  " + message.getText());
    }
}
