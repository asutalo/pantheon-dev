package com.eu.atit.pantheon.server.endpoint;

import com.eu.atit.pantheon.server.response.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registry {
    private final Map<String, IoCEndpoint> endpoints = new HashMap<>();

    private final List<IoCEndpoint> endpointsList = new ArrayList<>();

    public void registerEndpoint(IoCEndpoint endpoint) {
        String parsedUriDefinition = endpoint.parsedUriDefinition();

        if (!endpoints.containsKey(parsedUriDefinition)) {

            endpoints.put(parsedUriDefinition, endpoint);

            endpointsList.clear();
            endpointsList.addAll(endpoints.values());
        } else {
            throw new IllegalStateException("Endpoint already registered: " + endpoint.uriDefinition());
        }
    }

    public IoCEndpoint getEndpoint(String uriString) {
        for (IoCEndpoint ioCEndpoint : endpointsList) {
            if (ioCEndpoint.match(uriString)) {
                return ioCEndpoint;
            }
        }

        throw new NotFoundException();
    }

    //for tests
    public Map<String, IoCEndpoint> getEndpoints() {
        return endpoints;
    }

    //for tests
    public void clearEndpoints(){
        endpoints.clear();
    }
}
