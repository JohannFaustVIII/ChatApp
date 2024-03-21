package org.faust.chat.message.command.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record EditMessage(UUID messageId, UUID requesterId, LocalDateTime editRequestDate, LocalDateTime editServerDate, String content) {
}
