package org.faust.chat;

import org.faust.chat.config.CassandraConfig;
import org.faust.chat.message.MessageRepository;
import org.faust.chat.security.keycloak.KeycloakAuthenticationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.cassandra.core.convert.CassandraConverter;

@SpringBootTest(properties = {"spring.data.cassandra.keyspace-name=", "spring.data.cassandra.schema-action="})
@EnableAutoConfiguration(exclude = {CassandraDataAutoConfiguration.class, CassandraReactiveDataAutoConfiguration.class})
class ChatAppApplicationTests {
	// TODO: find a better way, it doesn't test it everything loads as supposed

	@MockBean
	private KeycloakAuthenticationRepository keycloakAuthenticationRepository;

	@MockBean
	private MessageRepository messageRepository;

	@MockBean
	private CassandraConfig cassandraConfig;

	@MockBean
	private CassandraConverter cassandraConverter;

	@Test
	void contextLoads() {
	}

}
