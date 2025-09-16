package com.eu.atit.pantheon.server.response;

import com.eu.atit.pantheon.server.endpoints.BaseResponse;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class TextResponse extends BaseResponse {
    private final String message;
    private static final Map<String, List<String>> baseHeaders =
            Map.of("Content-Type", List.of("text/plain; charset=UTF-8"));

    public TextResponse(int statusCode, String message) {
        super(statusCode, Map.of(), message.getBytes(StandardCharsets.UTF_8));
        this.message = message;
    }

    public TextResponse(int statusCode, String message, Map<String, List<String>> headers) {
        super(statusCode, headers, message.getBytes(StandardCharsets.UTF_8));
        this.message = message;
    }

    @Override
    public Map<String, List<String>> baseHeaders() {
        return baseHeaders;
    }

    public String getMessage() {
        return message;
    }
}
