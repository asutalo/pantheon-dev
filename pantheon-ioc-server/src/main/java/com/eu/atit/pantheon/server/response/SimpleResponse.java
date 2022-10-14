package com.eu.atit.pantheon.server.response;

import com.eu.atit.pantheon.server.endpoints.Response;

/**
 * A utility class, most basic version of a Response
 */
public class SimpleResponse implements Response {
    private final int statusCode;
    private final String message;

    public SimpleResponse(int statusCode, String message) {
        this.statusCode = statusCode;

        this.message = message;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
