package com.eu.atit.pantheon.server.exceptions;

public class UnauthorizedException extends PantheonServerException {
    public static final Integer UNAUTHORIZED_ERROR_STATUS = 401;
    public static final String UNAUTHORIZED_ERROR_MESSAGE = "Unauthorized";

    public UnauthorizedException() {
        super(UNAUTHORIZED_ERROR_STATUS, UNAUTHORIZED_ERROR_MESSAGE);
    }
}
