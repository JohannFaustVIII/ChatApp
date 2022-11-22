package org.faust.chat.message;

import org.faust.chat.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(@Autowired MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Flux<Message> getMessages() {
        return messageRepository
                .findAll()
                .flatMapSequential(message -> Flux.just(message.toModel()));
    }

    public Mono<Message> addMessage(Mono<Message> message) {
        return message
                .flatMap(m -> Mono.just(MessageDBEntity.toDBEntity(m)))
                .flatMap(messageRepository::save)
                .flatMap(m -> Mono.just(m.toModel()));
    }

    public Mono<Message> updateMessage(UUID id, Mono<Message> message) {
        return messageRepository
                .findById(id)
                .flatMap(entity -> message.map(m -> updateText(entity, m)))
                .flatMap(messageRepository::save)
                .flatMap(m -> Mono.just(m.toModel()));
    }

    private MessageDBEntity updateText(MessageDBEntity entity, Message message) {
        entity.setText(message.getText());
        return entity;
    }
}
