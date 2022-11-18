package org.faust.chat.repository;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

public interface MessageRepository extends ReactiveCassandraRepository<Message, Integer> {

}
