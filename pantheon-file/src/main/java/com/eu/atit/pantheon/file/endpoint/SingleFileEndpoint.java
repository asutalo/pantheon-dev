package com.eu.atit.pantheon.file.endpoint;

import com.eu.atit.pantheon.file.response.FileResponse;
import com.eu.atit.pantheon.server.endpoint.Endpoint;
import com.sun.net.httpserver.Headers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

// Convenience class for a basic File Endpoint, serving specific files with single mime type
public class SingleFileEndpoint extends Endpoint{
    private final Path filePath;
    private final String mimeType;
    public SingleFileEndpoint(String uri, String filePath, String mimeType) {
        super(uri);
        this.filePath = Path.of(filePath);
        this.mimeType = mimeType;
    }
    
    @Override
    public FileResponse get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        try {
            return new FileResponse(200, readFile(filePath), mimeType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    byte[] readFile(Path filePath) throws IOException {
        return Files.readAllBytes(filePath);
    }
}
