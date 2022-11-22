package org.faust.chat.message;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends ReactiveCassandraRepository<MessageDBEntity, UUID> {

}
