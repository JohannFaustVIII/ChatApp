package org.faust.chat;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.context.DriverContext;
import org.faust.chat.config.CassandraConfig;
import org.faust.chat.message.MessageRepository;
import org.faust.chat.message.MessageService;
import org.faust.chat.security.keycloak.KeycloakAuthenticationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.cassandra.config.SessionFactoryFactoryBean;
import org.springframework.data.cassandra.core.convert.CassandraConverter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ComponentScan(excludeFilters = {@ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE, value= CassandraConfig.class)})
@SpringBootTest(properties = {"spring.data.cassandra.keyspace-name=", "spring.data.cassandra.schema-action="})
class ChatAppApplicationTests {

	@MockBean
	private KeycloakAuthenticationRepository keycloakAuthenticationRepository;

	@MockBean
	private MessageService messageService;

	@MockBean
	private CassandraConfig cassandraConfig;

	@BeforeEach
	void setUp() {
		CqlSession session = mock(CqlSession.class);
		when(cassandraConfig.session()).thenReturn(session);
		when(session.getContext()).thenReturn(mock(DriverContext.class));
		when(cassandraConfig.sessionFactory(any(CqlSession.class), any(CassandraConverter.class))).thenReturn(mock(SessionFactoryFactoryBean.class));

	}

	@Test
	void contextLoads() {
	}

}
