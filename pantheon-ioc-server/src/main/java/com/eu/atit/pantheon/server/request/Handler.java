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

//        todo refactor response.getBody() -> byte[] => InputStream to enable streaming of all the responses
//        use body length if known, i.e. dealing with a file or a pre-generated response
//        If you build the entire JSON in memory first (e.g., use Jackson/Gson to serialize your List to String or byte[]
//        before responding): Yes, you know the size. You can measure the resulting byte[] length (for UTF-8 JSON),
//        set Content-Length, and send it. Downside: This can use a lot of memory if the response (object graph) is large.
//        If you want to stream/serialize each object directly to the output as you fetch/process it (e.g., for large datasets):
//        No, you do NOT know the size up front. Example: Using a streaming JSON generator to write each record to the
//        output stream (“Row 1, then row 2,...”) as it's fetched from the DB. You cannot know the final byte count without either:
//        Buffering everything until the end, then sending (see above), or Writing as you go (streaming), which means you must use chunked encoding.
//        Summary Table Method Know Size Up Front? Can Use Content-Length? Memory Usage Serialize all -> String (then send) Yes Yes High (full copy)
//        Stream JSON out as you fetch/process data No No (use chunked) Low Key Point If you collect ALL data and serialize
//        to a string or byte array BEFORE responding, you know the size. If you stream/generate output as you go, you generally do NOT know the size and should stream/chunk.
//        The choice is a tradeoff: Small/medium data: Buffer, then respond with Content-Length. Large/unknown data: Stream, use chunked encoding, unknown Content-Length.
//        In summary: You only know the response size up front if you buffer/generate the complete response before starting to send it.
//        httpExchange.sendResponseHeaders(response.getStatusCode(), 0);
//        Files.newInputStream(Path.of("s")).transferTo(outputStream);
//        todo look at SelectQueryResultProcessor for further refactoring to stream output from DB
    }
}
