package com.eu.atit.pantheon.server.request.validation;

import com.eu.atit.pantheon.server.response.exception.NotFoundException;

interface Validation {
    String VALID_URI_PATTERN = "^(/)([a-zA-Z/0-9]*(\\?)?(?!/\\?)[a-zA-Z0-9=&%+.\\-]+)?";

    void validateUri(String decodedUriString);
}

/**
 * Validator hardcoded to identify if the incoming URI is RFC 3986 friendly
 */
public class Validator implements Validation {
    public void validateUri(String decodedUriString) {
        if (!decodedUriString.matches(VALID_URI_PATTERN)) {
            throw new NotFoundException();
        }
    }
}
