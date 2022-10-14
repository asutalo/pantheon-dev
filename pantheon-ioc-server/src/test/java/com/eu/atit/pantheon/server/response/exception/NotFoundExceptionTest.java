package com.eu.atit.pantheon.server.response.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {
    @Test
    void shouldHaveCorrectStatusAndMessage() {
        int expectedStatus = 404;
        String expectedMessage = "Not Found";
        NotFoundException notFoundException = new NotFoundException();

        Assertions.assertEquals(expectedStatus, notFoundException.getStatusCode());
        Assertions.assertEquals(expectedMessage, notFoundException.getMessage());
    }
}