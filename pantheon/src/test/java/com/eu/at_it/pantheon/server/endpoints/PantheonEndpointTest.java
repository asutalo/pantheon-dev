package com.eu.at_it.pantheon.server.endpoints;

import com.eu.at_it.pantheon.server.exceptions.NotImplementedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class PantheonEndpointTest {

    @Test
    void actionsThrowNotImplementedExceptionByDefault() {
        PantheonEndpoint endpoint = new TestImpl();

        Assertions.assertThrows(NotImplementedException.class, () -> endpoint.head(Map.of(), Map.of(), null));
        Assertions.assertThrows(NotImplementedException.class, () -> endpoint.put(Map.of(), Map.of(), null));
        Assertions.assertThrows(NotImplementedException.class, () -> endpoint.post(Map.of(), Map.of(), null));
        Assertions.assertThrows(NotImplementedException.class, () -> endpoint.delete(Map.of(), Map.of(), null));
        Assertions.assertThrows(NotImplementedException.class, () -> endpoint.patch(Map.of(), Map.of(), null));
        Assertions.assertThrows(NotImplementedException.class, () -> endpoint.get(Map.of(), Map.of(), null));
    }

    private static class TestImpl implements PantheonEndpoint {
    }

}