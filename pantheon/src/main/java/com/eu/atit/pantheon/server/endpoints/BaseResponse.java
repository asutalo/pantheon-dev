package com.eu.atit.pantheon.server.endpoints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseResponse implements Response {
    private final Map<String, List<String>> headers;
    private final int statusCode;
    private final byte[] body;

    protected BaseResponse(int statusCode, Map<String, List<String>> headers, byte[] body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public abstract Map<String, List<String>> baseHeaders();

    @Override
    public Map<String, List<String>> getHeaders() {
        Map<String, List<String>> combined = new HashMap<>(headers);
        combined.putAll(baseHeaders());
        return combined;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public byte[] getBody() {
        return body;
    }
}