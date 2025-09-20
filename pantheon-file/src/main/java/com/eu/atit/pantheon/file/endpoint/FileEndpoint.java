package com.eu.atit.pantheon.file.endpoint;

import com.eu.atit.pantheon.file.response.FileResponse;
import com.eu.atit.pantheon.server.endpoint.Endpoint;
import com.sun.net.httpserver.Headers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class FileEndpoint extends Endpoint{
    private final Path basePath;
    private final String mimeType;
    public FileEndpoint(String uriDefinition, String basePath, String mimeType) {
        super(uriDefinition);
        this.basePath = Path.of(basePath);
        this.mimeType = mimeType;
    }

    @Override
    public FileResponse get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(basePath.resolve("disk3/Antonio mob 9 September 2025/Pictures/file_00000000c7a8620a963bc6f168fda0ec.png"));
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        return new FileResponse(200, bytes, mimeType);
    }
}
