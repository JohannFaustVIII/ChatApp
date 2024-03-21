package org.faust.chat.message.command.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeleteMessage(UUID messageId, UUID requesterId, LocalDateTime deleteRequestDate, LocalDateTime deleteServerDate) {
}
