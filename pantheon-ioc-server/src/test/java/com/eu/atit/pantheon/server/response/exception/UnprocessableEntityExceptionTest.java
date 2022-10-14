package com.eu.atit.pantheon.server.response.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnprocessableEntityExceptionTest {
    @Test
    void shouldHaveCorrectStatusAndMessage() {
        int expectedStatus = 422;
        String expectedMessage = "Unprocessable Entity";
        UnprocessableEntityException unprocessableEntityException = new UnprocessableEntityException();

        assertEquals(expectedStatus, unprocessableEntityException.getStatusCode());
        assertEquals(expectedMessage, unprocessableEntityException.getMessage());
    }
}