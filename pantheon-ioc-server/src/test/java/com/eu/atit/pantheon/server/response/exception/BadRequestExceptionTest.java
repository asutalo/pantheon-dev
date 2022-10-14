package com.eu.atit.pantheon.server.response.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BadRequestExceptionTest {
    @Test
    void shouldHaveCorrectStatusAndMessage() {
        int expectedStatus = 400;
        String expectedMessage = "Bad Request";
        BadRequestException badRequestException = new BadRequestException();

        Assertions.assertEquals(expectedStatus, badRequestException.getStatusCode());
        Assertions.assertEquals(expectedMessage, badRequestException.getMessage());
    }
}