package com.eu.atit.pantheon.server.response.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InternalServerErrorExceptionTest {

    @Test
    void shouldHaveCorrectStatusAndMessage() {
        int expectedStatus = 500;
        String expectedMessage = "Internal Server Error";
        InternalServerErrorException internalServerErrorException = new InternalServerErrorException();

        Assertions.assertEquals(expectedStatus, internalServerErrorException.getStatusCode());
        Assertions.assertEquals(expectedMessage, internalServerErrorException.getMessage());
    }
}