package com.eu.at_it.pantheon.server.exceptions;

import com.eu.at_it.pantheon.server.endpoints.Response;

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
}
