package org.faust.chat.security.keycloak;

import org.springframework.http.HttpHeaders;

import java.util.Map;

// should be injectable?
class KeycloakDefaults {

    static String mapRequestBodyToXWWWForm(Map<String, String> requestBody) {
        return requestBody.entrySet()
                .stream().map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((s1, s2) -> s1 + "&" + s2)
                .orElse("");
    }

    static HttpHeaders defaultHeaders(int contentLength, String host) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("Content-Length", Integer.toString(contentLength));
        headers.add("Host", host);
        return headers;
    }
}
