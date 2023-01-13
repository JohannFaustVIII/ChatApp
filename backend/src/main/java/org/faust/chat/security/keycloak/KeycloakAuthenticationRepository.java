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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class KeycloakAuthenticationRepository implements AuthenticationRepository {

    private final RestTemplate restTemplate;
    private final KeycloakRolesConverter converter;

    @Value("${keycloak.url}")
    private String url;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.host}")
    private String host;

    // client registration - /clients-registrations/openid-connect ??? will it work for users
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
        String request = mapRequestBodyToXWWWForm(requestBody);
        return new HttpEntity<>(request, defaultHeaders(request.length()));
    }

    private HttpEntity<String> makeValidationRequest(String accessToken) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("token", accessToken);
        String request = mapRequestBodyToXWWWForm(requestBody);
        return new HttpEntity<>(request, defaultHeaders(request.length()));
    }

    private HttpEntity<String> makeRefreshRequest(String refreshToken) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("grant_type", "refresh_token");
        requestBody.put("refresh_token", refreshToken);
        String request = mapRequestBodyToXWWWForm(requestBody);
        return new HttpEntity<>(request, defaultHeaders(request.length()));
    }

    private HttpEntity<String> makeLogoutRequest(String accessToken) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("token_type_hint", "access_token");
        requestBody.put("token", accessToken);
        String request = mapRequestBodyToXWWWForm(requestBody);
        return new HttpEntity<>(request, defaultHeaders(request.length()));
    }

    private boolean getIfIsValid(ResponseEntity<String> response) {
        ObjectMapper bodyMapper = new ObjectMapper();
        try {
            JsonNode root = bodyMapper.readTree(response.getBody());
            return root.path("active").asBoolean();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e); // TODO: implement something more descriptive
        }
    }

    private Token mapResponseToToken(ResponseEntity<String> response) {
        ObjectMapper bodyMapper = new ObjectMapper();
        try {
            JsonNode root = bodyMapper.readTree(response.getBody());
            String accessToken = root.path("access_token").asText();
            String refreshToken = root.path("refresh_token").asText();
            return new Token(accessToken, refreshToken);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e); // TODO: implement something more descriptive
        }
    }

    private AuthenticatedUser mapResponseToUser(String accessToken, ResponseEntity<String> response) {
        ObjectMapper bodyMapper = new ObjectMapper();
        try {
            JsonNode root = bodyMapper.readTree(response.getBody());
            String user = root.path("preferred_username").asText();
            List<GrantedAuthority> authorities = converter.convert(root.get("realm_access").get("roles"));
            return new KeycloakAuthenticatedUser(user, accessToken, authorities);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e); // TODO: implement something more descriptive
        }
    }

    private String mapRequestBodyToXWWWForm(Map<String, String> requestBody) {
        return requestBody.entrySet()
                .stream().map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((s1, s2) -> s1 + "&" + s2)
                .orElse("");
    }

    private HttpHeaders defaultHeaders(int contentLength) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("Content-Length", Integer.toString(contentLength));
        headers.add("Host", host);
        return headers;
    }
}
