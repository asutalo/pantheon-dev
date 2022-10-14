package com.eu.atit.pantheon.server.endpoints;

import java.util.List;
import java.util.Map;

public interface Response {
    int getStatusCode();

    String getMessage();

    default Map<String, List<String>> getHeaders() {
        return Map.of();
    }
}
