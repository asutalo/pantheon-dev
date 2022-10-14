package com.eu.atit.pantheon.server.endpoint;

import com.eu.atit.pantheon.validation.EndpointRequestValidator;
import com.eu.atit.pantheon.validation.EndpointValidator;

import java.lang.reflect.Proxy;
import java.util.Map;

public class EndpointProxy {
    public <T extends IoCEndpoint> T getProxyFor(T endpoint, Map<String, EndpointValidator> endpointMethodsAndValidators) {
        return (T) Proxy.newProxyInstance(endpoint.getClass().getClassLoader(),
                new Class[]{IoCEndpoint.class}, new EndpointRequestValidator(endpoint, endpointMethodsAndValidators));
    }
}
