package org.faust.chat.message;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.*;
import org.faust.chat.model.Message;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Table
@Getter
@AllArgsConstructor
@Builder
public class MessageDBEntity {

    @PrimaryKeyColumn(
            name = "id",
            type = PrimaryKeyType.PARTITIONED
    )
    @Id
    private UUID id;
    @Column
    private UUID senderId;
    @Setter
    @Column
    private String text;
    @Column
    private LocalDateTime time;

    public Message toModel() {
        Message result = new Message();
        result.setId(id);
        result.setSenderId(senderId);
        result.setText(text);
        result.setDate(time.atOffset(ZoneOffset.UTC));
        return result;
    }

    public static MessageDBEntity toDBEntity(Message message) {
        return MessageDBEntity.builder()
                .senderId(message.getSenderId())
                .time(message.getDate().toLocalDateTime())
                .text(message.getText())
                .id(message.getId() == null ? Uuids.timeBased() : message.getId())
                .build();
    }
}
