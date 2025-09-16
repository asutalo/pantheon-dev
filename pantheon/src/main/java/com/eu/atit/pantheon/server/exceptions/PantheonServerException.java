package com.eu.atit.pantheon.server.exceptions;

import com.eu.atit.pantheon.server.endpoints.BaseResponse;
import com.eu.atit.pantheon.server.endpoints.Response;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Standard wrapper for Server exceptions to conform to a Response object
 * Can either throw a new instance of this class or use it to create specific exceptions
 * such as {@link UnauthorizedException}
 */
public class PantheonServerException extends RuntimeException implements Response {
    final int statusCode;

    public PantheonServerException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public byte[] getBody() {
        return super.getMessage().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return Map.of("Content-Type", List.of("text/plain; charset=UTF-8"));
    }
}
