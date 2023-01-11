package org.faust.chat.security.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.faust.chat.security.AuthenticationRepository;
import org.faust.chat.security.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
@RequiredArgsConstructor
public class KeycloakAuthenticationRepository implements AuthenticationRepository {
    // TODO: headers may be a problem, to check

    private final RestTemplate restTemplate;

    @Value("${keycloak.url}")
    private String url;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    // client registration - /clients-registrations/openid-connect ??? will it work for users
    // POST EVERYWHERE
    @Override
    public Token authorize(String user, String password) {
        String authUrl = url + "/protocol/openid/token";
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
        String authUrl = url + "/protocol/openid/token";
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

    private HttpEntity<String> makeAuthorizationRequest(String user, String password) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", clientId);
        jsonObject.put("client_secret", clientSecret);
        jsonObject.put("grant_type", "password");
        jsonObject.put("username", user);
        jsonObject.put("password", password);
        HttpHeaders headers = new HttpHeaders();
        return new HttpEntity<>(jsonObject.toString(), headers);
    }

    private HttpEntity<String> makeValidationRequest(String accessToken) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", clientId);
        jsonObject.put("client_secret", clientSecret);
        jsonObject.put("token", accessToken);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
        return request;
    }

    private HttpEntity<String> makeRefreshRequest(String refreshToken) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", clientId);
        jsonObject.put("client_secret", clientSecret);
        jsonObject.put("grant_type", "refresh_token");
        jsonObject.put("refresh_token", refreshToken);
        HttpHeaders headers = new HttpHeaders();
        return new HttpEntity<>(jsonObject.toString(), headers);
    }

    private HttpEntity<String> makeLogoutRequest(String accessToken) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", clientId);
        jsonObject.put("client_secret", clientSecret);
        jsonObject.put("token_type_hint", "access_token");
        jsonObject.put("token", accessToken);
        HttpHeaders headers = new HttpHeaders();
        return new HttpEntity<>(jsonObject.toString(), headers);
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
}
