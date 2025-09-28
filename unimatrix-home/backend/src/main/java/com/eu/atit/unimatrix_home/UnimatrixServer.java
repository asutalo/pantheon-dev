package com.eu.atit.unimatrix_home;

import com.eu.atit.pantheon.file.endpoint.FileEndpoint;
import com.eu.atit.pantheon.file.endpoint.SingleFileEndpoint;
import com.eu.atit.pantheon.file.response.FileResponse;
import com.eu.atit.pantheon.server.Server;
import com.eu.atit.pantheon.server.endpoint.Endpoint;
import com.sun.net.httpserver.Headers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.eu.atit.pantheon.file.response.FileResponse.MimeTypes.*;

public class UnimatrixServer {
    public static void main(String[] args) throws IOException {
//        String basePath = args[0];
        String basePath = "/home/nattefrost/Projects/pantheon-dev/unimatrix-home/frontend/";
        String indexPath = (basePath + "/pages/homepage/index.html").replace("//", "/");
        Server server = new Server(8080, 60, 20, 10, TimeUnit.SECONDS, true);
        server.registerEndpoint(new SingleFileEndpoint("/", indexPath, HTML)); //index
        server.registerEndpoint(new FileEndpoint("/pages", basePath + "pages", HTML));
        server.registerEndpoint(new FileEndpoint("/css", basePath + "css", CSS));
        server.registerEndpoint(new FileEndpoint("/script", basePath + "script", JAVASCRIPT));
        server.registerEndpoint(new FileEndpoint("/img", basePath + "img", JPEG));
        server.registerEndpoint(new FileEndpoint("/info", basePath + "info", JSON));
        server.registerEndpoint(new Endpoint("/imginfo"){
            @Override
            public FileResponse get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
                Path folder = Path.of(basePath + "img/gallery");

                try {
                    return new FileResponse(200, photosAndIntervalJson(folder).getBytes(StandardCharsets.UTF_8), JSON);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public static String photosAndIntervalJson(Path folder) throws IOException {
                String staticPrefix = "../../img/gallery/";
                List<String> photos = new ArrayList<>();
                int interval = 60; // Default interval

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
        });
        server.start();
    }
}
