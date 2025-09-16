package com.eu.atit.pantheon.server.request;

import com.eu.atit.pantheon.server.endpoint.EndpointProcessor;
import com.eu.atit.pantheon.server.endpoint.IoCEndpoint;
import com.eu.atit.pantheon.server.endpoint.Registry;
import com.eu.atit.pantheon.server.endpoints.Response;
import com.eu.atit.pantheon.server.request.parsing.ParsingService;
import com.eu.atit.pantheon.server.request.validation.Validator;
import com.eu.atit.pantheon.server.response.exception.InternalServerErrorException;
import com.eu.atit.pantheon.server.response.exception.IocServerException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Handler implements HttpHandler {
    private final Validator validator;
    private final ParsingService parsingService;
    private final Registry registry;
    private final EndpointProcessor endpointProcessor;

    public Handler(Validator validator, ParsingService parsingService, Registry registry, EndpointProcessor endpointProcessor) {
        this.validator = validator;
        this.parsingService = parsingService;
        this.registry = registry;
        this.endpointProcessor = endpointProcessor;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String decodedUriString = parsingService.decodeUri(httpExchange);
            validator.validateUri(decodedUriString);
            IoCEndpoint endpoint = registry.getEndpoint(decodedUriString);
            Response response = endpointProcessor.process(endpoint, parsingService, decodedUriString, httpExchange);
            respond(httpExchange, response);
        } catch (IocServerException iocServerException) {
            respond(httpExchange, iocServerException);
        } catch (Exception e) {
            //todo log exception
            respond(httpExchange, new InternalServerErrorException());
        }
    }

    private void respond(HttpExchange httpExchange, Response response) throws IOException {
        httpExchange.getResponseHeaders().putAll(response.getHeaders());

        OutputStream outputStream = httpExchange.getResponseBody();
        byte[] body = response.getBody();
        httpExchange.sendResponseHeaders(response.getStatusCode(), body.length);
        outputStream.write(body);
        outputStream.flush();
        outputStream.close();
    }
}
