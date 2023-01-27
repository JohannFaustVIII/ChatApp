package org.faust.chat.security.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class KeycloakSession implements AutoCloseable {

    private final RestTemplate restTemplate;
    private final String url;
    private final String adminUrl;
    private final String clientId;
    private final String clientSecret;
    private final String host;

    private String authorizationToken;

    public KeycloakSession(RestTemplate restTemplate, String url, String adminUrl, String clientId, String clientSecret, String host) {
        this.restTemplate = Objects.requireNonNull(restTemplate);
        this.url = Objects.requireNonNull(url);
        this.adminUrl = Objects.requireNonNull(adminUrl);
        this.clientId = Objects.requireNonNull(clientId);
        this.clientSecret = Objects.requireNonNull(clientSecret);
        this.host = Objects.requireNonNull(host);

        openSession();
        Objects.requireNonNull(this.authorizationToken);
    }

    public boolean registerUser(String user, String password, String email) {
        String registerUrl = adminUrl + "/users";
        HttpEntity<String> request = makeRegisterRequest(user, password, email);
        ResponseEntity<String> response = restTemplate.postForEntity(registerUrl, request, String.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public void close() {
        closeSession();
    }

    private HttpEntity<String> makeRegisterRequest(String user, String password, String email) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", user);
        jsonObject.put("enabled", true);
        jsonObject.put("email", email);

        JSONObject credential = new JSONObject();
        credential.put("type", "password");
        credential.put("temporary", false);
        credential.put("value", password);

        JSONArray credentials = new JSONArray();
        credentials.add(credential);
        jsonObject.put("credentials", credentials);

        String object = jsonObject.toString();

        return new HttpEntity<>(object, jsonHeaders(object.length()));
    }

    private void openSession() {
        String authUrl = url + "/protocol/openid-connect/token";
        HttpEntity<String> request = makeClientSessionRequest();
        ResponseEntity<String> response = restTemplate.postForEntity(authUrl, request, String.class);

        this.authorizationToken = readToken(response);
    }

    private HttpEntity<String> makeClientSessionRequest() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("grant_type", "client_credentials");
        String request = KeycloakDefaults.mapRequestBodyToXWWWForm(requestBody);
        return new HttpEntity<>(request, KeycloakDefaults.defaultHeaders(request.length(), host));
    }

    private String readToken(ResponseEntity<String> response) {
        ObjectMapper bodyMapper = new ObjectMapper();
        try {
            JsonNode root = bodyMapper.readTree(response.getBody());
            return root.path("access_token").asText();
        } catch (JsonProcessingException e) {
            throw new KeycloakSessionException(e);
        }
    }

    private HttpHeaders jsonHeaders(int contentLength) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Authorization", "Bearer " + authorizationToken);
        headers.add("Content-Type", "application/json");
        headers.add("Content-Length", Integer.toString(contentLength));
        headers.add("Host", host);
        return headers;
    }

    private void closeSession() {
        String authUrl = url + "/protocol/openid-connect/revoke";
        HttpEntity<String> request = makeRevokeAuthorizationTokenRequest();
        ResponseEntity<String> response = restTemplate.postForEntity(authUrl, request, String.class);
        checkSessionCloseResponse(response);
    }

    private HttpEntity<String> makeRevokeAuthorizationTokenRequest() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("token_type_hint", "access_token");
        requestBody.put("token", authorizationToken);
        String request = KeycloakDefaults.mapRequestBodyToXWWWForm(requestBody);
        return new HttpEntity<>(request, KeycloakDefaults.defaultHeaders(request.length(), host));
    }

    private void checkSessionCloseResponse(ResponseEntity<String> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new KeycloakSessionException("Session wasn't closed properly");
        }
    }
}
