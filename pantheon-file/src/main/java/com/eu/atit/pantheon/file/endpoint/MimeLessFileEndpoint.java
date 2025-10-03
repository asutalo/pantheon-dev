package com.eu.atit.pantheon.file.endpoint;

import com.eu.atit.pantheon.file.response.FileResponse;
import com.eu.atit.pantheon.server.endpoint.Endpoint;
import com.sun.net.httpserver.Headers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.eu.atit.pantheon.server.request.parsing.ParsingService.STAR;

// Convenience class for returning files without a predefined mime type
public class MimeLessFileEndpoint extends FileEndpoint{
    public MimeLessFileEndpoint(String baseUri, String basePath) {
        super(baseUri, basePath, "unused");
    }
    
    @Override
    public FileResponse get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        try {
            Path filePath = resolve(uriParams, filePathKey);

            return new FileResponse(200, readFile(filePath), Files.probeContentType(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
