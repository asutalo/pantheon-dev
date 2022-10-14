package com.eu.at_it.pantheon.server.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UnauthorizedExceptionTest {
    @Test
    void shouldHaveCorrectStatusAndMessage() {
        int expectedStatus = 401;
        String expectedMessage = "Unauthorized";
        UnauthorizedException unauthorizedException = new UnauthorizedException();

        Assertions.assertEquals(expectedStatus, unauthorizedException.getStatusCode());
        Assertions.assertEquals(expectedMessage, unauthorizedException.getMessage());
    }
}