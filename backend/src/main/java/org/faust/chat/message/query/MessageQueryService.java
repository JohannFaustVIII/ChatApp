package org.faust.chat.message.query;

import org.faust.chat.message.query.model.MessageById;
import org.faust.chat.model.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface MessageQueryService {

    Flux<Message> getAllMessages();

    Mono<Message> getMessageById(MessageById query);

}
