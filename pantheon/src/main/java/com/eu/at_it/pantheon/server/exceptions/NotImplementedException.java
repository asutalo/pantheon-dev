package com.eu.at_it.pantheon.server.exceptions;

public class NotImplementedException extends PantheonServerException {
    public static final Integer NOT_IMPLEMENTED_ERROR_STATUS = 501;
    public static final String NOT_IMPLEMENTED_ERROR_MESSAGE = "Not Implemented";

    public NotImplementedException() {
        super(NOT_IMPLEMENTED_ERROR_STATUS, NOT_IMPLEMENTED_ERROR_MESSAGE);
    }
}
