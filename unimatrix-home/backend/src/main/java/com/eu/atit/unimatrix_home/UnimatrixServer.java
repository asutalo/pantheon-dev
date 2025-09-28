package com.eu.atit.unimatrix_home;

import com.eu.atit.pantheon.file.endpoint.FileEndpoint;
import com.eu.atit.pantheon.server.Server;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.eu.atit.pantheon.file.response.FileResponse.MimeTypes.PNG;

public class UnimatrixServer {
    public static void main(String[] args) throws IOException {
        FileEndpoint fileEndpoint = new FileEndpoint("/file", "/", PNG);
        Server server = new Server(8080, 60, 20, 10, TimeUnit.SECONDS, true);
        server.registerEndpoint(fileEndpoint);
        server.start();
    }
}
