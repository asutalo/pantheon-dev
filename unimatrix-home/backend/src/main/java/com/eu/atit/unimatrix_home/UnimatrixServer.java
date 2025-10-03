package com.eu.atit.unimatrix_home;

import com.eu.atit.pantheon.file.endpoint.FileEndpoint;
import com.eu.atit.pantheon.file.endpoint.MimeLessFileEndpoint;
import com.eu.atit.pantheon.file.endpoint.SingleFileEndpoint;
import com.eu.atit.pantheon.server.Server;
import com.eu.atit.unimatrix_home.endpoint.GalleryScannerEndpoint;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.eu.atit.pantheon.file.response.FileResponse.MimeTypes.*;

public class UnimatrixServer {
    public static void main(String[] args) throws IOException {
        String basePath = args[0];
        int port = Integer.parseInt(args[1]);
//        String basePath = "/home/nattefrost/Projects/pantheon-dev/unimatrix-home/frontend/";
        String indexPath = (basePath + "/pages/homepage/index.html").replace("//", "/");
        Server server = new Server(port, 60, 20, 10, TimeUnit.SECONDS, true);
        server.registerEndpoint(new SingleFileEndpoint("/", indexPath, HTML)); //index
        server.registerEndpoint(new FileEndpoint("/pages", basePath + "pages", HTML));
        server.registerEndpoint(new FileEndpoint("/css", basePath + "css", CSS));
        server.registerEndpoint(new FileEndpoint("/script", basePath + "script", JAVASCRIPT));
        server.registerEndpoint(new MimeLessFileEndpoint("/img", basePath + "img"));
        server.registerEndpoint(new FileEndpoint("/info", basePath + "info", JSON));
        server.registerEndpoint(new GalleryScannerEndpoint("/info/gallery/photos", basePath + "img/gallery", "../../img/gallery/", 60));
        server.start();
    }
}
