package com.eu.atit.unimatrix_home.endpoint;

import com.eu.atit.pantheon.json.response.JsonResponse;
import com.eu.atit.pantheon.server.endpoint.Endpoint;
import com.sun.net.httpserver.Headers;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GalleryScannerEndpoint extends Endpoint {
    private final Path folder;
    final String staticPrefix;
    final int defaultInterval;

    public GalleryScannerEndpoint(String uriDefinition, String basePath, String staticPrefix, int defaultInterval) {
        super(uriDefinition);
        folder = Path.of(basePath);
        this.staticPrefix = staticPrefix;
        this.defaultInterval = defaultInterval;
    }

    @Override
    public JsonResponse get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
        try {
            return new JsonResponse(200, photosAndIntervalJson(folder));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String photosAndIntervalJson(Path folder) throws IOException {
        List<String> photos = new ArrayList<>();
        int interval = defaultInterval;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path p : stream) {
                String name = p.getFileName().toString();
                if (name.equals("interval.txt")) {
                    // Read interval value (should be a number)
                    String txt = Files.readString(p).trim();
                    if (!txt.isEmpty()) {
                        interval = Integer.parseInt(txt);
                    }
                } else {
                    photos.add(staticPrefix + name);
                }
            }
        }

        Collections.sort(photos);

        StringBuilder sb = new StringBuilder("{\n  \"photos\": [");
        sb.append(photos.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")));
        sb.append("],\n  \"interval\": ");
        sb.append(interval);
        sb.append("\n}");

        return sb.toString();
    }
}
