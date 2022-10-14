package com.eu.atit.pantheon.server.endpoint;

import com.eu.atit.pantheon.server.request.parsing.Parser;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Main definition of an {@link Endpoint}
 * Provides the plumbing for enabling internal endpoint mapping
 */
abstract class RequestEndpoint implements IoCEndpoint {
    private final String parsedUriDefinition;
    private final List<Parser> uriParsers;
    private final Pattern uriMatchingPattern;

    RequestEndpoint(String parsedUriDefinition, List<Parser> uriParsers) {
        this.uriMatchingPattern = Pattern.compile(parsedUriDefinition);
        this.parsedUriDefinition = parsedUriDefinition;
        this.uriParsers = uriParsers;
    }

    public List<Parser> uriParsers() {
        return uriParsers;
    }

    public boolean match(String uriString) {
        return uriMatchingPattern.matcher(uriString).matches();
    }

    public String parsedUriDefinition() {
        return parsedUriDefinition;
    }
}
