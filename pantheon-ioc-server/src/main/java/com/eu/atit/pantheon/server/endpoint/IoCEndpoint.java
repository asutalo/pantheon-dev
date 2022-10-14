package com.eu.atit.pantheon.server.endpoint;

import com.eu.atit.pantheon.server.endpoints.PantheonEndpoint;
import com.eu.atit.pantheon.server.request.parsing.Parser;

import java.util.List;

public interface IoCEndpoint extends PantheonEndpoint {

    List<Parser> uriParsers();

    boolean match(String uriString);

    String parsedUriDefinition();

    String uriDefinition();

}
