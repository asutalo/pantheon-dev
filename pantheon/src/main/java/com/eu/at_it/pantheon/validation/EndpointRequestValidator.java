package com.eu.at_it.pantheon.validation;

import com.eu.at_it.pantheon.helper.EndpointInput;
import com.eu.at_it.pantheon.server.endpoints.PantheonEndpoint;
import com.eu.at_it.pantheon.server.exceptions.UnauthorizedException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class EndpointRequestValidator implements InvocationHandler {

    private final PantheonEndpoint endpoint;
    private final Map<String, EndpointValidator> methodsAndValidators;

    public EndpointRequestValidator(PantheonEndpoint endpoint, Map<String, EndpointValidator> methodsAndValidators) {
        this.endpoint = endpoint;
        this.methodsAndValidators = methodsAndValidators;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        EndpointValidator endpointValidator = methodsAndValidators.get(method.getName());
        if (endpointValidator != null) {
            if (!endpointValidator.test(new EndpointInput(args))) {
                throw new UnauthorizedException();
            }
        }
        return method.invoke(endpoint, args);
    }
}