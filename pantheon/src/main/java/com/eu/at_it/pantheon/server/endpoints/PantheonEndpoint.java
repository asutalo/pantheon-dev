package com.eu.at_it.pantheon.server.endpoints;

import com.eu.at_it.pantheon.server.exceptions.NotImplementedException;
import com.sun.net.httpserver.Headers;

import java.util.Map;

public interface PantheonEndpoint {
    default Response head(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        throw new NotImplementedException();
    }

    default Response get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        throw new NotImplementedException();
    }

    default Response put(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        throw new NotImplementedException();
    }

    default Response post(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        throw new NotImplementedException();
    }

    default Response patch(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        throw new NotImplementedException();
    }

    default Response delete(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        throw new NotImplementedException();
    }
}
