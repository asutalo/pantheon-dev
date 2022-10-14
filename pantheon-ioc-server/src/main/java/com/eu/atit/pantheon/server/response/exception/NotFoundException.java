package com.eu.atit.pantheon.server.response.exception;

public class NotFoundException extends IocServerException {
    public static final Integer NOT_FOUND_STATUS = 404;
    public static final String NOT_FOUND_MESSAGE = "Not Found";

    public NotFoundException() {
        super(NOT_FOUND_STATUS, NOT_FOUND_MESSAGE);
    }
}
