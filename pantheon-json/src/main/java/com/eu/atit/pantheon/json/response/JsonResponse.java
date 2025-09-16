package com.eu.atit.pantheon.json.response;

import com.eu.atit.pantheon.server.response.TextResponse;

import java.util.List;
import java.util.Map;

public class JsonResponse extends TextResponse {
    static final Map<String, List<String>> baseHeaders = Map.of("Content-Type", List.of("application/json"));

    public JsonResponse(int statusCode, String message) {
        super(statusCode, message);
    }

    public JsonResponse(int statusCode, String message, Map<String, List<String>> headers) {
        super(statusCode, message, headers);
    }

    @Override
    public Map<String, List<String>> baseHeaders() {
        return baseHeaders;
    }
}
