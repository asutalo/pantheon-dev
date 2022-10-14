package com.eu.atit.pantheon.server.response.exception;

public class BadRequestException extends IocServerException {
    public static final Integer BAD_REQUEST_ERROR_STATUS = 400;
    public static final String BAD_REQUEST_ERROR_MESSAGE = "Bad Request";

    public BadRequestException() {
        super(BAD_REQUEST_ERROR_STATUS, BAD_REQUEST_ERROR_MESSAGE);
    }
}
