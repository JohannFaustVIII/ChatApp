package org.faust.chat.security.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.faust.chat.security.AuthenticatedUser;
import org.faust.chat.security.AuthenticationRepository;
import org.faust.chat.security.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class KeycloakAuthenticationRepository implements AuthenticationRepository {

    private final RestTemplate restTemplate;
    private final KeycloakRolesConverter converter;

    @Value("${keycloak.url}")
    private String url;

    @Value("${keycloak.admin-url}")
    private String adminUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.host}")
    private String host;

    // TODO: registration doesn't provide required roles to use the rest of API
    @Override
    public boolean register(String user, String password, String email) {
        try (KeycloakSession session = new KeycloakSession(restTemplate, url, adminUrl, clientId, clientSecret, host)) {
            return session.registerUser(user, password, email);
        }
    }

    @Override
    public Token authorize(String user, String password) {
        String authUrl = url + "/protocol/openid-connect/token";
        HttpEntity<String> request = makeAuthorizationRequest(user, password);
        ResponseEntity<String> response = restTemplate.postForEntity(authUrl, request, String.class);
        return mapResponseToToken(response);
    }

    @Override
    public boolean isValid(String accessToken) {
        String validationUrl = url + "/protocol/openid-connect/token/introspect";
        HttpEntity<String> request = makeValidationRequest(accessToken);
        ResponseEntity<String> response = restTemplate.postForEntity(validationUrl, request, String.class);
        return getIfIsValid(response);
    }

    @Override
    public Token refresh(String refreshToken) {
        String authUrl = url + "/protocol/openid-connect/token";
        HttpEntity<String> request = makeRefreshRequest(refreshToken);
        ResponseEntity<String> response = restTemplate.postForEntity(authUrl, request, String.class);
        return mapResponseToToken(response);
    }

    @Override
    public boolean logout(String accessToken) {
        String authUrl = url + "/protocol/openid-connect/revoke";
        HttpEntity<String> request = makeLogoutRequest(accessToken);
        ResponseEntity<String> response = restTemplate.postForEntity(authUrl, request, String.class);
        return response.getStatusCode().is2xxSuccessful(); // TODO: could be better?
    }

    @Override
    public AuthenticatedUser getUserInfo(String accessToken) {
        String validationUrl = url + "/protocol/openid-connect/token/introspect";
        HttpEntity<String> request = makeValidationRequest(accessToken);
        ResponseEntity<String> response = restTemplate.postForEntity(validationUrl, request, String.class);
        return mapResponseToUser(accessToken, response);
    }

    private HttpEntity<String> makeAuthorizationRequest(String user, String password) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("grant_type", "password");
        requestBody.put("username", user);
        requestBody.put("password", password);
        String request = KeycloakDefaults.mapRequestBodyToXWWWForm(requestBody);
        return new HttpEntity<>(request, KeycloakDefaults.defaultHeaders(request.length(), host));
    }

    private HttpEntity<String> makeValidationRequest(String accessToken) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("token", accessToken);
        String request = KeycloakDefaults.mapRequestBodyToXWWWForm(requestBody);
        return new HttpEntity<>(request, KeycloakDefaults.defaultHeaders(request.length(), host));
    }

    private HttpEntity<String> makeRefreshRequest(String refreshToken) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("grant_type", "refresh_token");
        requestBody.put("refresh_token", refreshToken);
        String request = KeycloakDefaults.mapRequestBodyToXWWWForm(requestBody);
        return new HttpEntity<>(request, KeycloakDefaults.defaultHeaders(request.length(), host));
    }

    private HttpEntity<String> makeLogoutRequest(String accessToken) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("token_type_hint", "access_token");
        requestBody.put("token", accessToken);
        String request = KeycloakDefaults.mapRequestBodyToXWWWForm(requestBody);
        return new HttpEntity<>(request, KeycloakDefaults.defaultHeaders(request.length(), host));
    }

    private boolean getIfIsValid(ResponseEntity<String> response) {
        try {
            ObjectMapper bodyMapper = new ObjectMapper();
            JsonNode root = bodyMapper.readTree(response.getBody());
            return root.path("active").asBoolean();
        } catch (JsonProcessingException e) {
            throw new KeycloakAuthenticationRepositoryException(e);
        }
    }

    private Token mapResponseToToken(ResponseEntity<String> response) {
        try {
            ObjectMapper bodyMapper = new ObjectMapper();
            JsonNode root = bodyMapper.readTree(response.getBody());
            String accessToken = root.path("access_token").asText();
            String refreshToken = root.path("refresh_token").asText();
            return new Token(accessToken, refreshToken);
        } catch (JsonProcessingException e) {
            throw new KeycloakAuthenticationRepositoryException(e);
        }
    }

    private AuthenticatedUser mapResponseToUser(String accessToken, ResponseEntity<String> response) {
        try {
            ObjectMapper bodyMapper = new ObjectMapper();
            JsonNode root = bodyMapper.readTree(response.getBody());
            String user = root.path("preferred_username").asText();
            UUID uuid = UUID.fromString(root.path("sub").asText());
            List<GrantedAuthority> authorities = converter.convert(root.get("realm_access").get("roles"));
            return new KeycloakAuthenticatedUser(user, accessToken, authorities, uuid);
        } catch (JsonProcessingException e) {
            throw new KeycloakAuthenticationRepositoryException(e);
        }
    }
}
