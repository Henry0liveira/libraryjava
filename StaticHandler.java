package biblioteca.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticHandler implements HttpHandler {

    private final String staticDir;

    public StaticHandler(String staticDir) {
        this.staticDir = staticDir;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String reqPath = exchange.getRequestURI().getPath();
        if (reqPath.equals("/") || reqPath.isEmpty()) reqPath = "/index.html";

        Path filePath = Paths.get(staticDir + reqPath);
        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
            byte[] bytes = Files.readAllBytes(filePath);
            String contentType = getContentType(reqPath);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        } else {
            // Serve index.html for SPA routing
            Path index = Paths.get(staticDir + "/index.html");
            if (Files.exists(index)) {
                byte[] bytes = Files.readAllBytes(index);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
            } else {
                exchange.sendResponseHeaders(404, 0);
                exchange.getResponseBody().close();
            }
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css"))  return "text/css; charset=UTF-8";
        if (path.endsWith(".js"))   return "application/javascript; charset=UTF-8";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".ico"))  return "image/x-icon";
        return "application/octet-stream";
    }
}
