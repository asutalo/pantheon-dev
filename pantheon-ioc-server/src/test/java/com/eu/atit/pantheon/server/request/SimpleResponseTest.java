package com.eu.atit.pantheon.server.request;

import com.eu.atit.pantheon.server.response.SimpleResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SimpleResponseTest {
    static final int SOME_STATUS_CODE = 1;
    static final String SOME_MESSAGE = "msg";

    @Test
    void getStatusCode() {
        Assertions.assertEquals(SOME_STATUS_CODE, new SimpleResponse(SOME_STATUS_CODE, SOME_MESSAGE).getStatusCode());
    }

    @Test
    void getMessage() {
        Assertions.assertEquals(SOME_MESSAGE, new SimpleResponse(SOME_STATUS_CODE, SOME_MESSAGE).getMessage());
    }
}