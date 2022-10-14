package com.eu.atit.pantheon.server.response;

import java.util.List;
import java.util.Map;

/**
 * A utility class, adds the ability to specify response Headers in addition to status code and message
 */
public class SimpleResponseWithHeaders extends SimpleResponse {
    private final Map<String, List<String>> headers;

    public SimpleResponseWithHeaders(int statusCode, String message, Map<String, List<String>> headers) {
        super(statusCode, message);
        this.headers = headers;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}
