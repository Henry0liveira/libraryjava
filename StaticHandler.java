package biblioteca.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Serve arquivos estáticos a partir de um diretório especificado.
 */
public class StaticHandler implements HttpHandler {

    private final String staticDir;

    public StaticHandler(String staticDir) {
        this.staticDir = staticDir != null ? staticDir : "src/main/resources/static";
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String reqPath = exchange.getRequestURI().getPath();
        if (reqPath.equals("/") || reqPath.isEmpty()) {
            reqPath = "/index.html";
        }

        // Constrói o caminho do arquivo
        Path filePath = Paths.get(staticDir).resolve(reqPath.substring(1));

        // Valida se o arquivo está dentro do diretório estático (previne path traversal)
        Path baseDir = Paths.get(staticDir).toRealPath();
        Path resolvedPath = filePath.toRealPath();
        if (!resolvedPath.startsWith(baseDir)) {
            exchange.sendResponseHeaders(403, 0);
            exchange.getResponseBody().close();
            return;
        }

        // Se for um diretório, tenta servir index.html
        if (Files.isDirectory(resolvedPath)) {
            resolvedPath = resolvedPath.resolve("index.html");
        }

        // Verifica se o arquivo existe
        if (!Files.exists(resolvedPath)) {
            // Fallback para SPA: serve index.html
            resolvedPath = Paths.get(staticDir).resolve("index.html");
            if (!Files.exists(resolvedPath)) {
                exchange.sendResponseHeaders(404, 0);
                exchange.getResponseBody().close();
                return;
            }
        }

        byte[] bytes = Files.readAllBytes(resolvedPath);
        exchange.getResponseHeaders().set("Content-Type", getContentType(resolvedPath.toString()));
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css"))  return "text/css; charset=UTF-8";
        if (path.endsWith(".js"))   return "application/javascript; charset=UTF-8";
        if (path.endsWith(".json")) return "application/json; charset=UTF-8";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".svg"))  return "image/svg+xml";
        if (path.endsWith(".ico"))  return "image/x-icon";
        return "application/octet-stream";
    }
}
