package com.eu.at_it.pantheon.helper;

import com.sun.net.httpserver.Headers;

import java.util.Arrays;
import java.util.Map;

public class EndpointInput {
    private final Object[] args;

    public EndpointInput(Object[] args) {
        this.args = args;
    }

    public Map<String, Object> getUriParams() {
        return (Map<String, Object>) args[0];
    }

    public Map<String, Object> getRequestBody() {
        return (Map<String, Object>) args[1];
    }

    public Headers getRequestHeaders() {
        return (Headers) args[2];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndpointInput that = (EndpointInput) o;
        return Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(args);
    }
}
