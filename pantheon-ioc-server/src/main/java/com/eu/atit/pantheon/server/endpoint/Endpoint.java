package com.eu.atit.pantheon.server.endpoint;

import com.sun.net.httpserver.Headers;

import java.util.Map;

import static com.eu.atit.pantheon.server.request.parsing.ParsingService.getInstance;

/**
 * Public provider of the {@link RequestEndpoint}, should be extended in custom endpoint definitions
 * When extending make sure to override the HTTP verb methods as needed
 * i.e. {@link #get(Map, Map, Headers)}, {@link #post(Map, Map, Headers)}, etc.
 */
public abstract class Endpoint extends RequestEndpoint {
    private final String uriDefinition;

    public Endpoint(String uriDefinition) {
        super(getInstance().parseUriDefinition(uriDefinition), getInstance().getUriParsers(uriDefinition));
        this.uriDefinition = uriDefinition;
    }

    public String uriDefinition() {
        return uriDefinition;
    }
}
