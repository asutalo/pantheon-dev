package com.eu.atit.pantheon.server.response.exception;

public class UnprocessableEntityException extends IocServerException {
    public static final Integer UNPROCESSABLE_ENTITY_STATUS = 422;
    public static final String UNPROCESSABLE_ENTITY_MESSAGE = "Unprocessable Entity";

    public UnprocessableEntityException() {
        super(UNPROCESSABLE_ENTITY_STATUS, UNPROCESSABLE_ENTITY_MESSAGE);
    }
}
