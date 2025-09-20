package com.eu.atit.pantheon.file.response;

import com.eu.atit.pantheon.server.endpoints.BaseResponse;

import java.util.List;
import java.util.Map;

public class FileResponse extends BaseResponse {
    public static class MimeTypes {
        public static final String PNG = "image/png";
        public static final String JPEG = "image/jpeg";
        public static final String GIF = "image/gif";
        public static final String BMP = "image/bmp";
        public static final String WEBP = "image/webp";
        public static final String SVG = "image/svg+xml";

        public static final String PDF = "application/pdf";
        public static final String MS_WORD = "application/msword";
        public static final String MS_WORDX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        public static final String MS_EXCEL = "application/vnd.ms-excel";
        public static final String MS_EXCELX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        public static final String MS_POWERPOINT = "application/vnd.ms-powerpoint";
        public static final String MS_POWERPOINTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";

        public static final String TEXT = "text/plain";
        public static final String HTML = "text/html";
        public static final String CSS = "text/css";
        public static final String JAVASCRIPT = "application/javascript";

        public static final String ZIP = "application/zip";
        public static final String TAR = "application/x-tar";
        public static final String GZIP = "application/gzip";
        public static final String OCTET_STREAM = "application/octet-stream";

        public static final String MP3 = "audio/mpeg";
        public static final String WAV = "audio/wav";

        public static final String MP4 = "video/mp4";
        public static final String AVI = "video/x-msvideo";
    }

    private final Map<String, List<String>> baseHeaders;
    public FileResponse(int statusCode, byte[] body, String contentType) {
        super(statusCode, Map.of(), body);
        this.baseHeaders = Map.of("Content-Type", List.of(contentType));
    }

    public FileResponse(int statusCode, Map<String, List<String>> headers, byte[] body, String contentType) {
        super(statusCode, headers, body);
        this.baseHeaders = Map.of("Content-Type", List.of(contentType));
    }

    @Override
    public Map<String, List<String>> baseHeaders() {
        return baseHeaders;
    }
}
