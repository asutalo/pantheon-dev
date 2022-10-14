package com.eu.atit.pantheon.server.response.exception;

public class InternalServerErrorException extends IocServerException {
    public static final Integer INTERNAL_SERVER_ERROR_STATUS = 500;
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";

    public InternalServerErrorException() {
        super(INTERNAL_SERVER_ERROR_STATUS, INTERNAL_SERVER_ERROR_MESSAGE);
    }
}
