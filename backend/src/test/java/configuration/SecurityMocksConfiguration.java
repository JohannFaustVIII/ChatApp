package configuration;

import org.faust.chat.security.keycloak.KeycloakAuthenticationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

@Profile("test")
@Configuration
public class SecurityMocksConfiguration {

    @Bean
    @Primary
    public KeycloakAuthenticationRepository keycloakAuthenticationRepository() {
        return mock(KeycloakAuthenticationRepository.class);
    }

}
