package com.eu.atit.pantheon.file.endpoint;

import com.eu.atit.pantheon.file.response.FileResponse;
import com.eu.atit.pantheon.server.endpoint.Endpoint;
import com.sun.net.httpserver.Headers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.eu.atit.pantheon.server.request.parsing.ParsingService.STAR;

// Convenience class for a basic File Endpoint, serving files based on relative path from path params and single mime type
public class FileEndpoint extends Endpoint{
    private final Path basePath;
    private final String mimeType;
    static final String filePathKey = "filePath";
    public static final String filePathRegex = "(?:" + filePathKey + "=" + STAR + ")";
    public FileEndpoint(String baseUri, String basePath, String mimeType) {
        super(uriDefinition(baseUri));
        this.basePath = Path.of(endWIthSlash(basePath));
        this.mimeType = mimeType;
    }
    
    private static String uriDefinition(String baseUri) {
        return endWIthSlash(baseUri) + filePathRegex;
    }

    private static String endWIthSlash(String s) {
        return (s.endsWith("/") ? s : s + "/");
    }

    @Override
    public FileResponse get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        try {
            return new FileResponse(200, readFile(uriParams.get(filePathKey).toString()), mimeType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    byte[] readFile(String filePath) throws IOException {
        return Files.readAllBytes(basePath.resolve(filePath));
    }
}
