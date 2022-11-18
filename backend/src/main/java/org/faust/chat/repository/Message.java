package org.faust.chat.repository;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Table
public class Message {

    @PrimaryKey
    private int id;
    private int sender_id;
    private String text;
    private LocalDateTime time;
}
