package com.eu.atit.pantheon.server.endpoint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Map;

class EndpointProxyTest {
    @Test
    void getProxyFor_shouldReturnProxyObject() {
        IoCEndpoint proxy = new EndpointProxy().getProxyFor(new Endpoint("") {
            @Override
            public String uriDefinition() {
                return super.uriDefinition();
            }
        }, Map.of());

        Assertions.assertTrue(proxy instanceof Proxy);
    }
}