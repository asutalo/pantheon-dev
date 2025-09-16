package com.eu.atit.pantheon.server.endpoints.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ResponseHeaders {
    private ResponseHeaders() {} // Utility class

    /**
     * Combine base headers and extra headers into a single map.
     * Extra headers will override base headers if there are duplicate keys.
     */
    public static Map<String, List<String>> combine(
            Map<String, List<String>> baseHeaders,
            Map<String, List<String>> extraHeaders
    ) {
        // Start with a copy of the base headers
        Map<String, List<String>> combined = new HashMap<>(baseHeaders);
        // Add or override with extra headers
        if (extraHeaders != null) {
            combined.putAll(extraHeaders);
        }
        return combined;
    }
}

