package org.faust.chat.message.command;

import org.faust.chat.message.command.model.CreateMessage;
import org.faust.chat.message.command.model.DeleteMessage;
import org.faust.chat.message.command.model.EditMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface MessageCommandService {

    Mono<Void> addMessage(CreateMessage message);

    Mono<Void> editMessage(EditMessage message);

    Mono<Void> deleteMessage(DeleteMessage message);

}
