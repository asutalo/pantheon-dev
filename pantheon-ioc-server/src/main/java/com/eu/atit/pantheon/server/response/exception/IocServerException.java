package com.eu.atit.pantheon.server.response.exception;

import com.eu.atit.pantheon.server.endpoints.Response;

/**
 * Standard wrapper for IoC Server exceptions to conform to a Response object
 * Can either throw a new instance of this class or use it to create specific exceptions
 * such as {@link BadRequestException}
 */
public class IocServerException extends RuntimeException implements Response {
    final int statusCode;

    public IocServerException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
