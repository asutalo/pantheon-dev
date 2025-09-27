package com.eu.atit.pantheon.file.endpoint;

import com.eu.atit.pantheon.file.response.FileResponse;
import com.eu.atit.pantheon.server.endpoint.Endpoint;
import com.sun.net.httpserver.Headers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.eu.atit.pantheon.server.request.parsing.ParsingService.EQUALS;
import static com.eu.atit.pantheon.server.request.parsing.ParsingService.STAR;

public class FileEndpoint extends Endpoint{
    private final Path basePath;
    private final String mimeType;
    private static final String filePathKey = "filePath";
    public FileEndpoint(String baseUri, String basePath, String mimeType) {
        super(uriDefinition(baseUri));
        this.basePath = Path.of(basePath);
        this.mimeType = mimeType;
    }
    
    private static String uriDefinition(String baseUri) {
        return (baseUri.endsWith("/") ? baseUri : baseUri + "/") + "(" + filePathKey + EQUALS + STAR +  ")";
    }

    @Override
    public FileResponse get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(basePath.resolve(uriParams.get(filePathKey).toString()));
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        return new FileResponse(200, bytes, mimeType);
    }
}
