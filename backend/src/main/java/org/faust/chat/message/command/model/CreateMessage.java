package org.faust.chat.message.command.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateMessage(UUID messageId, UUID creatorId, LocalDateTime creationDate, String content) {

}
